

# Introduction #
This library provides several JAAS LoginModules for authentication and authorization against a LDAP.

## LdapLoginModule ##
JAAS module which provides authentication and authorization against a LDAP.

### Configuration ###
This configuration will authenticate a user using the 'uid' attribute and populate the user's principals with the values of the 'eduPersonAffiliation' attribute.
```
vt-ldap {
   edu.vt.middleware.ldap.jaas.LdapLoginModule required
     ldapUrl="ldap://authn.directory.vt.edu:389"
     baseDn="ou=people,dc=vt,dc=edu"
     tls="true"
     userFilter="(uid={0})"
     userRoleAttribute="eduPersonAffiliation";
};
```

This configuration performs the same function as the previous, but it will use the _bindDn_ and _bindCredential_ to lookup to the user's DN. In addition, the configured certificate will be used to trust the LDAP server.
```
vt-ldap {
   edu.vt.middleware.ldap.jaas.LdapLoginModule required
     ldapUrl="ldap://authn.directory.vt.edu:389"
     baseDn="ou=people,dc=vt,dc=edu"
     bindDn="cn=priviledged_user,ou=services,dc=vt,dc=edu"
     bindCredential="notarealpassword"
     tls="true"
     sslSocketFactory="{{trustCertificates=file:/path/to/certs.pem}}"
     userFilter="(uid={0})"
     userRoleAttribute="eduPersonAffiliation";
};
```

See [Authentication](vtldapAuthentication.md) for more information on how to configure the aspects of authentication.<br />
See [SSL / TLS](vtldapTLS.md) for more information on configuring secure transport.

### Options ###
Each option corresponds to a [Ldap Property](vtldapProperties.md) without the domain prefix.<br />
There are several options which do not correspond to Ldap properties and those are explained below:
| **Property Name** | **Description** |
|:------------------|:----------------|
| userRoleAttribute | An attribute(s) that exists on the user entry. The value(s) of these attributes will be added as roles for this user. Comma delimited for multiple attributes. By default no attributes are returned as roles. If all attributes should be assigned as role data, set this property to '`*`'.|
| useFirstPass      | Whether the login name and password should be retrieved from shared state rather than the callback handler. A login failure with this login name results in a LoginException.|
| tryFirstPass      | Whether the login name and password should be retrieved from shared state rather than the callback handler. A login failure with this login name results in the callback handler begin invoked for a new login name and password.|
| storePass         | Whether the login name, login dn, and login password should be stored in shared state after a successful login. Existing values are overwritten.|
| clearPass         | Whether the login name, login dn, and login password should be removed from shared state after a successful login.|
| setLdapPrincipal  | Whether the login name should be stored in the LdapPrincipal class. Default value is true.|
| setLdapDnPrincipal | Whether the LDAP entry DN should be stored in the LdapDnPrincipal class. Default value is false.|
| setLdapCredential | Whether the login password should be stored in the LdapCredential class. Default value is true.|
| defaultRole       | Role(s) to set if login succeeds on this module. Comma delimited for multiple values. Default value is empty. |
| principalGroupName | Name of the Group to place the principal(s) in if login succeeds on this module. Default value is empty. |
| roleGroupName     | Name of the Group to place the roles in if login succeeds on this module. Default value is empty. |

Any options supplied that do not map to a defined property will be passed directly to the JNDI context.

## LdapRoleAuthorizationModule ##
JAAS module which provides authorization against a LDAP. This module is meant to be stacked with a module that has performed authentication.

### Configuration ###
This configuration is the same as the previous except it will also populate the user's principals with value of the 'uugid' attribute found on any entries in the 'ou=Groups,dc=vt,dc=edu' branch which contain a member attribute value that includes this user's DN.
```
vt-ldap {
   edu.vt.middleware.ldap.jaas.LdapLoginModule required
     ldapUrl="ldap://authn.directory.vt.edu:389"
     baseDn="ou=people,dc=vt,dc=edu"
     tls="true"
     userFilter="(uid={0})"
     userRoleAttribute="eduPersonAffiliation";
   edu.vt.middleware.ldap.jaas.LdapRoleAuthorizationModule required
     useFirstPass="true"
     ldapUrl="ldap://directory.vt.edu:389/ou=groups,dc=vt,dc=edu"
     roleFilter="(member={0})"
     roleAttribute="uugid";
};
```

### Options ###
Each option corresponds to a [Ldap Property](vtldapProperties.md) without the domain prefix.<br />
There are several options which do not correspond to Ldap properties and those are explained below:
| **Property Name** | **Description** |
|:------------------|:----------------|
| roleFilter        | An LDAP search filter where {0} is replaced with the user dn and {1} is replaced with the user. This is used to find roles for the user. |
| roleAttribute     | An attribute(s) that exists on any role entries found with the roleFilter. The value(s) of these attributes will be added as roles for this user. Comma delimited for multiple attributes. By default no attributes are returned as roles. If all attributes should be assigned as role data, set this property to '`*`'. |
| noResultsIsError  | Whether an exception should be thrown if no roles are found. Default value is false. |
| useFirstPass      | Whether the login name and password should be retrieved from shared state rather than the callback handler. A login failure with this login name results in a LoginException.|
| tryFirstPass      | Whether the login name and password should be retrieved from shared state rather than the callback handler. A login failure with this login name results in the callback handler begin invoked for a new login name and password.|
| storePass         | Whether the login name, login dn, and login password should be stored in shared state after a successful login. Existing values are overwritten.|
| clearPass         | Whether the login name, login dn, and login password should be removed from shared state after a successful login.|
| setLdapPrincipal  | Whether the login name should be stored in the LdapPrincipal class. Default value is false.|
| setLdapDnPrincipal | Whether the LDAP entry DN should be stored in the LdapDnPrincipal class. Default value is false.|
| setLdapCredential | Whether the login password should be stored in the LdapCredential class. Default value is false.|
| defaultRole       | Role(s) to set if login succeeds on this module. Comma delimited for multiple values. Default value is empty. |
| principalGroupName | Name of the Group to place the principal(s) in if login succeeds on this module. Default value is empty. |
| roleGroupName     | Name of the Group to place the roles in if login succeeds on this module. Default value is empty. |

Any options supplied that do not map to a defined property will be passed directly to the JNDI context.

## LdapDnAuthorizationModule ##
JAAS module which injects the login name, login dn, and/or login password into shared state. This module is meant to be stacked with a module that has performed authentication.

### Configuration ###
This configuration uses Kerberos for authentication and then injects LDAP data into the subject. Note that the LdapDnAuthorizationModule is stacked before the LdapRoleAuthorizationModule so that the DN can be leveraged in the roleFilter query.
```
vt-ldap {
   com.sun.security.auth.module.Krb5LoginModule required
     storePass="true"
     debug="true";
   edu.vt.middleware.ldap.jaas.LdapDnAuthorizationModule required
     useFirstPass="true"
     storePass="true"
     ldapUrl="ldap://authn.directory.vt.edu:389"
     baseDn="ou=people,dc=vt,dc=edu"
     tls="true"
     userFilter="(uid={0})";
   edu.vt.middleware.ldap.jaas.LdapRoleAuthorizationModule required
     useFirstPass="true"
     ldapUrl="ldap://directory.vt.edu:389/ou=groups,dc=vt,dc=edu"
     roleFilter="(member={0})"
     roleAttribute="uugid";
};
```

### Options ###
Each option corresponds to a [Ldap Property](vtldapProperties.md) without the domain prefix.<br />
There are several options which do not correspond to Ldap properties and those are explained below:
| **Property Name** | **Description** |
|:------------------|:----------------|
| noResultsIsError  | Whether an exception should be thrown if no DN is found. Default value is false. |
| useFirstPass      | Whether the login name and password should be retrieved from shared state rather than the callback handler. A login failure with this login name results in a LoginException.|
| tryFirstPass      | Whether the login name and password should be retrieved from shared state rather than the callback handler. A login failure with this login name results in the callback handler begin invoked for a new login name and password.|
| storePass         | Whether the login name, login dn, and login password should be stored in shared state after a successful login. Existing values are overwritten.|
| clearPass         | Whether the login name, login dn, and login password should be removed from shared state after a successful login.|
| setLdapPrincipal  | Whether the login name should be stored in the LdapPrincipal class. Default value is false.|
| setLdapDnPrincipal | Whether the LDAP entry DN should be stored in the LdapDnPrincipal class. Default value is false.|
| setLdapCredential | Whether the login password should be stored in the LdapCredential class. Default value is false.|
| defaultRole       | Role(s) to set if login succeeds on this module. Comma delimited for multiple values. Default value is empty. |
| principalGroupName | Name of the Group to place the principal(s) in if login succeeds on this module. Default value is empty. |
| roleGroupName     | Name of the Group to place the roles in if login succeeds on this module. Default value is empty. |

Any options supplied that do not map to a defined property will be passed directly to the JNDI context.


---

# Sample Code #
Once the `java.security.auth.login.config` property has been set, you can use the JAAS module with a LoginContext.

```
import javax.security.auth.login.LoginContext;
import com.sun.security.auth.callback.TextCallbackHandler;

LoginContext lc = new LoginContext("vt-ldap", new TextCallbackHandler());
lc.login()
```


---

# JBoss #
The JAAS module can be used in JBoss. Note that if authenticated users need to invoke EJBs then you must set the _principalGroupName_ and _roleGroupName_ as shown. JBoss requires groups with those exact names to contain the associated principals. Otherwise those options can be omitted.<br />

## Instructions ##
  1. Put the vt-ldap jar in the JBoss server classpath
  1. Add a new application-policy to the JBoss login-config.xml file:
```
        <application-policy name="LdapAuth">
          <authentication>
            <login-module
              code="edu.vt.middleware.ldap.jaas.LdapLoginModule"
              flag="required">

              <module-option name="ldapUrl">
                ${ldap.url}
              </module-option>
              <module-option name="base">
                ${ldap.base}
              </module-option>
              <module-option name="tls">true</module-option>
              <module-option name="userFilter">
                ${ldap.userFilter}
              </module-option>
              <module-option name="principalGroupName">CallerPrincipal</module-option>
              <module-option name="roleGroupName">Roles</module-option>
            </login-module>
          </authentication>
        </application-policy>
```
  1. Restart JBoss


---

# Tomcat Realm #
The JAAS module can be used as a Tomcat realm.<br />

## Instructions ##
  1. Put the vt-ldap jar in the Tomcat server classpath
  1. Set the `java.security.auth.login.config` property to the location of your JAAS file
    * can be done by setting a shell environment variable or editing the bin/catalina.sh script
  1. Add the realm declaration to the Tomcat server.xml file:
```
      <Realm className="org.apache.catalina.realm.JAASRealm"
             appName="vt-ldap"
             userClassNames="edu.vt.middleware.ldap.jaas.LdapPrincipal"
             roleClassNames="edu.vt.middleware.ldap.jaas.LdapRole"/>
```
    * note that appName must be the same as the declaration in your JAAS file.
  1. Configure your web.xml to use your LDAP roles:
```
        <security-constraint>
          <web-resource-collection>
            <web-resource-name>My webapp</web-resource-name>
            <url-pattern>/*</url-pattern>
          </web-resource-collection>
          <auth-constraint>
             <role-name>staff</role-name>
          </auth-constraint>
        </security-constraint>
```
  1. Restart tomcat