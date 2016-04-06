

# Introduction #
This library includes servlet filters for several commons tasks.


---

# Installation #
The latest version can be downloaded from the [downloads](http://code.google.com/p/vt-middleware/downloads/list) page.

If you would like to use this project in your maven build, include the following in your pom.xml:
```
<dependencies>
  <dependency>
      <groupId>edu.vt.middleware</groupId>
      <artifactId>vt-servlet-filters</artifactId>
      <version>2.0.2</version>
  </dependency>
<dependencies>
```
```
<repositories>
  <repository>
    <id>vt-middleware.repo</id>
    <url>http://vt-middleware.googlecode.com/svn/maven2</url>
  </repository>
</repositories>
```


---

# CharacterEncodingFilter #
**Since 2.0.1**

This filter allows setting the character set encoding of HTTP servlet requests and/or responses by calling [ServletRequest#setCharacterEncoding(String)](http://java.sun.com/javaee/5/docs/api/javax/servlet/ServletRequest.html#setCharacterEncoding(java.lang.String))/[ServletResponse#setCharacterEncoding(String)](http://java.sun.com/javaee/5/docs/api/javax/servlet/ServletResponse.html#setCharacterEncoding(java.lang.String)).  Note that the request and response encoding are controlled independently, such that one can be set without the other.  In most cases, however, it would be desirable to set both to the same encoding.
```
<!--
This filter should be placed at the head of the filter chain, if possible, to ensure that
all downstream filters have the proper encoding.
-->
<filter>
  <filter-name>CharacterEncodingFilter</filter-name>
  <filter-class>edu.vt.middleware.servlet.filter.CharacterEncodingFilter</filter-class>
  <!-- 
    Sets the character encoding of the request to the given Java character set name.
    Name must be understood by java.nio.charset.Charset class, e.g.,
    ISO-8859-1, UTF-8, UTF-16.
  -->
  <init-param>
    <param-name>requestCharsetName</param-name>
    <param-value>UTF-8</param-value>
  </init-param>
  <!-- 
    Sets the character encoding of the response to the given Java character set name.
    Name must be understood by java.nio.charset.Charset class, e.g.,
    ISO-8859-1, UTF-8, UTF-16.
  -->
  <init-param>
    <param-name>responseCharsetName</param-name>
    <param-value>UTF-8</param-value>
  </init-param>
</filter>
```


---

# ClientCertFilter #
This filter allows you to restrict access to a servlet based on some attributes of a client certificate.
```
<filter>
  <filter-name>ClientCertFilter</filter-name>
  <filter-class>edu.vt.middleware.filters.ClientCertFilter</filter-class>
  <!-- Whether the request *must* send a client certificate in order to pass through this filter -->
  <init-param>
    <param-name>requireCert</param-name>
    <param-value>true</param-value>
  </init-param>
  <!-- Regular expression which the subject DN of the client certificate must match in order to pass through this filter -->
  <init-param>
    <param-name>subjectDn</param-name>
    <param-value>DC=edu, DC=vt, DC=middleware, CN=.*</param-value>
  </init-param>
  <!-- Regular expression which the issuer DN of the client certificate must match in order to pass through this filter -->
  <init-param>
    <param-name>issuerDn</param-name>
    <param-value>DC=edu, DC=vt, DC=middleware, CN=Middleware CA</param-value>
  </init-param>
</filter>
```


---

# RequestDumperFilter #
The request dumper filter is a modified version of RequestDumperFilter example source that ships with the [Apache Tomcat](http://tomcat.apache.org/) servlet container distribution.  This filter is different in two notable aspects:
  1. Can dump the full request, including any data in the POST body
  1. Uses [Commons Logging](http://commons.apache.org/logging/) to log request data

Request dumper filter configuration is very straightforward, an involves editing the application web.xml file similar to the following example.
```
  <filter>
    <filter-name>Request Dumper Filter</filter-name>
    <filter-class>edu.vt.middleware.servlet.filter.RequestDumperFilter</filter-class>
  </filter>
  <filter-mapping>
    <filter-name>Request Dumper Filter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

Since this filter uses commons logging to print request data, the logging engine underlying commons logging must be set to TRACE for the edu.vt.middleware.servlet.filter.RequestDumperFilter category.  The following log4j.properties file provides a configuration sample for the log4j logging engine.
```
log4j.rootLogger=ERROR, stdout, logfile

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - <%m>%n

log4j.appender.logfile=org.apache.log4j.DailyRollingFileAppender
log4j.appender.logfile.File=/home/marvin/logs/cas.log
log4j.appender.logfile.DatePattern='.'yyyy-MM-dd
log4j.appender.logfile.Append=false
log4j.appender.logfile.layout=org.apache.log4j.PatternLayout
log4j.appender.logfile.layout.ConversionPattern=%d %-5p [%c] - %m%n

log4j.logger.com.example.application=DEBUG
log4j.logger.edu.vt.middleware.servlet.filter.RequestDumperFilter=TRACE
```

## Performance Considerations ##
The HTTP request stream containing the POST body can only be read once, so it must be read into a buffer to both print it and pass it to downstream filters and ultimately into the servlet that services the request.  This may have considerable performance impact on application throughput, particularly for applications that transfer large amounts of data via HTTP (e.g. file upload).

The filter will only attempt to buffer and print the request if the edu.vt.middleware.servlet.filter.RequestDumperFilter category is set to TRACE:

```
  public void doFilter(
    final ServletRequest request,
    final ServletResponse response,
    final FilterChain chain)
    throws IOException, ServletException
  {
    if (this.config == null) {
      return;
    }

    // Just pass through to next filter if we're not at TRACE level
    if (!logger.isTraceEnabled()) {
      chain.doFilter(request, response);
      return;
    }
...
}
```
It is safe to leave the filter configured in the web.xml of a production application, while the logger configuration may be turned up to TRACE to engage the filter as needed to dump request data.


---

# RequestMethodFilter #
This filter allows you to restrict access to a servlet based on data from the servlet request object.<br />
Each param-name represents a method to be called on the request object, while the param-value is a regular expression the result of the method call must match.<br />
If you need to pass parameter(s) into the method, then they should appear after the method name using whitespace as a delimiter.
```
<filter>
  <filter-name>RequestMethodFilter</filter-name>
  <filter-class>edu.vt.middleware.filters.RequestMethodFilter</filter-class>
  <!-- Call ServletRequest.getRemoteAddr(), it's result must match 10.0.10.* --> 
  <init-param>
    <param-name>getRemoteAddr</param-name>
    <param-value>10.0.10.*</param-value>
  </init-param>
  <!-- Call HttpServletRequest.getMethod(), it's result must match 'GET' --> 
  <init-param>
    <param-name>getMethod</param-name>
    <param-value>[Gg][Ee][Tt]</param-value>
  </init-param>
  <!-- Call HttpServletRequest.getHeader('user-agent'), it's result must match '.*Mozilla.*' --> 
  <init-param>
    <param-name>getHeader user-agent</param-name>
    <param-value>.*Mozilla.*</param-value>
  </init-param>
</filter>
```


---

# SessionAttributeFilter #
This filter allows you to restrict access to a servlet based on attributes from the HTTP session object.<br />
Each param-name represents an attribute name and regular expression value, while the param-value is a URL to forward the request to if the regular expression match fails.
```
<filter>
  <filter-name>SessionAttributeFilter</filter-name>
  <filter-class>edu.vt.middleware.filters.SessionAttributeFilter</filter-class>
  <!-- Whether the attribute(s) *must* exist in order to pass through this filter -->
  <init-param>
    <param-name>requireAttribute</param-name>
    <param-value>true</param-value>
  </init-param>
  <!-- Call HttpSession.getAttribute("user"), it's result must match .*, otherwise forward request to login.jsp --> 
  <init-param>
    <param-name>user .*</param-name>
    <param-value>/login.jsp</param-value>
  </init-param>
</filter>
```