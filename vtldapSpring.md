

# Spring Integration #
The VT LDAP library was designed with Spring integration in mind.  Most if not all classes are Javabean compliant, and therefore are candidates for configuration via Spring XML context files.

## Basic Configuration ##
The following Spring context XML provides a basic example of wiring up an Ldap object for use in a Spring application.
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:p="http://www.springframework.org/schema/p"
  xmlns:util="http://www.springframework.org/schema/util"
  xsi:schemaLocation="
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">

  <bean id="ldap"
    class="edu.vt.middleware.ldap.Ldap"
    p:ldapConfig-ref="ldapConfig"
  />
  
  <bean id="ldapConfig"
    class="edu.vt.middleware.ldap.LdapConfig"
    p:ldapUrl="ldap://ed-dev.middleware.vt.edu:10389"
    p:tls="true"
    p:authtype="EXTERNAL"
    p:searchScope="SUBTREE">
    <property name="sslSocketFactory">
      <bean class="edu.vt.middleware.ldap.ssl.TLSSocketFactory"
        init-method="initialize">
        <property name="SSLContextInitializer">
          <bean
            factory-bean="sslContextInitializerFactory"
	    factory-method="createSSLContextInitializer" />
        </property>
      </bean>
    </property>
  </bean>
  
  <bean id="sslContextInitializerFactory"
    class="edu.vt.middleware.ldap.ssl.KeyStoreCredentialConfig"
    p:keyStore="classpath:/edid.keystore"
    p:keyStoreType="BKS"
    p:keyStorePassword="changeit"
    p:trustStore="classpath:/vt-truststore.bks"
    p:trustStoreType="BKS"
    p:trustStorePassword="changeit"
  />
  
</beans>
```

## Pooling Example ##
```
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:p="http://www.springframework.org/schema/p"
  xmlns:util="http://www.springframework.org/schema/util"
  xsi:schemaLocation="
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.5.xsd">

  <bean id="ldapFactory"
    class="edu.vt.middleware.ldap.pool.DefaultLdapFactory">
    <constructor-arg index="0" ref="ldapConfig"/>
  </bean>
  
  <bean id="ldapPool"
    class="edu.vt.middleware.ldap.pool.BlockingLdapPool"
    init-method="initialize"
    p:blockWaitTime="5000">
    <constructor-arg index="0">
      <bean class="edu.vt.middleware.ldap.pool.LdapPoolConfig"
        p:minPoolSize="5"
        p:maxPoolSize="20"
        p:validatePeriodically="true"
        p:validateTimerPeriod="30000"
        p:expirationTime="600000"
        p:pruneTimerPeriod="60000"
      />
    </constructor-arg>
    <constructor-arg index="1" ref="ldapFactory"/>
  </bean>
  
  <bean id="ldapConfig"
    class="edu.vt.middleware.ldap.LdapConfig"
    p:ldapUrl="ldap://ed-dev.middleware.vt.edu:10389"
    p:tls="true"
    p:authtype="EXTERNAL"
    p:searchScope="SUBTREE">
    <property name="sslSocketFactory">
      <bean class="edu.vt.middleware.ldap.ssl.TLSSocketFactory"
        init-method="initialize">
        <property name="SSLContextInitializer">
          <bean
            factory-bean="sslContextInitializerFactory"
	    factory-method="createSSLContextInitializer" />
        </property>
      </bean>
    </property>
  </bean>

  <bean id="sslContextInitializerFactory"
    class="edu.vt.middleware.ldap.ssl.KeyStoreCredentialConfig"
    p:keyStore="classpath:/edid.keystore"
    p:keyStoreType="BKS"
    p:keyStorePassword="changeit"
    p:trustStore="classpath:/vt-truststore.bks"
    p:trustStoreType="BKS"
    p:trustStorePassword="changeit"
  />
</beans>
```