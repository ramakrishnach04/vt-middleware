

# Introduction #
The configuration of LDAP related objects in this library is done via reflection on their associated PropertyConfig objects. This allows the JAAS and properties file implementations continuity as the API changes. All the properties listed here map directly to the fields found on the concrete implementations of PropertyConfig.

# Properties Files #
The Ldap and Authenticator objects can be configured from a properties file. This is useful for maintaining a ldap configuration outside of your source code. Simply create a file called _ldap.properties_ and place it in your root classpath..
```
Ldap ldap = new Ldap();
ldap.loadFromProperties();
```
If you prefer to use a different filename and/or classpath location:
```
Ldap ldap = new Ldap();
ldap.loadFromProperties(MyClass.class.getResourceAsStream("/classpath/to/your/propertiesFile"));
```
If you prefer to use a system filepath:
```
Ldap ldap = new Ldap();
ldap.loadFromProperties(new FileInputStream("/path/to/your/propertiesFile"));
```

# Properties #
Any property provided that does not match the following property names is placed directly into the JNDI context. This allows the user to provide ad-hoc environment properties.

### Context Properties ###
Properties injected directly into the JNDI context.
| **Property Name** | **Default Value** | **Description** |
|:------------------|:------------------|:----------------|
| edu.vt.middleware.ldap.contextFactory | com.sun.jndi.ldap.LdapCtxFactory | fully qualified class name of the context factory that JNDI should use |
| edu.vt.middleware.ldap.sslSocketFactory | _none_            | fully qualified class name which implements javax.net.ssl.SSLSocketFactory; will be used for all TLS/SSL connections |
| edu.vt.middleware.ldap.hostnameVerifier | _none_            | fully qualified class name which implements javax.net.ssl.HostnameVerifier; will be used for all TLS connections |
| edu.vt.middleware.ldap.ldapUrl | _none_            | fully qualified URL to the ldap. e.g. ldap://directory.vt.edu:389. This property can also accept the base DN: ldap://directory.vt.edu:389/ou=People,dc=vt,dc=edu. (Note that this format should only be used for searching. If you will be performing updates or authentications, then including the base DN in the URL has the potential to cause problems.) |
| edu.vt.middleware.ldap.timeout | -1                | the amount of time in milliseconds that connect operations will block; a value of -1 means use the network timeout value |
| edu.vt.middleware.ldap.authoritative | false             | whether authoritative responses are accepted from DNS servers |
| edu.vt.middleware.ldap.batchSize | -1                | the batch size to use when returning results; a value of -1 means use no batch size |
| edu.vt.middleware.ldap.dnsUrl | _none_            | the DNS url to use for hostname resolution |
| edu.vt.middleware.ldap.language | _none_            | the preferred language |
| edu.vt.middleware.ldap.referral | _none_            | specifies how referrals should be handled; must be one of 'throw', 'ignore', or 'follow' |
| edu.vt.middleware.ldap.derefAliases | _none_            | specifies how aliases should be handled; must be one of 'always', 'never', 'finding', or 'searching' |
| edu.vt.middleware.ldap.binaryAttributes | _none_            | space delimited list of attributes which should be treated as binary; e.g. userSMIMECertificate jpegPhoto |
| edu.vt.middleware.ldap.saslAuthorizationId | _none_            | entity for which access control checks should be made if the authentication succeeds |
| edu.vt.middleware.ldap.saslRealm | _none_            | identifies the realm or domain from which the principal name should be chosen |
| edu.vt.middleware.ldap.typesOnly | false             | only return attribute type names |
| edu.vt.middleware.ldap.ssl | false             | whether SSL should be used for LDAP connections |
| edu.vt.middleware.ldap.tls | false             | whether TLS should be used for LDAP connections |

### Search Properties ###
Properties used for connecting, binding, and searching.
| **Property Name** | **Default Value** | **Description** |
|:------------------|:------------------|:----------------|
| edu.vt.middleware.ldap.baseDn | _none_            | default base dn used for operations |
| edu.vt.middleware.ldap.bindDn | _none_            | dn to bind as before performing operations |
| edu.vt.middleware.ldap.bindCredential | _none_            | credential for the bind dn |
| edu.vt.middleware.ldap.authtype | simple            | LDAP authentication mechanism; must be one of 'none', 'simple', 'strong', 'DIGEST-MD5', 'CRAM-MD5', 'GSSAPI', or 'EXTERNAL' |
| edu.vt.middleware.ldap.ignoreCase | true              | whether to ignore case in attribute names |
| edu.vt.middleware.ldap.operationRetry | 1                 | specifies the number of times an LDAP operation will be retried if a retry exception occurs; set to -1 for infinite retries |
| edu.vt.middleware.ldap.operationRetryWait | 0                 | the amount of time in milliseconds that LDAP operations should wait before retrying |
| edu.vt.middleware.ldap.operationRetryBackoff | 0                 | factor by which to multiply the operation retry wait time; this allows clients to progressively delay each retry |
| edu.vt.middleware.ldap.operationRetryExceptions | {CommunicationException, ServiceUnavailableException} | exception classes to retry operations on; supports a comma delimited list for multiple values |
| edu.vt.middleware.ldap.logCredentials | false             | whether bind credentials should be logged; logging occurs at debug level |
| edu.vt.middleware.ldap.connectionHandler | DefaultConnectionHandler | creates and closes LDAP connections. |
| edu.vt.middleware.ldap.searchResultHandlers | {FqdnSearchResultHandler} | performs post processing of results; supports a comma delimited list for multiple values. See [Search Result Handlers](vtldapSearching#Search_Result_Handlers.md) |
| edu.vt.middleware.ldap.handlerIgnoreExceptions | {LimitExceededException} | exception classes that should be ignored when processing result sets; supports a comma delimited list for multiple values |
| edu.vt.middleware.ldap.pagedResultsSize | 0                 | value used when the PagedResultsControl in invoked |

### SearchControl Properties ###
Properties that map to: [javax.naming.directory.SearchControls](http://java.sun.com/j2se/1.5.0/docs/api/javax/naming/directory/SearchControls.html)
| **Property Name** | **Default Value** | **Description** |
|:------------------|:------------------|:----------------|
| edu.vt.middleware.ldap.searchScope | SUBTREE (2)       | specifies the scope of searches; must be one of OBJECT (0), ONELEVEL (1), or SUBTREE (2) |
| edu.vt.middleware.ldap.timeLimit | 0                 | the amount of time in milliseconds that search operations will block; a value of 0 means blocking indefinitely |
| edu.vt.middleware.ldap.countLimit | 0                 | the maximum number of entries that search operations will return; a value of 0 means return all results |
| edu.vt.middleware.ldap.derefLinkFlag | false             | whether links will be dereferenced during the search |
| edu.vt.middleware.ldap.returningObjFlag | false             | whether objects will be returned as part of the result |

### Authenticator Properties ###
Properties used specifically for authentication.
All the previous properties are inherited by the Authenticator and can be overridden as necessary.
| **Property Name** | **Default Value** | **Description** |
|:------------------|:------------------|:----------------|
| edu.vt.middleware.ldap.auth.userField | {uid}             | LDAP attribute(s) which contain the user identifier to search on; supports a comma delimited list for multiple values |
| edu.vt.middleware.ldap.auth.userFilter | _none_            | LDAP filter to search for users. Mutually exclusive with the userField propery. |
| edu.vt.middleware.ldap.auth.userFilterArgs | _none_            | LDAP filter arguments for the userFilter property. The {0} argument is automatically populated with the supplied user value. Any arguments provided here will start at {1}. Supports a comma delimited list for multiple values |
| edu.vt.middleware.ldap.auth.constructDn | false             | whether the authentication dn should be constructed or looked up in the LDAP; a constructed dn takes the form: userField=user,baseDn. Equivalent to setting dnResolver to ConstructDnResolver.  |
| edu.vt.middleware.ldap.auth.allowMultipleDns | false             | whether an exception should be thrown if multiple DNs are found when the user is searched for; default behavior is to throw a NamingException  |
| edu.vt.middleware.ldap.auth.dnResolver | SearchDnResolver  | implementation used to resolve a user's DN |
| edu.vt.middleware.ldap.auth.subtreeSearch | false             | whether the authentication dn should be searched for over the entire base; default value is onelevel  |
| edu.vt.middleware.ldap.auth.authenticationHandler | BindAuthenticationHandler | performs LDAP authentication, see [Authentication Handlers](vtldapAuthentication#Authentication_Handlers.md). |
| edu.vt.middleware.ldap.auth.authenticationResultHandlers | _none_            | performs post processing of authentication results, see [Authentication Result Handlers](vtldapAuthentication#Authentication_Result_Handlers.md). Supports a comma delimited list for multiple values |
| edu.vt.middleware.ldap.auth.authorizationFilter | _none_            | ldap filter to use for performing authorization after successful authentication  |
| edu.vt.middleware.ldap.auth.authorizationFilterArgs | _none_            | LDAP filter arguments for the authorizationFilter property. Supports a comma delimited list for multiple values |
| edu.vt.middleware.ldap.auth.authorizationHandlers | _none_            | performs post processing of authorization once authentication has succeeded, see [Authorization Handlers](vtldapAuthentication#Authorization_Handlers.md). Supports a comma delimited list for multiple values |