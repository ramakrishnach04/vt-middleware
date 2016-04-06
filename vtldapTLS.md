

# Introduction #
This library utilizes standard JVM mechanisms for both trust and authentication of secure connections. Depending on the degree of customization required, there are several solutions for controlling who the client trusts and how the client authenticates over secure channels.

# SSL versus TLS #
TLS allows the client to upgrade and downgrade the security of the connection as needed. Consequently the implementation of TLS in JNDI is very different from the implementation of SSL.

## Using SSL ##
The default LDAP SSL port is 636 and creating an SSL connection is simple:
```
LdapConfig config = new LdapConfig("ldap://directory.vt.edu:636");
config.setSsl(true);
config.setSslSocketFactory(new CustomSSLSocketFactory());
```

The underlying implementation is to add `java.naming.security.protocol=ssl` to the JNDI context and if an SSLSocketFactory has been set `java.naming.ldap.factory.socket=CustomSSLSocketFactory.getClass().getName()` is also added. The implication of this implementation is that when using SSL your socket factory must be able to statically initialize itself. This means hard coding many parameters, which makes using custom SSL configurations more difficult.

## Using TLS ##
Since TLS is used to upgrade an existing insecure connection a different port is typically not used:
```
LdapConfig config = new LdapConfig("ldap://directory.vt.edu:389");
config.setTls(true);
config.setSslSocketFactory(new CustomSSLSocketFactory());
```

The underlying implementation is to to call `LdapContext.extendedOperation(new StartTlsRequest())` and to ultimately negotiate the connection with a custom SSLSocketFactory if one was provided. This provides a great deal more flexibility as the SSLSocketFactory object provided to the LdapConfig is simply passed into the TLS response.<br /><br />

# Trust/Authentication Solutions #
When using SSL or TLS trust errors are very common. The client must be configured to trust the server and when performing client authentication, the server must be configured to trust the client. This sections deals with how to configure your LDAP client with the proper trust and authentication material.<br /><br />
The following examples assume you are attempting to connect to a host that is using a certificate **not** signed by the CAs included with the standard Java installation.

## Java cacerts ##
You can add either the server certificate or the server certificate's CA to the cacerts file included with your Java installation. This is simplest solution, but be aware that it impacts **all** secure connections made by the JVM.
```
keytool -import -file $PATH_TO_CERT -keystore $JAVA_HOME/jre/lib/security/cacerts -alias my_server_cert
```

## Command line options ##
Java supports command line options for designating both the truststore and keystore to be used for secure connections. Note that this impacts **all** secure connections made by the JVM.
```
java -Djavax.net.ssl.keyStore=$PATH_TO/my.keystore -Djavax.net.ssl.trustStore=$PATH_TO/my.truststore
```

When performing client authentication the JVM will select the first certificate in my.keystore that matches the allowed CAs supplied by the server.

## TLSSocketFactory ##
This library includes an implementation of SSLSocketFactory and several utility classes to make the use of keystores and X509 credentials easier. This solution has the potential benefit of not impacting any other connections in the JVM. Each connection is provided the socket factory when it is created.

### Examples ###

Use a custom truststore for TLS connections that is located on the classpath.

```
LdapConfig config = new LdapConfig("ldap://directory.vt.edu/dc=vt,dc=edu");
config.setTls(true);

KeystoreCredentialConfig cc = new KeyStoreCredentialConfig();
cc.setTrustStore("classpath:/my.truststore");

TLSSocketFactory sf = new TLSSocketFactory();
sf.setSSLContextIntializer(cc.createSSLContextInitializer());
sf.initialize();

config.setSslSocketFactory(sf);
```

Use X509 certificates for both authentication and trust that are located on the file system.
  * Supported certificate formats include: PEM, DER, and PKCS7
  * Supported private key formats include: PKCS8

```
LdapConfig config = new LdapConfig("ldap://directory.vt.edu/dc=vt,dc=edu");
config.setTls(true);
config.setAuthtype("EXTERNAL");

X509CredentialConfig cc = new X509CredentialConfig();
cc.setTrustCertificates("file:/tmp/certs.pem");
cc.setAuthenticationCertificate("file:/tmp/mycert.pem");
cc.setAuthenticationKey("file:/tmp/mykey.pkcs8");

TLSSocketFactory sf = new TLSSocketFactory();
sf.setSSLContextIntializer(cc.createSSLContextInitializer());
sf.initialize();

config.setSslSocketFactory(sf);
```

_If you prefer to work directly with InputStreams see CredentialReader._

Properties can also be used to configure trust and authentication material.
```
edu.vt.middleware.ldap.sslSocketFactory=edu.vt.middleware.ldap.ssl.TLSSocketFactory
  {edu.vt.middleware.ldap.ssl.KeyStoreCredentialConfig{{trustStore=/tmp/my.truststore}{trustStoreType=BKS}}}
```

The default SSLSocketFactory used in the property invoker is the TLSSocketFactory and the default CredentialConfig is the X509CredentialConfig. Consequently, if you want to use X509 material, the configuration can be abbreviated:
```
edu.vt.middleware.ldap.sslSocketFactory={{trustCertificates=/tmp/certs.pem}}
```

# Hostname Validation #
[RFC 2830](http://www.ietf.org/rfc/rfc2830.txt) section 3.6 specifies how hostnames should be validated for startTLS. No such RFC exists for LDAPS and JNDI does not perform any hostname checks after the SSL handshake. To solve this problem [hostname validation](http://code.google.com/p/vt-middleware/source/browse/vt-ldap/branches/vt-ldap-3/src/main/java/edu/vt/middleware/ldap/ssl/DefaultHostnameVerifier.java) is performed with a trust manager when an LDAPS connection is detected. The validation rules use the same implementation as startTLS:

  * if hostname is IP, then cert must have exact match IP subjAltName
  * hostname must match any DNS subjAltName if any exist
  * hostname must match the first CN
  * if cert begins with a wildcard, domains are used for matching

If you determine that hostname validation is not required in your environment this behavior can be disabled or customized by providing a your own SSLSocketFactory implementation or by specifying the default JNDI SSLSocketFactory:

```
java.naming.ldap.factory.socket=javax.net.ssl.SSLSocketFactory
```

Note that using the `edu.vt.middleware.ldap.ssl.sslSocketFactory` property has no affect on LDAPS connections. That property only impacts connections that use startTLS.

# Useful Links #
  * http://java.sun.com/j2se/1.5.0/docs/guide/security/jsse/JSSERefGuide.html