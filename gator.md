#summary Features, installation, and use of Gator log aggregating server application.
#labels Gator,log4j

![http://vt-middleware.googlecode.com/svn/wiki/images/gator.png](http://vt-middleware.googlecode.com/svn/wiki/images/gator.png)

# Download #
Gator is offered as a source archive since many of the configuration files may be of interest for customization via the recommended Maven War Overlay deployment process.  All versions are available in the [downloads](http://code.google.com/p/vt-middleware/downloads/list) area.  We recommend reading over the [release notes](gatorReleaseNotes.md) prior to download.

# Features #
Gator provides the following features/services:
  * Single point of configuration for log4j logging across multiple applications on multiple hosts
  * Aggregated logging from all applications/nodes on the host running Gator
  * Web-based configuration manager
  * Real-time Web-based log viewer
  * Project-specific ACLs (New in version 1.2)

# Background #
A bit of background may help put the features above in context.  Gator evolved from an amalgam of JBoss and the log4j [SocketServer](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/net/SocketServer.html) to address our need for consolidated logging across multiple nodes and tiers of an enterprise Java application.  We chose this solution for a few reasons:
  1. Wanted per-node logging and consolidated logging for redundancy
  1. The timestamps on the nodes were required to match exactly those on the server
  1. We did not have control of syslog configuration on our hosts
Although the application used commons logging internally, we used the log4j engine underneath.  This made us secure in choosing a log4j-specific solution.  Although the end result suited our needs, it was a pain to configure.  Our solution required a JBoss container instance for each instance (development, pre-production, production) of our application, where each JBoss instance had a slightly different configuration.  And there were also 2 log4j configuration files for each server, one for the container and one for the nodes.  This is where it got dicey.  There were some very non-intuitive requirements on appender configuration and log levels of categories that only 1 person in the group understood well enough to troubleshoot.  But it worked once it was up and running, and it performed well.

And then we would need to modify the logger configuration for a node or aspect of the application.  The process was both tedious and error prone, and again, we had to lean on a single person in the group to help troubleshoot configuration errors.  After running into this problem a few times, and getting more insight into the architecture of our solution, I had a hunch we could do better.  And then the outline of Gator emerged from a week or so of hacking on a JDBC-backed log4j configuration engine and a new [SocketServer](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-core/src/main/java/edu/vt/middleware/gator/log4j/SocketServer.java) implementation that understands the notion of a project containing both a log4j logger hierarchy and allowed clients.

# Installation #
Gator is packaged as a Java Web Application Archive (WAR) and is deployable in a servlet container such as [Tomcat](http://tomcat.apache.org/) out of the box using sensible defaults:
  * In-memory HSQL database
  * Gator logs written to gator-logs directory in current working directory of container process
  * Static XML-based user/role configuration for authentication (**admin**/**admin** by default)

The basic configuration of Gator can be controlled via servlet context configuration parameters.  A [sample context configuration file](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator.xml.sample) is provided for Tomcat that contains extensive comments describing the purpose for each configuration value.  The following configuration aspects can be controlled via context parameters:
  * Database platform and pooling configuration
  * Socket server listening address, port, and capacity configuration
  * Configuration of key filesystem paths needed by Gator
  * Pluggable application policy settings

For production deployments where greater configuration control is needed, we strongly encourage the use of the [Maven War Overlay](http://maven.apache.org/plugins/maven-war-plugin/examples/war-overlay.html) method for managing customizations.  Gator uses the [Spring Framework](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/), which provides convenient configuration and extension points through XML configuration files.  The following listing describes the XML configuration files (located in the WEB-INF directory) that control various aspects of the application:

  * [applicationContext.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/applicationContext.xml) - Main Spring application context configuration that defines core Gator components
  * [applicationContext-authz.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/applicationContext-authz.xml) - Spring Security configuration for business object method invocation security
  * [gator-servlet.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/gator-servlet.xml) - Spring Web MVC servlet configuration
  * [securityContext.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/securityContext.xml) - Spring Security configuration of users, roles, and other authentication/authorization concerns
  * [servletContextParams.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/servletContextParams.xml) - Provides sensible defaults for out-of-box deployment
  * [web.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/web.xml) - Java servlet configuration

For all but the most advanced deployments, it should be sufficient to customize [securityContext.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/securityContext.xml) and [web.xml](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/gator-webapp/src/main/webapp/WEB-INF/web.xml) in order to customize security configuration.  For example it is expected that enterprise deployments would integrate with a centralized authentication/authorization provider such as LDAP for security configuration.  Since LDAP is expected to be a common case, a [sample LDAP security configuration file](http://code.google.com/p/vt-middleware/source/browse/gator/tags/gator-1.2/securityContext-ldap.xml.sample) is provided.  Any security provider supported by [Spring Security](http://static.springsource.org/spring-security/site/docs/3.0.x/reference/springsecurity.html) is supported in Gator.  For example at Virginia Tech we leverage [Spring Security's CAS integration](http://static.springsource.org/spring-security/site/docs/3.0.x/reference/cas.html) to provide authentication and authorization services from our instance of [CAS](http://www.jasig.org/cas/).

# User Guide #
There are two considerations to using gator for collecting log data:
  1. Create a Gator project that describes logging and security configuration.
  1. Configure client applications to obtain logging configuration from Gator project.

## Gator Projects ##
A gator project is simply a named container for the following data:
  * Log4j logger hierarchy configuration
  * List of clients allowed to connect to server to send logging events
  * Security configuration (view/modify/delete project configuration)

The log4j configuration consists of the usual appender and category configuration elements common to existing log4j configuration techniques (e.g. properties file, XML).  Gator allows a non-standard configuration option named "Client Log Directory" which is an absolute path appended to file appender paths in the configuration given to clients.  The rational for this feature is that it is assumed that paths to client log files is different than those on the Gator server.

A project must define the host names or IP addresses of clients allowed to connect to the Gator socket server in order to send logging events.  Gator currently supports at most one Gator-enabled project per logical host.  If an unauthorized client attempts to connect to the socket server, the client socket is immediately closed.

## Client Configuration ##
Clients MUST support XML-based hierarchy configuration from an HTTP resource.  (The log4j [DOMConfigurator](http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/DOMConfigurator.html) supports this out of the box.)  Clients SHOULD support periodic polling of XML configuration files in order to detect configuration changes made by the Gator Web interface.  (The JBoss org.jboss.logging.Log4jService MBean has support for this feature, for example.)

The URL for a Gator project log4j XML configuration file is specified by the following pattern:

http://GATOR_HOST_NAME:PORT/project/PROJECT_NAME/log4j.xml

An example for a Gator client that is deployed to a JBoss 5 AS JEE container:
```
<mbean code="org.jboss.logging.Log4jService"
    name="jboss.system:type=Log4jService,service=Logging"
    xmbean-dd="resource:xmdesc/Log4jService-xmbean.xml">

  <attribute name="ConfigurationURL">http://log.middleware.vt.edu/gator/project/j2ee-dev/log4j.xml</attribute>
  <!-- Set the org.apache.log4j.helpers.LogLog.setQuiteMode. As of log4j1.2.8
  this needs to be set to avoid a possible deadlock on exception at the
  appender level. See bug#696819.
  -->
  <attribute name="Log4jQuietMode">true</attribute>
  <!-- How frequently in seconds the ConfigurationURL is checked for changes -->
  <attribute name="RefreshPeriod">60</attribute>

  <!-- The value to assign to system property jboss.server.log.threshold
       if it is not already set. This system property in turn controls
       the logging threshold for the server.log file.
       If the system property is already set when this service is created,
       this value is ignored. -->
  <attribute name="DefaultJBossServerLogThreshold">INFO</attribute>
</mbean>

```