

# Version 3.3.9 #
  * [vtldap-235](http://code.google.com/p/vt-middleware/issues/detail?id=235) - JNDI does not startTLS when following referrals

# Version 3.3.8 #
  * [vtldap-226](http://code.google.com/p/vt-middleware/issues/detail?id=226) - default certificate hostname verifier incorrectly parses CN

# Version 3.3.7 #
  * [vtldap-175](http://code.google.com/p/vt-middleware/issues/detail?id=175) - timer tasks may die if LDAP unavailable

# Version 3.3.6 #
  * [vtldap-133](http://code.google.com/p/vt-middleware/issues/detail?id=133) - Non default connection strategies broken for LDAPS

# Version 3.3.5 #
  * [vtldap-130](http://code.google.com/p/vt-middleware/issues/detail?id=130) - Pool checkout may hang if creation fails
  * [vtldap-129](http://code.google.com/p/vt-middleware/issues/detail?id=129) - LDAPS certificate hostname verification

# Version 3.3.4 #
  * [vtldap-113](http://code.google.com/p/vt-middleware/issues/detail?id=113) - Regression from vt-ldap 109, URLs in DN not parsed correctly

# Version 3.3.3 #
  * [vtldap-110](http://code.google.com/p/vt-middleware/issues/detail?id=110) - Send correct return value for commit in LdapLoginModule
  * [vtldap-109](http://code.google.com/p/vt-middleware/issues/detail?id=109) - Properly handle DNs with invalid composite name characters

# Version 3.3.2 #
  * [vtldap-93](http://code.google.com/p/vt-middleware/issues/detail?id=93) - Escape attribute values when used with the userField property

# Version 3.3.1 #
  * [vtldap-90](http://code.google.com/p/vt-middleware/issues/detail?id=90) - Added search result handler and attribute handler for modifying the case of search results
  * [vtldap-89](http://code.google.com/p/vt-middleware/issues/detail?id=89) - Change behavior of stopTLS when closing connections
  * [vtldap-75](http://code.google.com/p/vt-middleware/issues/detail?id=75) - Added support for setting cipher suites and protocols in TLSSocketFactory
  * [vtldap-74](http://code.google.com/p/vt-middleware/issues/detail?id=74) - Added fail-over support to the connection handlers
  * [vtldap-71](http://code.google.com/p/vt-middleware/issues/detail?id=71) - LdapRolesAuthorization module should have option to fail if no results are found

# Version 3.3 #
  * [vtldap-67](http://code.google.com/p/vt-middleware/issues/detail?id=67) - Added new interface ExtendedSearchResultHandler to support handlers that require a reference to the ldap object which performed the search.
    * Also provided support for nested property initialization via property string.
  * [vtldap-66](http://code.google.com/p/vt-middleware/issues/detail?id=66) - Added JAAS options to support the creation of groups that contain principals and roles
  * [vtldap-63](http://code.google.com/p/vt-middleware/issues/detail?id=63) - LdapLoginModule setLdapDnPrincipal default value changed to false
  * [vtldap-62](http://code.google.com/p/vt-middleware/issues/detail?id=62) - Added support for configuring TLS/SSL trust and key material directly in the JAAS configuration file.
  * [vtldap-59](http://code.google.com/p/vt-middleware/issues/detail?id=59) - Removed ConnectLdapValidator as the default validator in DefaultLdapFactory
  * [vtldap-58](http://code.google.com/p/vt-middleware/issues/detail?id=58) - Added support for sorting LDAP results via bean factory implementation
  * [vtldap-57](http://code.google.com/p/vt-middleware/issues/detail?id=58) - Properties deprecated from peer review:
    * serviceUser -> bindDn
    * serviceCredential -> bindCredential
    * base -> baseDn
  * [vtldap-56](http://code.google.com/p/vt-middleware/issues/detail?id=56) - Added RecursiveSearchResultHandler and updated LdapRoleAuthorizationModule with an option to use it
  * [vtldap-55](http://code.google.com/p/vt-middleware/issues/detail?id=55) - Added AuthorizationException to distinguish authentication and authorization errors
  * [vtldap-54](http://code.google.com/p/vt-middleware/issues/detail?id=54) - Added support for infinite operation retries and a linear backoff on operation retries
  * [vtldap-53](http://code.google.com/p/vt-middleware/issues/detail?id=53) - Added support for setting default roles in the LdapLoginModule
  * [vtldap-50](http://code.google.com/p/vt-middleware/issues/detail?id=50) - Added authentication handlers to provide an API for custom authentication implementations
    * **NOTE** authentication related classes have moved into the _edu.vt.middleware.ldap.auth_ package
  * [vtldap-49](http://code.google.com/p/vt-middleware/issues/detail?id=49) - Provide way to set class type properties to null
  * [vtldap-47](http://code.google.com/p/vt-middleware/issues/detail?id=47) - Added configuration options to control operation retry exceptions and operation retry wait
  * [vtldap-45](http://code.google.com/p/vt-middleware/issues/detail?id=45) - Added connection handlers to provide an API for custom connection strategies

# Version 3.2 #
  * [vtldap-41](http://code.google.com/p/vt-middleware/issues/detail?id=41) - Added property to allow/disallow multiple DNs when performing binds
  * [vtldap-40](http://code.google.com/p/vt-middleware/issues/detail?id=40) - Exposed search methods that accept SearchControls
  * [vtldap-39](http://code.google.com/p/vt-middleware/issues/detail?id=39) - Added PagedResultsControl implementation
  * [vtldap-38](http://code.google.com/p/vt-middleware/issues/detail?id=38) - Updated JAAS principals to include any ldap attributes requested as roles
  * [vtldap-37](http://code.google.com/p/vt-middleware/issues/detail?id=37) - Added support for Authorization handlers as part of the authentication process
  * [vtldap-14](http://code.google.com/p/vt-middleware/issues/detail?id=14) - JAAS modules now default to return no attributes as roles. Use '`*`' to return all attributes.
  * [vtldap-35](http://code.google.com/p/vt-middleware/issues/detail?id=35) - Refactored Ldif and Dsml APIs for consistency
  * [vtldap-34](http://code.google.com/p/vt-middleware/issues/detail?id=34) - Added support for parsing LDIF files
  * [vtldap-32](http://code.google.com/p/vt-middleware/issues/detail?id=32) - Added MergeSearchResultHandler
  * [vtldap-31](http://code.google.com/p/vt-middleware/issues/detail?id=31) - Added ability to ignore any specific naming exceptions encountered by a handler
  * [vtldap-30](http://code.google.com/p/vt-middleware/issues/detail?id=30) - Moved LdapUtil.searchAttributesRecursive() to RecursiveAttributeHandler
  * [vtldap-29](http://code.google.com/p/vt-middleware/issues/detail?id=29) - Changed SearchResultHandler.setAttributeHandler() to accept an array of handlers
  * [vtldap-26](http://code.google.com/p/vt-middleware/issues/detail?id=26) - Added userFilter and userFilterArgs for custom lookups of authentication DNs
  * [vtldap-25](http://code.google.com/p/vt-middleware/issues/detail?id=25) - Added authorization filter arguments
  * [vtldap-24](http://code.google.com/p/vt-middleware/issues/detail?id=24) - Add size() methods to LdapResult and LdapAttributes
  * [vtldap-23](http://code.google.com/p/vt-middleware/issues/detail?id=23) - Added Ldap.rename() implementation
  * [vtldap-22](http://code.google.com/p/vt-middleware/issues/detail?id=22) - Added SearchFilter class as argument for all methods that accept filters
  * [vtldap-21](http://code.google.com/p/vt-middleware/issues/detail?id=21) - Updated compare() to accept filter arguments
  * [vtldap-19](http://code.google.com/p/vt-middleware/issues/detail?id=17) - Refactored attribute modification methods on Ldap
  * [vtldap-17](http://code.google.com/p/vt-middleware/issues/detail?id=17) - Fixed Properties based setter to allow overriding of properties

# Version 3.1 #
  * Updated LGPL from version 2.1 to 3.0
  * [vtldap-16](http://code.google.com/p/vt-middleware/issues/detail?id=16) - JAAS modules now support the tryFirstPass, useFirstPass, storePass options
  * [vtldap-15](http://code.google.com/p/vt-middleware/issues/detail?id=15) - JAAS role based functionality moved to separate login modules
  * [vtldap-14](http://code.google.com/p/vt-middleware/issues/detail?id=14) - JAAS modules now support returning all attributes from searches
  * [vtldap-13](http://code.google.com/p/vt-middleware/issues/detail?id=13) - Added AuthenticationResultHandlers
  * [vtldap-12](http://code.google.com/p/vt-middleware/issues/detail?id=12) - SearchCriteria now receives Context.getNameInNamespace() for the DN if it exists, otherwise it receives the DN of the search
  * [vtldap-11](http://code.google.com/p/vt-middleware/issues/detail?id=11) - LdapConfig base dn now defaults to ''
  * [vtldap-10](http://code.google.com/p/vt-middleware/issues/detail?id=10) - Pool initialization no longer throws exception if pool is empty
  * [vtldap-9](http://code.google.com/p/vt-middleware/issues/detail?id=9) - Updated LdapConfig to contain a property for SearchResultHandlers
  * [vtldap-8](http://code.google.com/p/vt-middleware/issues/detail?id=8) - Updated method signatures that accept a handler to accept an array of handlers
  * [vtldap-7](http://code.google.com/p/vt-middleware/issues/detail?id=7) - Converted string based servlet init parameters to use enums
  * [vtldap-6](http://code.google.com/p/vt-middleware/issues/detail?id=6) - Binary attribute values are now properly encoded for DSML output
  * [vtldap-5](http://code.google.com/p/vt-middleware/issues/detail?id=5) - Changed LdapAttribute.hashCode() to use String.hashCode() rather than Object.hashCode()
  * [vtldap-3](http://code.google.com/p/vt-middleware/issues/detail?id=3) - Added new SearchResultHandler which injects a SearchResult DN as an attribute of the result set
  * [vtldap-2](http://code.google.com/p/vt-middleware/issues/detail?id=2) - Updated property based methods that accept a string filename to accept an input stream

# Version 3.0 #
Initial google code release.