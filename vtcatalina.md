

# Introduction #
RemoteUserAuthenticator is a Java class that provides a trust-based Catalina [Authenticator](http://tomcat.apache.org/tomcat-5.5-doc/catalina/docs/api/org/apache/catalina/Authenticator.html) for use in the Tomcat servlet container.  This authenticator checks for the existence of a principal, commonly set by Tomcat processing the REMOTE\_USER header, and re-authenticates them using a provided Tomcat [Realm](http://tomcat.apache.org/tomcat-5.5-doc/catalina/docs/api/org/apache/catalina/Realm.html).  This is particularly useful in Single-Sign-On environments where you need to integrate an existing application that leverages container security.

Tomcat realms used with this authenticator **must be prepared to accept a null password**, the assumption being that this user has already authenticated and the realm exists solely to provide role data.  It is important to consider the security implications made by RemoteUserAuthenticator in trusting the REMOTE\_USER (or other) header in determining prior authentication.


---

# Installation #
  1. Checkout the current release version:
```
  svn co http://vt-middleware.googlecode.com/svn/vt-catalina/tags/vt-catalina-1.0
```
  1. Build the jar:
```
  cd vt-catalina-1.0
  mvn package
```
  1. Copy the jar to Tomcat:
```
  cp target/vt-catalina-1.0.jar $CATALINA_HOME/lib
```
  1. Extract Authenticators.properties:
```
  jar xvf $CATALINA_HOME/lib/catalina.jar org/apache/catalina/startup/Authenticators.properties
```
  1. Add the following line to the end of Authenticators.properties:
```
  REMOTE-USER=edu.vt.middleware.catalina.authenticator.RemoteUserAuthenticator
```
  1. Update Authenticators.properties
```
  jar uvf $CATALINA_HOME/lib/catalina.jar org/apache/catalina/startup/Authenticators.properties
```

Tomcat now recognizes the REMOTE-USER authentication method.


---

# Configuration #

## Tomcat ##
You must now add a realm to Tomcat that supports authentication without a password.<br />
The following example will use the LdapRoleAuthorizationModule, as it expects to be stacked with a module that has performed authentication.<br />
<br />
  1. Edit $CATALINA\_HOME/conf/server.xml and add your realm:
```
  <Realm className="org.apache.catalina.realm.JAASRealm"
         appName="vt-ldap-authz"
         userClassNames="edu.vt.middleware.ldap.jaas.LdapPrincipal"
         roleClassNames="edu.vt.middleware.ldap.jaas.LdapRole" />
```
  1. Since a JAAS Realm is being used, create a JAAS config file, e.g. $CATALINA\_HOME/jaas.config:
```
  vt-ldap-authz {
   edu.vt.middleware.ldap.jaas.LdapDnAuthorizationModule required
     storePass="true"
     ldapUrl="ldap://authn.directory.vt.edu/ou=people,dc=vt,dc=edu"
     tls="true"
     userField="uupid";
   edu.vt.middleware.ldap.jaas.LdapRoleAuthorizationModule required
     useFirstPass="true"
     ldapUrl="ldap://authn.directory.vt.edu/ou=groups,dc=vt,dc=edu"
     tls="true"
     roleFilter="(member={0})"
     roleAttribute="uugid";
  };
```
  1. Set the `java.security.auth.login.config` system property to point to the JAAS config file by editing $CATALINA\_HOME/bin/setenv.sh:
```
  CATALINA_OPTS="$CATALINA_OPTS -Djava.security.auth.login.config=$CATALINA_HOME/jaas.config"
  export CATALINA_OPTS
```
  1. Copy vt-ldap.jar containing the JAAS classes used above to $CATALINA\_HOME/lib.

## Web Application ##
Update your web.xml to include security directives for the new authentication method.
  1. Edit your web.xml:
```
  <security-constraint>
    <web-resource-collection>
      <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>ROLE-FROM-LDAP</role-name>
    </auth-constraint>
  </security-constraint>
  <login-config>
    <auth-method>REMOTE-USER</auth-method>
  </login-config>
```

Your web application is now ready to accept the REMOTE\_USER header from an external source and populate role data from a realm.


---

# Notes #
  * Any URLs that are protected but do not receive the REMOTE\_USER header will fail authentication and the user will see a HTTP 403 error.
  * If you are using the Tomcat AJP connector and getting a 401 "Principal not found" error, then the REMOTE\_USER header is absent from the request.  This can commonly be fixed by adding tomcatAuthentication="false" to the AJP connector in server.xml:
```
  <Connector
    protocol="org.apache.coyote.ajp.AjpAprProtocol"
    port="8009"
    maxThreads="150"
    connectionTimeout="10000"
    enableLookups="false"
    tomcatAuthentication="false" />
```