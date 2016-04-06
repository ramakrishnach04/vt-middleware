

# Windows Active Directory #
Active Directory provides an LDAPv3 compliant interface for performing operations. However, users should be aware of several pitfalls that are unique to Active Directory. This document attempts to explain these pitfalls and how to overcome them when using the vt-ldap library.

## Referrals ##
JNDI provides three ways in which to handle LDAP referrals: 'ignore', 'follow', and 'throw'. The way in which you configure this setting will determine how your search operations behave. Note that Active Directory will typically use referrals for any search bases that access domain DNS objects at the root level. e.g. dc=mydomain,dc=org.

### Ignoring Referrals ###
Set the property: _edu.vt.middleware.ldap.referral=ignore_<br />
The is the default setting, if you don't specify a value this is the behavior you will experience. Using this value causes JNDI to invoke the [ManageDsaIT](http://www.faqs.org/rfcs/rfc3296.html) LDAP control which provides a mechanism to tell the LDAP server to return referral entries as ordinary entries. Active Directory does not support this control which results in a `javax.naming.PartialResultException: Unprocessed Continuation Reference(s)` when the search results are read by the client if referrals were encountered. (Note that all other settings will not send the `Manage Referral` control.)<br /><br />

  * to ignore the partial result exceptions you can set:
```
edu.vt.middleware.ldap.handlerIgnoreExceptions=javax.naming.LimitExceededException,javax.naming.PartialResultException
```
This will will instruct the search result handler to ignore exceptions of type `LimitExceededException` and `PartialResultException`.<br />
However, this solution is **not** recommend as referrals and entries can be returned in any order and you may miss entries found after a referral.

### Following Referrals ###
Set the property: _edu.vt.middleware.ldap.referral=follow_<br />
If you are confident that all the referrals returned by the Active Directory can be followed you can use this setting. Note that referrals often contain hostnames other than the server that is being searched. The authentication credentials for the original connection must be valid for any hosts supplied by the referrals. In addition, these hostnames must be DNS resolvable and reachable in order for the search to be successful. `javax.naming.CommunicationException` and `java.net.UnknownHostException` are commonly encountered when following referrals.<br /><br />

### Throwing Referrals ###
Set the property: _edu.vt.middleware.ldap.referral=throw_<br />
Using this option will result in a `com.sun.jndi.ldap.LdapReferralException: Continuation Reference` being thrown when the search results are read. However, using this option guarantees that all entries will be read before the first referral is encountered. This option exists to allow you to decide whether to follow or ignore each referral and you can do so in several ways.<br /><br />

  * to ignore the referral exceptions you can set:
```
edu.vt.middleware.ldap.handlerIgnoreExceptions=javax.naming.LimitExceededException,javax.naming.ReferralException
```
This will will instruct the search result handler to ignore exceptions of type `LimitExceededException` and `ReferralException`.<br /> Since all the entries will be read before the first referral is encountered, this is an easy mechanism for ignoring referrals.<br /><br />

If you would like to process each result separately and make custom decisions, you will need to implement a custom [search result handler](vtldapSearching#Search_Result_Handlers.md).<br /><br />

### Global Catalog ###
The Global Catalog enables searching for Active Directory objects in any domain in the forest without the need for subordinate referrals.
Because the Global Catalog contains only a subset of the attributes of an object, this solution is viable only if the attributes requested for the search results are stored in the Global Catalog. (Note the GC is accessible on port 3268/3269, not the standard LDAP ports of 389/636.)

### Conclusions ###
  * If you must follow referrals use 'follow' and work with your Active Directory administrator to ensure you have the appropriate access to follow all referrals.
  * If you can ignore referrals use 'throw' and set handlerIgnoreExceptions appropriately.
  * If you don't want to deal with referrals and your Global Catalog contains every attribute you need, use it.

### Useful Links ###
  * [Referrals in JNDI](http://java.sun.com/products/jndi/tutorial/ldap/referral/jndi.html)
  * [Manually Following Referrals](http://java.sun.com/products/jndi/tutorial/ldap/referral/throw.html)

## Binary Attributes ##
Some attributes in the Active Directory may be binary and need to be declared as such when they are retrieved.<br /><br />

To work around this issue you can set:
```
edu.vt.middleware.ldap.binaryAttributes=objectSid objectGUID
```
This will allow you to properly retrieve these attributes as byte arrays.

## Range Attributes ##
Active Directory may not return all the values of an attribute, electing instead to provide the client with a range of attribute values. This practice is documented in an expired RFC: [Incremental Retrieval of Multi-valued Properties](http://www.tkk.fi/cc/docs/kerberos/draft-kashi-incremental-00.txt). For instance, requests for the `member` attribute may return a result like: `member;Range=0-1000`. The client is then expected to request additional attribute values of the form `member;Range=1001-2000` and so forth until all values have been retrieved.

### RangleSearchResultHandler ###
A [search result handler](vtldapSearching#Search_Result_Handlers.md) can be used to process these attribute values.
```
package edu.internet2.middleware.ldappc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchResult;

import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.handler.CopySearchResultHandler;
import edu.vt.middleware.ldap.handler.ExtendedSearchResultHandler;
import edu.vt.middleware.ldap.handler.SearchCriteria;

/**
 * The <code>RangeSearchResultHandler</code> rewrites attributes returned from Active
 * Directory to include all values by performing additional searches. This behavior is
 * based on the expired RFC "Incremental Retrieval of Multi-valued Properties"
 * http://www.ietf.org/proceedings/53/I-D/draft-kashi-incremental-00.txt.
 * 
 * For example, when the membership of a group exceeds 1500, requests for the member
 * attribute will likely return an attribute with name "member;Range=0-1499" and 1500
 * values. For a group with just over 3000 members, subsequent searches will request
 * "member;Range=1500-2999" and then "member;Range=3000-4499". When the returned attribute
 * is of the form "member;Range=3000-*", all values have been retrieved.
 */
public class RangeSearchResultHandler extends CopySearchResultHandler implements ExtendedSearchResultHandler {

  /** The character indicating that the end of the range has been reached. */
  public static final String END_OF_RANGE = "*";

  /** The format used to calculate attribute IDs for subsequent searches. */
  public static final String RANGE_FORMAT = "%1$s;Range=%2$s-%3$s";

  /** The expression matching the range attribute ID "<id>;range=<X>-<Y>". */
  public static final String RANGE_PATTERN_STRING = "^(.*?);Range=([\\d\\*]+)-([\\d\\*]+)";

  /** The pattern matching the range attribute ID. */
  public static final Pattern RANGE_PATTERN = Pattern.compile(RANGE_PATTERN_STRING, Pattern.CASE_INSENSITIVE);

  /** Ldap object for searching. */
  private Ldap ldap;

  /**
   * Creates a new <code>RangeSearchResultHandler</code>.
   */
  public RangeSearchResultHandler() {
  }

  /**
   * Creates a new <code>RangeSearchResultHandler</code> with the supplied ldap.
   * 
   * @param ldap
   *          <code>Ldap</code>
   */
  public RangeSearchResultHandler(final Ldap ldap) {
    this.ldap = ldap;
  }

  /** {@inheritDoc} */
  public Ldap getSearchResultLdap() {
    return this.ldap;
  }

  /** {@inheritDoc} */
  public void setSearchResultLdap(Ldap l) {
    this.ldap = l;
  }

  /** {@inheritDoc} */
  protected Attributes processAttributes(final SearchCriteria sc, final SearchResult sr)
      throws NamingException {

    // get all attributes in the search result
    Attributes attrs = sr.getAttributes();

    // for every attribute in the search result
    NamingEnumeration<? extends Attribute> attrsEnumeration = attrs.getAll();
    while (attrsEnumeration.hasMore()) {
      Attribute attr = attrsEnumeration.next();

      // skip nulls
      if (attr == null) {
        continue;
      }

      // Match attribute ID against the pattern
      Matcher matcher = RANGE_PATTERN.matcher(attr.getID());

      // If the attribute ID matches the pattern
      if (matcher.find()) {

        String msg = "attribute '" + attr.getID() + "' result '" + sr.getName() + "'";

        // Determine the attribute name without the range syntax
        final String attrTypeName = matcher.group(1);
        this.logger.debug("Found Range option " + msg);
        if (attrTypeName == null || attrTypeName.isEmpty()) {
          this.logger.error("Unable to determine the attribute type name for " + msg);
          throw new RuntimeException("Unable to determine the attribute type name for " + msg);
        }

        // Create or update the attribute whose ID has the range syntax removed
        Attribute newAttr = attrs.get(attrTypeName);
        if (newAttr == null) {
          newAttr = new BasicAttribute(attrTypeName, attr.isOrdered());
          attrs.put(newAttr);
        }

        // Copy values
        NamingEnumeration<?> attrValues = attr.getAll();
        while (attrValues.hasMore()) {
          newAttr.add(attrValues.next());
        }

        // Remove original attribute with range syntax from returned attributes
        sr.getAttributes().remove(attr.getID());

        // If the attribute ID ends with * we're done, otherwise increment
        if (!attr.getID().endsWith(END_OF_RANGE)) {

          // Determine next attribute ID
          final String initialRange = matcher.group(2);
          if (initialRange == null || initialRange.isEmpty()) {
            this.logger.error("Unable to determine initial range for " + msg);
            throw new RuntimeException("Unable to determine initial range for " + msg);
          }
          final String terminalRange = matcher.group(3);
          if (terminalRange == null || terminalRange.isEmpty()) {
            this.logger.error("Unable to determine terminal range for " + msg);
            throw new RuntimeException("Unable to determine terminal range for " + msg);
          }
          int start = 0;
          int end = 0;
          try {
            start = Integer.parseInt(initialRange);
            end = Integer.parseInt(terminalRange);
          } catch (NumberFormatException e) {
            this.logger.error("Unable to parse range for " + msg);
            throw new RuntimeException("Unable to parse range for " + msg);
          }
          int diff = end - start;
          final String nextAttrID = String.format(RANGE_FORMAT, attrTypeName, end + 1, end + diff + 1);

          // Search for next increment of values
          this.logger.debug("Searching for '" + nextAttrID + "' to increment " + msg);
          Attributes nextAttrs = this.ldap.getAttributes(sr.getName(), new String[] { nextAttrID });

          // Add all attributes to the search result
          NamingEnumeration<? extends Attribute> nextAttrsEnum = nextAttrs.getAll();
          while (nextAttrsEnum.hasMore()) {
            Attribute nextAttr = nextAttrsEnum.next();
            if (nextAttr == null) {
              this.logger.error("Null attribute returned for '" + nextAttrID + "' when incrementing " + msg);
              throw new RuntimeException("Null attribute returned for '" + nextAttrID + "' when incrementing " + msg);
            }
            sr.getAttributes().put(nextAttr);
          }

          // Iterate
          attrs = processAttributes(sc, sr);
        }
      }
    }

    return attrs;
  }
}
```

_Thanks to Tom Zeller @ Memphis for this implementation_

### Useful Links ###
  * http://www.openldap.org/its/index.cgi?findid=5472
  * http://www.openldap.org/lists/ietf-ldapbis/200404/msg00047.html
  * http://www.openldap.org/lists/openldap-bugs/200406/msg00108.html