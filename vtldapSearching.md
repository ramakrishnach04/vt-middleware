

# Search Methods #
This library provides two basic methods for searching an LDAP.
  1. Filter based searches <br /> These methods take the form of Ldap.search(...) and always contain a SearchFilter as an argument. Returned results are entries that match the supplied filter.
```
Ldap ldap = new Ldap(
  new LdapConfig("ldap://directory.vt.edu", "dc=vt,dc=edu"));
Iterator<SearchResult> results = ldap.search(
  new SearchFilter("(&(givenName=daniel)(sn=fisher))"), new String[]{"mail", "displayName"});
```
  1. Attributes based search <br /> These methods take the form of Ldap.searchAttributes(...) and always contain an Attributes as an argument. Returned results are entries that contain matches to the supplied attributes.
```
BasicAttributes attrs = new BasicAttributes();
attrs.put("givenName", "daniel");
attrs.put("sn", "fisher");
Ldap ldap = new Ldap(
  new LdapConfig("ldap://directory.vt.edu", "dc=vt,dc=edu"));
Iterator<SearchResult> results = ldap.searchAttributes(
  attrs, new String[]{"mail", "displayName"});
```

_Notes about properties_

  * baseDn <br /> This property provides the default search baseDN. Search methods which accept a DN can be used to override the baseDn property. If the baseDN is set as part of the ldapUrl then _all_ searches will be performed from that baseDN regardless of any other configuration. This applies to ONE\_LEVEL searches as will as SUB\_TREE.

# Search Result Handlers #
SearchResultHandlers provide a programmatic interface for post processing of search results and attributes. All search results must be processed by a handler, even if the handler is simply copying results. By default all search results are processed by the FqdnSearchResultHandler, which ensures that the DN of each result is fully qualified.


---

## Code Samples ##
Perform a search and with **no** processing of results.
```
Ldap ldap = new Ldap(new LdapConfig("ldap://directory.vt.edu:389"));
Iterator<SearchResult> results = ldap.search(
  "ou=People,dc=vt,dc=edu",
  new SearchFilter("sn=Fisher"),
  new String[]{"givenName", "mail"},
  (SearchResultHandler[]) null);
```

Retrieve all attributes for a specific entry and encode any attribute values of type `byte[]` into Base64.
```
Ldap ldap = new Ldap(new LdapConfig("ldap://directory.vt.edu:389"));
Attributes results = ldap.getAttributes(
  "uid=818037,ou=People,dc=vt,dc=edu", null, new BinaryAttributeHandler());
```

Perform a search and encode any attribute values of type `byte[]` into Base64.
```
Ldap ldap = new Ldap(new LdapConfig("ldap://directory.vt.edu:389"));
FqdnSearchResultHandler handler = new FqdnSearchResultHandler();
handler.setAttributeHandler(new AttributeHandler[] {new BinaryAttributeHandler()});
Iterator<SearchResult> results = ldap.search(
  "ou=People,dc=vt,dc=edu", new SearchFilter("sn=Fisher"), (String[]) null, handler);
```

Perform a search and include the entry DN as an attribute in the search results. Note that search result handlers are executed in the order in which they are provided.
```
LdapConfig config = new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu");
config.setSearchResultHandlers(new SearchResultHandler[]{new FqdnSearchResultHandler(), new EntryDnSearchResultHandler()});
Ldap ldap = new Ldap(config);
Iterator<SearchResult> results = ldap.search(new SearchFilter("sn=Fisher"));
```

## Supplied Search Result Handlers ##
This library provides the following search result handlers:
  * BinarySearchResultHandler - converts byte[.md](.md) values to Base64 encoded strings
  * EntryDnSearchResultHandler - adds the entry DN as an attribute of the result set
  * FqdnSearchResultHandler - ensures the entry DN is fully qualified
  * MergeSearchResultHandler - merges the attributes from all entries into the first entry
  * RecursiveSearchResultHandler - recursively adds the attributes from other entries into the current entry; useful for resolving nested relationships like group membership

## Custom Search Result Handlers ##
There are two interfaces available for implementing a custom search result handler. SearchResultHandler is available for the simple case of processing search results. ExtendedSearchResultHandler provides access to the _Ldap_ object that performed the search, which is useful for performing additional searches based on the results. Note that the NamingEnumeration is only processed by the first handler, subsequent handlers will be processing List.

### SearchResultHandler ###
```
public interface SearchResultHandler
  extends ResultHandler<SearchResult, SearchResult>
{
  List<O> process(SearchCriteria sc, NamingEnumeration<? extends R> en) 
    throws NamingException;

  List<O> process(
    SearchCriteria sc,
    NamingEnumeration<? extends R> en,
    Class<?>[] ignore)
    throws NamingException;

  List<O> process(SearchCriteria sc, List<? extends R> l)
    throws NamingException;

  AttributeHandler[] getAttributeHandler();

  void setAttributeHandler(final AttributeHandler[] ah);
}
```

### ExtendedSearchResultHandler ###
```
public interface ExtendedSearchResultHandler extends SearchResultHandler
{
  Ldap getSearchResultLdap();

  void setSearchResultLdap(final Ldap l); 
}
```

# Search Result Order #
Search results are handled by bean factories and three implementations are provided:
  * UnorderedLdapBeanFactory
  * OrderedLdapBeanFactory
  * SortedLdapBeanFactory
By default the unordered implementation is used. The ordered implementation stores results in the order in which they were returned from the LDAP. The sorted implementation stores the results sorted by attribute name and value.<br /><br />
To configure the bean provider programmatically JVM wide:
```
LdapBeanProvider.setLdapBeanFactory(new SortedLdapBeanFactory());
```
To configure the bean provider with a JVM switch:
```
-Dedu.vt.middleware.ldap.beanFactory=edu.vt.middleware.ldap.bean.OrderedLdapBeanFactory
```