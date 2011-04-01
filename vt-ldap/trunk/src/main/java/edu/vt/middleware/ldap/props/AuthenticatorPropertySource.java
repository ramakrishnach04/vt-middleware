/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.props;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.DnResolver;
import edu.vt.middleware.ldap.auth.SearchDnResolver;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;

/**
 * Reads properties specific to {@link Authenticator} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AuthenticatorPropertySource
  extends AbstractPropertySource<Authenticator>
{

  /** Invoker for authenticator. */
  private static final AdvancedPropertyInvoker INVOKER =
    new AdvancedPropertyInvoker(Authenticator.class);


  /**
   * Creates a new authenticator property source using the default properties
   * file.
   */
  public AuthenticatorPropertySource()
  {
    this(
      AuthenticatorPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new authenticator property source.
   *
   * @param  is  to read properties from
   */
  public AuthenticatorPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new authenticator property source.
   *
   * @param  props  to read properties from
   */
  public AuthenticatorPropertySource(final Properties props)
  {
    this(PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new authenticator property source.
   *
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public AuthenticatorPropertySource(
    final PropertyDomain domain, final Properties props)
  {
    this.object = initializeObject(
      INVOKER, new Authenticator(), domain.value(), props);

    LdapConnectionConfigPropertySource lccPropSource = null;

    // initialize a SearchDnResolver by default
    DnResolver resolver = this.object.getDnResolver();
    if (resolver == null) {
      final SearchDnResolverPropertySource drPropSource =
        new SearchDnResolverPropertySource(domain, props);
      resolver = drPropSource.get();
      this.object.setDnResolver(resolver);
    }
    if (resolver instanceof SearchDnResolver) {
      final SearchDnResolver sdr = (SearchDnResolver) resolver;
      if (sdr.getLdapConnectionConfig() == null) {
        lccPropSource = new LdapConnectionConfigPropertySource(domain, props);
        sdr.setLdapConnectionConfig(lccPropSource.get());
      }
    }

    // initialize a BindAuthenticationHandler by default
    AuthenticationHandler authHandler = this.object.getAuthenticationHandler();
    if (authHandler == null) {
      final BindAuthenticationHandlerPropertySource ahPropSource =
        new BindAuthenticationHandlerPropertySource(domain, props);
      authHandler = ahPropSource.get();
      this.object.setAuthenticationHandler(authHandler);
    }
    if (authHandler.getLdapConnectionConfig() == null) {
      if (lccPropSource == null) {
        lccPropSource = new LdapConnectionConfigPropertySource(domain, props);
      }
      authHandler.setLdapConnectionConfig(lccPropSource.get());
    }
  }


  /**
   * Returns the property names for this property source.
   *
   * @return  all property names
   */
  public static Set<String> getProperties()
  {
    return INVOKER.getProperties();
  }
}