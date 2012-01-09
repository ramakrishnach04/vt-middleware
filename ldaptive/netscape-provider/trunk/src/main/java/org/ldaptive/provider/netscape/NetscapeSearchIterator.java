/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider.netscape;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPControl;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv2;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for netscape search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeSearchIterator implements SearchIterator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  private SearchRequest request;

  /** Control processor. */
  private ControlProcessor<LDAPControl> controlProcessor;

  /** Response data. */
  private Response<Void> response;

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Ldap search results. */
  private LDAPSearchResults results;

  /** Codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Operation time limit. */
  private int timeLimit;


  /**
   * Creates a new netscape search iterator.
   *
   * @param  sr  search request
   * @param  processor  control processor
   */
  public NetscapeSearchIterator(
    final SearchRequest sr,
    final ControlProcessor<LDAPControl> processor)
  {
    request = sr;
    controlProcessor = processor;
  }


  /**
   * Returns the ldap result codes to retry operations on.
   *
   * @return  result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the ldap result codes to retry operations on.
   *
   * @param  codes  result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    operationRetryResultCodes = codes;
  }


  /**
   * Returns the search ignore result codes.
   *
   * @return  result codes to ignore
   */
  public ResultCode[] getSearchIgnoreResultCodes()
  {
    return searchIgnoreResultCodes;
  }


  /**
   * Sets the search ignore result codes.
   *
   * @param  codes  to ignore
   */
  public void setSearchIgnoreResultCodes(final ResultCode[] codes)
  {
    searchIgnoreResultCodes = codes;
  }


  /**
   * Returns the operation time limit in milliseconds.
   *
   * @return  operation time limit
   */
  public int getTimeLimit()
  {
    return timeLimit;
  }


  /**
   * Sets the time limit.
   *
   * @param  limit  time in milliseconds
   */
  public void setTimeLimit(final int limit)
  {
    timeLimit = limit;
  }


  /**
   * Initializes this netscape search iterator.
   *
   * @param  conn  to search with
   *
   * @throws  LdapException  if an error occurs
   */
  public void initialize(final LDAPConnection conn)
    throws LdapException
  {
    connection = conn;
    try {
      results = search(connection, request);
    } catch (LDAPException e) {
      NetscapeUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    }
  }


  /**
   * Executes an ldap search.
   *
   * @param  conn  to search with
   * @param  sr  to read properties from
   *
   * @return  ldap search results
   *
   * @throws  LDAPException  if an error occurs
   */
  protected LDAPSearchResults search(
    final LDAPConnection conn,
    final SearchRequest sr)
    throws LDAPException
  {
    String[] retAttrs = sr.getReturnAttributes();
    if (retAttrs != null && retAttrs.length == 0) {
      retAttrs = new String[] {"1.1"};
    }
    return
      conn.search(
        sr.getBaseDn(),
        getSearchScope(sr.getSearchScope()),
        SearchFilter.format(sr.getSearchFilter()),
        retAttrs,
        sr.getTypesOnly(),
        getLDAPSearchConstraints(request));
  }


  /**
   * Returns a netscape search request object configured with the supplied
   * search request.
   *
   * @param  sr  search request containing configuration to create netscape
   * search request
   *
   * @return  search request
   *
   * @throws  LDAPException  if the search request cannot be initialized
   */
  protected LDAPSearchConstraints getLDAPSearchConstraints(
    final SearchRequest sr)
    throws LDAPException
  {
    final LDAPSearchConstraints cons = new LDAPSearchConstraints();
    cons.setTimeLimit(timeLimit);
    cons.setDereference(getDereference(request.getDerefAliases()));
    cons.setMaxResults((int) request.getSizeLimit());
    cons.setReferrals(
      ReferralBehavior.FOLLOW == request.getReferralBehavior() ? true : false);
    cons.setServerControls(
      controlProcessor.processRequestControls(request.getControls()));
    cons.setServerTimeLimit((int) request.getTimeLimit());
    return cons;
  }


  /**
   * Returns the netscape search scope for the supplied search scope.
   *
   * @param  ss  search scope
   *
   * @return  netscape search scope
   */
  protected static int getSearchScope(final SearchScope ss)
  {
    int scope = LDAPv2.SCOPE_SUB;
    if (ss == SearchScope.OBJECT) {
      scope = LDAPv2.SCOPE_BASE;
    } else if (ss == SearchScope.ONELEVEL) {
      scope = LDAPv2.SCOPE_ONE;
    } else if (ss == SearchScope.SUBTREE) {
      scope = LDAPv2.SCOPE_SUB;
    }
    return scope;
  }


  /**
   * Returns the netscape deference policy for the supplied deref aliases.
   *
   * @param  da  deref aliases
   *
   * @return  netscape deref constant
   */
  protected static int getDereference(final DerefAliases da)
  {
    int deref = LDAPv2.DEREF_NEVER;
    if (da == DerefAliases.ALWAYS) {
      deref = LDAPv2.DEREF_ALWAYS;
    } else if (da == DerefAliases.FINDING) {
      deref = LDAPv2.DEREF_FINDING;
    } else if (da == DerefAliases.NEVER) {
      deref = LDAPv2.DEREF_NEVER;
    } else if (da == DerefAliases.SEARCHING) {
      deref = LDAPv2.DEREF_SEARCHING;
    }
    return deref;
  }


  /** {@inheritDoc} */
  @Override
  public boolean hasNext()
    throws LdapException
  {
    if (results == null || response != null) {
      return false;
    }

    boolean more = false;
    try {
      more = results.hasMoreElements();
      if (!more) {
        final ResponseControl[] respControls =
          controlProcessor.processResponseControls(
            request.getControls(),
            results.getResponseControls());
        final boolean searchAgain = ControlProcessor.searchAgain(respControls);
        if (searchAgain) {
          results = search(connection, request);
          more = results.hasMoreElements();
        }
        if (!more) {
          response = new Response<Void>(
            null,
            ResultCode.SUCCESS,
            controlProcessor.processResponseControls(
              request.getControls(),
              results.getResponseControls()));
        }
      }
    } catch (LDAPException e) {
      final ResultCode rc = ignoreSearchException(searchIgnoreResultCodes, e);
      if (rc == null) {
        NetscapeUtil.throwOperationException(
          operationRetryResultCodes,
          e,
          controlProcessor);
      }
      response = new Response<Void>(
        null,
        rc,
        controlProcessor.processResponseControls(
          request.getControls(),
          results.getResponseControls()));
    }
    return more;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry next()
    throws LdapException
  {
    final NetscapeUtil bu = new NetscapeUtil(request.getSortBehavior());
    bu.setBinaryAttributes(request.getBinaryAttributes());

    LdapEntry le = null;
    try {
      le = bu.toLdapEntry(results.next());
    } catch (LDAPReferralException e) {
      if (request.getReferralBehavior() == ReferralBehavior.IGNORE) {
        logger.debug("Ignoring referral exception", e);
      } else {
        NetscapeUtil.throwOperationException(
          operationRetryResultCodes,
          e,
          controlProcessor);
      }
    } catch (LDAPException e) {
      final ResultCode rc = ignoreSearchException(searchIgnoreResultCodes, e);
      if (rc == null) {
        NetscapeUtil.throwOperationException(
          operationRetryResultCodes,
          e,
          controlProcessor);
      }
      response = new Response<Void>(
        null,
        rc,
        controlProcessor.processResponseControls(
          request.getControls(),
          results.getResponseControls()));
    }
    return le;
  }


  /**
   * Determines whether the supplied ldap exception should be ignored.
   *
   * @param  ignoreResultCodes  to match against the exception
   * @param  e  ldap exception to match
   *
   * @return  result code that should be ignored or null
   */
  protected ResultCode ignoreSearchException(
    final ResultCode[] ignoreResultCodes,
    final LDAPException e)
  {
    ResultCode ignore = null;
    if (ignoreResultCodes != null && ignoreResultCodes.length > 0) {
      for (ResultCode rc : ignoreResultCodes) {
        if (e.getLDAPResultCode() == rc.value()) {
          logger.debug("Ignoring ldap exception", e);
          ignore = rc;
          break;
        }
      }
    }
    return ignore;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> getResponse()
  {
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException {}
}