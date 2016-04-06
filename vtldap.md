

<br><br><br>
<hr />
<h1>This project has finished incubation and has moved to <a href='http://www.ldaptive.org'>Ldaptive</a></h1>
<hr />
<br><br><br>

<h1>Introduction</h1>
VT Ldap is a Java library for interfacing with version 3 LDAPs.<br>
This library meets the following design goals:<br>
<ol><li>Easy and flexible to configure. Configuration can be done with a properties file located on your classpath or via Spring.<br>
</li><li>Ease of use for common ldap operations. Searching, comparing, adding, and replacing all require only a few lines of code.<br>
</li><li>Automatic connection handling. Timed out connections automatically retry and TLS/SSL is negotiated transparently.<br>
</li><li>Support for sasl operations. Digest-MD5, CRAM-MD5, GSSAPI, and EXTERNAL are all supported.<br>
</li><li>Command line interface for common operations.</li></ol>

<hr />
<h1>Installation</h1>
This project is available from Maven Central. If you would like to use this project in your maven build, include the following in your pom.xml:<br>
<pre><code>&lt;dependencies&gt;<br>
  &lt;dependency&gt;<br>
      &lt;groupId&gt;edu.vt.middleware&lt;/groupId&gt;<br>
      &lt;artifactId&gt;vt-ldap&lt;/artifactId&gt;<br>
      &lt;version&gt;3.3.9&lt;/version&gt;<br>
  &lt;/dependency&gt;<br>
&lt;dependencies&gt;<br>
</code></pre>

<hr />
<h1>Code Samples</h1>
Unless otherwise noted, classes appearing in the following samples are included in the JSE libraries or VT Ldap.<br>
<br>
<h2>Searching</h2>

<h3>Compare</h3>
Perform a compare on the entry 'uid=818037,ou=People,dc=vt,dc=edu', checking for an attribute value of 'mail=dfisher@vt.edu'<br>
<pre><code>Ldap ldap = new Ldap(<br>
  new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));<br>
if (ldap.compare("uid=818037,ou=People,dc=vt,dc=edu", new SearchFilter("mail=dfisher@vt.edu"))) {<br>
  System.out.println("Compare succeeded");<br>
} else {<br>
  System.out.println("Compare failed");<br>
}<br>
ldap.close();<br>
</code></pre>
<i>Note that in order to perform a real LDAP compare operation, your filter must be of the form '(name=value)'. Any other type of filter, using expressions or wildcards, will results in an object level ldap search. This will produce the desired result, but implementers examining their LDAP logs may notice this discrepancy.</i>

<h3>Subtree search</h3>
Perform a search for 'sn=Fisher' and output all attributes in LDIF format to System.out.<br>
<pre><code>Ldap ldap = new Ldap(<br>
  new LdapConfig("ldap://directory.vt.edu/dc=vt,dc=edu"));<br>
(new Ldif()).outputLdif(<br>
  ldap.search(new SearchFilter("sn=Fisher")),<br>
  new BufferedWriter(new OutputStreamWriter(System.out)));<br>
ldap.close();<br>
</code></pre>

<h3>Attribute search</h3>
Perform a search for all entries containing an attribute matching 'mail=dfisher@vt.edu', and output the attributes 'sn' and 'givenName' in DSMLv1 format to System.out.<br>
<pre><code>Ldap ldap = new Ldap(<br>
  new LdapConfig("ldap://directory.vt.edu", "ou=People,dc=vt,dc=edu"));<br>
(new Dsmlv1()).outputDsml(<br>
  ldap.searchAttributes(<br>
    AttributesFactory.createAttributes("mail", "dfisher@vt.edu"),<br>
    new String[]{"sn", "givenName"}),<br>
  new BufferedWriter(new OutputStreamWriter(System.out)));<br>
ldap.close();<br>
</code></pre>

See <a href='vtldapSearching.md'>Searching</a>

<h2>Authentication</h2>
Authenticate a user whose entry resides in the 'ou=People' branch of the ldap.<br>
TLS is required for all connections and the username must equal the attribute value of the attributes 'uid' or 'mail'.<br>
<pre><code>AuthenticatorConfig config = new AuthenticatorConfig(<br>
  "ldap://authn.directory.vt.edu", "ou=People,dc=vt,dc=edu");<br>
config.setTls(true);<br>
config.setUserField(new String[]{"uid", "mail"}); // attribute to search for user with<br>
Authenticator auth = new Authenticator(config);<br>
if (auth.authenticate(user, credential)) {<br>
  System.out.println("Authentication succeeded");<br>
} else {<br>
  System.out.println("Authentication failed");<br>
}<br>
</code></pre>

Same example as above, but also authorize the user on 'eduPersonAffiliation'.<br>
<pre><code>AuthenticatorConfig config = new AuthenticatorConfig(<br>
  "ldap://authn.directory.vt.edu", "ou=People,dc=vt,dc=edu");<br>
config.setTls(true);<br>
config.setUserFilter("(|(uid={0})(mail={0}))"); // attribute to search for user with<br>
Authenticator auth = new Authenticator(config);<br>
if (auth.authenticate(user, credential, new SearchFilter("eduPersonAffiliation=staff"))) {<br>
  System.out.println("Authentication/Authorization succeeded");<br>
} else {<br>
  System.out.println("Authentication/Authorization failed");<br>
}<br>
</code></pre>

See <a href='vtldapAuthentication.md'>Authentication</a>

<h2>Pooling</h2>
Create a new soft limit pool, accepting all the default configuration properties. Check a ldap object out from the pool, perform a search, and return the object to the pool.<br>
<pre><code>DefaultLdapFactory factory = new DefaultLdapFactory(<br>
  new LdapConfig("ldap://directory.vt.edu/ou=People,dc=vt,dc=edu"));<br>
SoftLimitLdapPool pool = new SoftLimitLdapPool(factory);<br>
pool.initialize();<br>
Ldap ldap = null;<br>
try {<br>
  ldap = pool.checkOut();<br>
  ...<br>
  Iterator&lt;SearchResult&gt; i = ldap.search(<br>
    new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});<br>
  ...<br>
} catch (LdapPoolException e) {<br>
  log.error("Error using the ldap pool.", e);<br>
} finally {<br>
  pool.checkIn(ldap);<br>
}<br>
...<br>
pool.close();<br>
</code></pre>

See <a href='vtldapPooling.md'>Pooling</a>

<hr />
<h1>Scripts</h1>
Script execution requirements vary by platform.  For the following platform-specific instructions, let VTLDAP_HOME be the location where the VT Ldap distribution was unpacked.<br>
<br>
<b>Unix</b>
<ol><li>Ensure the java executable is on your path.<br>
</li><li>Ensure $VTLDAP_HOME/bin is on your path.<br>
</li><li>If you encounter classpath problems executing the scripts, export VTLDAP_HOME as a separate shell variable.  This is not necessary in most cases (e.g. Linux, OSX, FreeBSD).</li></ol>

<h2>ldapsearch - Search Operations</h2>
Perform a subtree search for any entry containing 'mail=dfisher@vt.edu' and return the attributes givenName and sn in LDIF format.<br>
<pre><code>ldapsearch -ldapUrl ldap://directory.vt.edu/ou=People,dc=vt,dc=edu -query mail=dfisher@vt.edu givenName sn<br>
</code></pre>

<h2>ldapauth - Authentication Operations</h2>
Authenticate a user using the 'mail' attribute. You will be prompted for username and credential. Note that your credential will be visible in your terminal.<br>
<pre><code>ldapauth -ldapUrl ldap://authn.directory.vt.edu -baseDn ou=People,dc=vt,dc=edu -tls true -userFilter mail={0}<br>
</code></pre>