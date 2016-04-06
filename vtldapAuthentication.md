

# Introduction #
Authentication against an LDAP is the result of performing a bind operation. Binding against an LDAP requires an entry DN and the entry's password. In order to resolve the entry DN an attribute is typically used which contains a user-friendly value. This means that authentication is a two step process:
  1. resolve the user's DN
  1. bind using the DN and password

Perform simple authentication:
```
AuthenticatorConfig config = new AuthenticatorConfig(
  "ldap://authn.directory.vt.edu", "ou=People,dc=vt,dc=edu");
config.setTls(true);
config.setUserFilter("(uid={0})");
Authenticator auth = new Authenticator(config);
if (auth.authenticate(user, credential)) {
  // success
  ...
} else {
  // failure
  ...
}
```

Perform simple authentication and get some attributes:
```
AuthenticatorConfig config = new AuthenticatorConfig(
  "ldap://authn.directory.vt.edu", "ou=People,dc=vt,dc=edu");
config.setTls(true);
config.setUserFilter("(uid={0})");
Authenticator auth = new Authenticator(config);
try {
  Attributes attrs = auth.authenticate(user, credential, new String[]{"givenName", "sn"});
  ...
} catch(AuthenticationException e) {
  // authentication failure
  ...
} catch (NamingException e) {
  // ldap related failure
  ...
}
```

# DN Resolution #

DN resolution is the process of obtaining the entry DN. This library provides the following implementations of DnResolver:
  * **SearchDnResolver**<br />this is the default implementation, which uses a search filter to find the entry DN
  * **ConstructDnResolver**<br />resolves the entry DN by concatenating a value with the baseDN; this implementation bypasses the search by requiring the user to supply part of the DN
  * **NoopDnResolver**<br />returns the user as the DN; useful for some authentication methods, like DIGEST-MD5

```
public interface DnResolver
{
  String resolve(String user) throws NamingException;

  AuthenticatorConfig getAuthenticatorConfig();

  void setAuthenticatorConfig(AuthenticatorConfig config);

  void close();
}
```

To use a different DN resolver call `AuthenticationConfig.setDnResolver(...)`.

## SearchDnResolver ##
This resolver can leverage both the _userField_ and _userFilter_ properties. If the _userFilter_ property is set then it is used to search the LDAP for the entry. If the _userField_ property is used a filter will be created by ORing it's value(s) together. By default multiple DNs will result in a NamingException, set `allowMultipleDns` to change this behavior.

# Authentication Handlers #
AuthenticationHandlers provide a programmatic interface for authentication implementations. This library provides two implementations:
  * **BindAuthenticationHandler**<br />This is the default authentication handler. It performs authentication by executing a LDAP bind.
  * **CompareAuthenticationHandler**<br />This implementation authenticates by comparing the password against the password attribute.

```
public interface AuthenticationResultHandler
{
  void process(AuthenticationCriteria ac, boolean success);
}
```

To use a different authentication handler call `AuthenticationConfig.setAuthenticationHandler(...)`.

# Authentication Result Handlers #
AuthenticationResultHandlers provide a programmatic interface for post processing of authentication results. There is no default authentication result handler and this distribution does not currently provide any implementations.<br /><br />
Potential use cases for authentication result handlers include:
  * updating a database on success or failure
  * sending notifications after a number of authentication failures
  * storing authentication statistics

```
public interface AuthenticationResultHandler
{
  void process(AuthenticationCriteria ac, boolean success);
}
```

To set an authentication result handler call `AuthenticationConfig.setAuthenticationResultHandlers(...)`.

# Authorization #
Authorization is not typically bundled with an authentication operation. However, the read restricted nature of LDAPs can make performing authorization at the time of authentication necessary. Since binding as the user may be the only time that the user's entry can be read, it is expedient to inspect the user's attributes at that time. Note that authorization failures can be distinguished from authentication as they result in AuthorizationException.

## Authorization Handlers ##
AuthorizationHandlers provide a programmatic interface for performing authorization after an authentication has succeeded. The only implementation provided by in this project is the CompareAuthorizationHandler. It is used by default whenever the `edu.vt.middleware.ldap.auth.authorizationFilter` property is used. Note that implementations should throw AuthorizationException on an authorization failure.
```
public interface AuthorizationHandler
{
  void process(AuthenticationCriteria ac, LdapContext ctx)
    throws NamingException;
}
```

To set an authorization handler call `AuthenticationConfig.setAuthorizationHandlers(...)`.