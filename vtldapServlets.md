

# Introduction #
This library includes servlets for searching and authentication for use with web applications.


---

# Login/Logout Servlets #
```
    <!-- Servlet which can be used to perform ldap authentication --> 
    <servlet>
      <servlet-name>Login</servlet-name>
      <servlet-class>edu.vt.middleware.ldap.servlets.LoginServlet</servlet-class>
      <!-- properties file to configure Authenticator with
           default is '/ldap.properties' --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.propertiesFile</param-name>
        <param-value>/my.ldap.properties</param-value>
      </init-param>
      <!-- session attribute to set which will contain the user identifier
           default is 'user' --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionId</param-name>
        <param-value>application.user</param-value>
      </init-param>
      <!-- URL of the page that collects user credentials
           default is '/' --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.loginUrl</param-name>
        <param-value>login.jsp</param-value>
      </init-param>
      <!-- Error message to display if authentication fails
           default is 'Could not authenticate or authorize user' --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.errorMsg</param-name>
        <param-value>Invalid credentials</param-value>
      </init-param>
      <!-- Class which extends
           edu.vt.middleware.ldap.servlets.session.SessionManager
           This param is optional, and only needed if additional
           session initialization is required
           By default the only session initialization that occurs is
           to set the sessionId to the user name --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionManager</param-name>
        <param-value>path.to.your.package.customSessionManager</param-value>
      </init-param>
    </servlet>
    <servlet-mapping>
      <servlet-name>Login</servlet-name>
      <url-pattern>/Login</url-pattern>
    </servlet-mapping>

    <!-- Servlet used to remove the session attribute set by Login --> 
    <servlet>
      <servlet-name>Logout</servlet-name>
      <servlet-class>edu.vt.middleware.ldap.servlets.LogoutServlet</servlet-class>
      <!-- session attribute to remove which contains the user identifier
           if set it must match the sessionId param set in the Login servlet
           default is 'user' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionId</param-name>
        <param-value>application.user</param-value>
      </init-param>
      <!-- Class which extends
           edu.vt.middleware.ldap.servlets.session.SessionManager
           This param is optional, and only needed if additional
           session cleanup is required
           By default the only session cleanup that occurs is
           to remove the sessionId -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.sessionManager</param-name>
        <param-value>path.to.your.package.customSessionManager</param-value>
      </init-param>
    </servlet>
    <servlet-mapping>
      <servlet-name>Logout</servlet-name>
      <url-pattern>/Logout</url-pattern>
    </servlet-mapping>
```

Sample HTML for using the login servlet:
```
    <form method="post" action="Login">
      <input type="text" name="user"/>
      <input type="password" name="credential"/>
      <input type="hidden" name="url" value="url to redirect to after login"/>
      <input type="submit" name="login" value="Login"/>
    </form>
```


---

# Search Servlet #
```
    <!-- Servlet which can be used to provide a LDAP gateway --> 
    <servlet-name>LdapSearch</servlet-name>
    <servlet-class>edu.vt.middleware.ldap.servlets.SearchServlet</servlet-class>
      <!-- properties file to configure SearchServlet with
           default is '/ldap.properties' --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.propertiesFile</param-name>
        <param-value>/my.ldap.properties</param-value>
      </init-param>
        <!-- properties file to configure SearchServlet pool with 
             there is no default --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.poolPropertiesFile</param-name>
        <param-value>/my.ldap.pool.properties</param-value>
      </init-param>
        <!-- type of pool to use;
             should be one of 'SHARED', 'BLOCKING', or 'SOFTLIMIT' -->
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.poolType</param-name>
        <param-value>SHARED</param-value>
      </init-param>
        <!-- output format of this servlet; should be one of 'LDIF' or 'DSML' --> 
      <init-param>
        <param-name>edu.vt.middleware.ldap.servlets.outputFormat</param-name>
        <param-value>LDIF</param-value>
      </init-param>
    </servlet>
    <servlet-mapping>
      <servlet-name>LdapSearch</servlet-name>
      <url-pattern>/LdapSearch</url-pattern>
    </servlet-mapping>
```

Perform a search for attribute 'sn=fisher' and return the 'givenName' and 'mail' attributes.<br />
```
http://www.server.com/LdapSearch?query=sn=fisher&attrs=givenname&attrs=mail
```

If you need to use complex queries then you must form encode the request.