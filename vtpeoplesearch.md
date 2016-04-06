

<br><br><br>
<hr />
<h1>This project has finished incubation and has moved to <a href='http://www.ldaptive.org/docs/guide/templates'>Ldaptive</a></h1>
<hr />
<br><br><br>

<h1>Introduction</h1>
VT People Search is a Java servlet that exposes a fuzzy logic search interface to an LDAP. <br />
Results can be returned in LDIF, DSML version 1, or DSML version 2 format. <br />
This provides a flexible means for displaying and parsing search results for display. <br />
<br />
Queries are executed based on the number of search terms that are supplied. <br />
For each configured search term count a specified number of queries are run in order.<br />
Results are then returned either additively or on a first hit basis.<br>
<br>
<hr />
<h1>Installation</h1>
The latest version can be downloaded from the <a href='http://code.google.com/p/vt-middleware/downloads/list'>downloads</a> page.<br>
<br>
<h2>War</h2>
To configure the war, edit the following files:<br>
<ul><li>WEB-INF/web.xml<br>
</li><li>WEB-INF/classes/peoplesearch.ldap.properties<br>
</li><li>WEB-INF/classes/peoplesearch.ldap.pool.properties (optional)<br>
</li><li>WEB-INF/classes/peoplesearch-context.xml</li></ul>

Deploy the war in a servlet container.<br>
<br>
<h2>Jar</h2>
If you would like to use this project's jar library in your maven build, include the following in your pom.xml:<br>
<pre><code>&lt;dependencies&gt;<br>
  &lt;dependency&gt;<br>
      &lt;groupId&gt;edu.vt.middleware&lt;/groupId&gt;<br>
      &lt;artifactId&gt;vt-peoplesearch&lt;/artifactId&gt;<br>
      &lt;version&gt;3.0.1&lt;/version&gt;<br>
  &lt;/dependency&gt;<br>
&lt;dependencies&gt;<br>
</code></pre>
<pre><code>&lt;repositories&gt;<br>
  &lt;repository&gt;<br>
    &lt;id&gt;vt-middleware.repo&lt;/id&gt;<br>
    &lt;url&gt;http://vt-middleware.googlecode.com/svn/maven2&lt;/url&gt;<br>
  &lt;/repository&gt;<br>
&lt;/repositories&gt;<br>
</code></pre>

<hr />
<h1>Configuration</h1>

<h2>Servlet</h2>
The SearchServlet accepts the following configuration parameters:<br>
<pre><code>  &lt;servlet&gt;<br>
    &lt;servlet-name&gt;PeopleSearch&lt;/servlet-name&gt;<br>
    &lt;servlet-class&gt;edu.vt.middleware.ldap.search.servlets.SearchServlet&lt;/servlet-class&gt;<br>
    &lt;!-- Type output to produce.  Acceptable values are:<br>
         DSML and LDIF --&gt;<br>
    &lt;init-param&gt;<br>
      &lt;param-name&gt;edu.vt.middleware.ldap.search.outputType&lt;/param-name&gt;<br>
      &lt;param-value&gt;DSML&lt;/param-value&gt;<br>
    &lt;/init-param&gt;<br>
    &lt;!-- Classpath location of the peoplesearch spring context --&gt;<br>
    &lt;init-param&gt;<br>
      &lt;param-name&gt;edu.vt.middleware.ldap.search.springContextPath&lt;/param-name&gt;<br>
      &lt;param-value&gt;/peoplesearch-context.xml&lt;/param-value&gt;<br>
    &lt;/init-param&gt;<br>
    &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;<br>
  &lt;/servlet&gt;<br>
</code></pre>

<h2>Library</h2>
The PeopleSearch bean is configured via Spring context.<br />
Simple regular expression terms have been defined to aid in writing queries.<br />
Place the strings:<br>
<ul><li><code>@@@QUERY_N@@@</code> to indicate the nth query parameter<br>
</li><li><code>@@@INITIAL_N@@@</code> to indicate the first letter of the nth query parameter</li></ul>

<i>Regex Examples:</i>
<pre><code>(&amp;amp;(givenName=@@@QUERY_1@@@)(sn=@@@QUERY_2@@@))<br>
</code></pre>
search for a givenName matching the first term and a last name matching the second term<br>
<pre><code>(|(&amp;amp;(givenName=@@@INITIAL_1@@@*)(sn=@@@QUERY_2@@@))(&amp;amp;(middleName=@@@INITIAL_1@@@*)(sn=@@@QUERY_2@@@)))<br>
</code></pre>
search for a givenName or middleName starting with the first letter of the first search term and a last name matching the second search term<br>
<br>
<h3>SearchInvoker Bean</h3>
The SearchInvoker contains all the configured Search beans:<br>
<pre><code>  &lt;bean id="searchInvoker" class="edu.vt.middleware.ldap.search.SearchInvoker"&gt;<br>
    &lt;!--  Search configuration is defined in the following beans --&gt;<br>
    &lt;property name="searches"&gt;<br>
      &lt;map&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;1&lt;/value&gt;&lt;/key&gt;<br>
          &lt;ref bean="oneTermSearch"/&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;2&lt;/value&gt;&lt;/key&gt;<br>
          &lt;ref bean="twoTermSearch"/&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;3&lt;/value&gt;&lt;/key&gt;<br>
          &lt;ref bean="threeTermSearch"/&gt;<br>
        &lt;/entry&gt;<br>
      &lt;/map&gt;<br>
    &lt;/property&gt;<br>
  &lt;/bean&gt;<br>
</code></pre>

<h4>Proxy SASL Authorization</h4>
The SearchInvoker can be configured to perform SASL authorization on behalf on the client.<br />
Used in conjunction with the LDAP configuration, this will allow clients to proxy authentication to the LDAP.<br />
Add the following property to enable this feature:<br>
<pre><code>    &lt;property name="proxySaslAuthorization"&gt;<br>
      &lt;value&gt;true&lt;/value&gt;<br>
    &lt;/property&gt;<br>
</code></pre>
If using the SearchServlet, this setting will pass the <i>CN</i> of a supplied certificate as the LDAP SASL authorization ID.<br>
<br>
<h3>Search Bean</h3>
Contains all the queries to execute if one search term is received.<br>
<pre><code>  &lt;bean id="oneTermSearch" class="edu.vt.middleware.ldap.search.Search"&gt;<br>
    &lt;property name="termCount"&gt;&lt;value&gt;1&lt;/value&gt;&lt;/property&gt;<br>
<br>
    &lt;!-- Whether a query should use results from all searches or<br>
         just the results from the first match --&gt;<br>
    &lt;property name="additive"&gt;<br>
      &lt;value&gt;true&lt;/value&gt;<br>
    &lt;/property&gt;<br>
<br>
    &lt;property name="queries"&gt;<br>
      &lt;map&gt;<br>
        &lt;!-- phone number search --&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;1&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(telephoneNumber=*@@@QUERY_1@@@)(localPhone=*@@@QUERY_1@@@))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;!-- name search --&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;2&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(givenName=*@@@QUERY_1@@@*)(sn=*@@@QUERY_1@@@*))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;!-- email search --&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;3&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(uupid=*@@@QUERY_1@@@*)(mail=*@@@QUERY_1@@@*))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
      &lt;/map&gt;<br>
    &lt;/property&gt;<br>
  &lt;/bean&gt;<br>
</code></pre>
Contains all the queries to execute if two search terms are received.<br>
<pre><code>  &lt;bean id="twoTermSearch" class="edu.vt.middleware.ldap.search.Search"&gt;<br>
    &lt;property name="termCount"&gt;&lt;value&gt;2&lt;/value&gt;&lt;/property&gt;<br>
<br>
    &lt;property name="queries"&gt;<br>
      &lt;map&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;1&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(&amp;amp;(givenName=*@@@QUERY_1@@@*)(sn=*@@@QUERY_2@@@*))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;2&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(cn=*@@@QUERY_1@@@* *@@@QUERY_2@@@*)&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;3&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(&amp;amp;(givenName=@@@INITIAL_1@@@*)(sn=@@@QUERY_2@@@))(&amp;amp;(middleName=@@@INITIAL_1@@@*)(sn=@@@QUERY_2@@@)))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;4&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(sn=@@@QUERY_2@@@)&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
      &lt;/map&gt;<br>
    &lt;/property&gt;<br>
  &lt;/bean&gt;<br>
</code></pre>
Contains all the queries to execute if three search terms are received.<br>
<pre><code>  &lt;bean id="threeTermSearch" class="edu.vt.middleware.ldap.search.Search"&gt;<br>
    &lt;property name="termCount"&gt;&lt;value&gt;3&lt;/value&gt;&lt;/property&gt;<br>
<br>
    &lt;property name="queries"&gt;<br>
      &lt;map&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;1&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(&amp;amp;(givenName=*@@@QUERY_1@@@*)(sn=*@@@QUERY_3@@@*))(&amp;amp;(givenName=*@@@QUERY_2@@@*)(sn=*@@@QUERY_3@@@*)))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;2&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(cn=*@@@QUERY_1@@@* *@@@QUERY_2@@@* *@@@QUERY_3@@@*)(cn=*@@@QUERY_2@@@* *@@@QUERY_1@@@* *@@@QUERY_3@@@*))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;3&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(|(&amp;amp;p(givenName=@@@INITIAL_1@@@*)(middlename=@@@INITIAL_2@@@*)(sn=@@@QUERY_3@@@))(&amp;amp;(givenName=@@@INITIAL_2@@@*)(middleName=@@@INITIAL_1@@@*)(sn=@@@QUERY_3@@@)))&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
        &lt;entry&gt;<br>
          &lt;key&gt;&lt;value&gt;4&lt;/value&gt;&lt;/key&gt;<br>
          &lt;value&gt;(sn=@@@QUERY_3@@@)&lt;/value&gt;<br>
        &lt;/entry&gt;<br>
      &lt;/map&gt;<br>
    &lt;/property&gt;<br>
  &lt;/bean&gt;<br>
</code></pre>
Additional search term resolution can continue as needed.<br />

<h4>Additive Searches</h4>
Each search bean can be configured to be additive by adding the following property:<br>
<pre><code>    &lt;property name="additive"&gt;<br>
      &lt;value&gt;true&lt;/value&gt;<br>
    &lt;/property&gt;<br>
</code></pre>
This means that <b>all</b> searches configured will be executed, rather than stopping when the first search produces a result.<br>
<br>
<h4>Search Restrictions</h4>
An overriding search restriction can be provided for each search bean by providing the following property:<br>
<pre><code>    &lt;property name="searchRestrictions"&gt;<br>
      &lt;value&gt;(objectClass=inetOrgPerson)&lt;/value&gt;<br>
    &lt;/property&gt;<br>
</code></pre>
This filter will be <b>anded</b> to each search in order to restrict the results.<br>
<br>
<h4>Post Processers</h4>
Each search bean can be configured with a post processer.<br />
This library comes with the QueryPostDataProcesser which adds the following attributes to all search results:<br>
<ul><li>ldapQuery - the LDAP filter which produced the result<br>
</li><li>termCount - the term count which produced the result<br>
</li><li>searchIteration - the iteration count which produced the result<br>
</li><li>searchTime - the time in milliseconds it took for the LDAP filter to execute</li></ul>

To leverage this processer, add the following property to all search bean.<br>
<pre><code>    &lt;property name="postProcessers"&gt;<br>
      &lt;list&gt;<br>
        &lt;ref bean="queryDataPostProcesser"/&gt;<br>
      &lt;/list&gt;<br>
    &lt;/property&gt;<br>
</code></pre>

<h2>LDAP</h2>
The LdapPoolManager is configured with the classpath location of the ldap and pool properties files and the type of pool.<br />
For detailed descriptions of the <a href='vtldap#Configuration_Properties.md'>ldap</a> and <a href='vtldapPooling.md'>pool</a> configurations see the <a href='vtldap.md'>VT Ldap library</a>.<br />
If no ldapPoolProperties property is supplied than the default pooling configuration is used.<br>
<pre><code>  &lt;bean id="ldapPoolManager" class="edu.vt.middleware.ldap.search.LdapPoolManager"&gt;<br>
<br>
    &lt;!-- Name of file located in your classpath which contains the ldap configuration --&gt;<br>
    &lt;property name="ldapProperties"&gt;<br>
      &lt;value&gt;/peoplesearch.ldap.properties&lt;/value&gt;<br>
    &lt;/property&gt;<br>
<br>
    &lt;!-- Name of file located in your classpath which contains the ldap pool configuration --&gt;<br>
    &lt;property name="ldapPoolProperties"&gt;<br>
      &lt;value&gt;/peoplesearch.ldap.pool.properties&lt;/value&gt;<br>
    &lt;/property&gt;<br>
<br>
    &lt;!-- Type of ldap pool to use.  Acceptable values are:<br>
         SOFT_LIMIT, BLOCKING, SHARED --&gt;<br>
    &lt;property name="poolType"&gt;<br>
      &lt;value&gt;SOFT_LIMIT&lt;/value&gt;<br>
    &lt;/property&gt;<br>
  &lt;/bean&gt;<br>
</code></pre>

<hr />
<h1>Usage</h1>
The Search servlet accepts the following parameters:<br>
<table><thead><th> <b>Parameter</b> </th><th> <b>Value</b> </th></thead><tbody>
<tr><td> dsml-version     </td><td> 1 or 2, if the servlet is configured to return DSML </td></tr>
<tr><td> content-type     </td><td> text or xml, if the servlet is configured to return DSML </td></tr>
<tr><td> query            </td><td> search terms to process </td></tr>
<tr><td> attrs            </td><td> LDAP attributes to return </td></tr>
<tr><td> search-restrictions </td><td> LDAP filter to <i>AND</i> with all LDAP searches </td></tr>
<tr><td> from-result      </td><td> search index to begin pagination from </td></tr>
<tr><td> to-result        </td><td> search index to end pagination at </td></tr></tbody></table>

<h2>Sample</h2>
Request:<br>
<pre><code>http://your.host.com/PeopleSearch?query=dfisher&amp;attrs=givenName&amp;attrs=sn<br>
</code></pre>
Response:<br>
<pre><code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
<br>
&lt;dsml:dsml xmlns:dsml="http://www.dsml.org/DSML"&gt;<br>
  &lt;dsml:directory-entries&gt;<br>
    &lt;dsml:entry dn="uid=818037,ou=People,dc=vt,dc=edu"&gt;<br>
      &lt;dsml:attr name="sn"&gt;<br>
        &lt;dsml:value&gt;Fisher&lt;/dsml:value&gt;<br>
      &lt;/dsml:attr&gt;<br>
      &lt;dsml:attr name="givenName"&gt;<br>
        &lt;dsml:value&gt;Daniel&lt;/dsml:value&gt;<br>
      &lt;/dsml:attr&gt;<br>
    &lt;/dsml:entry&gt;<br>
  &lt;/dsml:directory-entries&gt;<br>
&lt;/dsml:dsml&gt;<br>
</code></pre>

Queries that use reserved characters should be submitted via POST so that they are properly encoded.<br>
<br>
<hr />
<h1>Scripts</h1>
Script execution requirements vary by platform.  For the following platform-specific instructions, let VTPEOPLESEARCH_HOME be the location where the VT People Search distribution was unpacked.<br>
<br>
<b>Unix</b>
<ol><li>Ensure the java executable is on your path.<br>
</li><li>Ensure $VTPEOPLESEARCH_HOME/bin is on your path.<br>
</li><li>If you encounter classpath problems executing the scripts, export VTPEOPLESEARCH_HOME as a separate shell variable.  This is not necessary in most cases (e.g. Linux, OSX, FreeBSD).</li></ol>

<h2>peoplesearch</h2>
Perform a fuzzy logic search for the terms 'daniel fisher' and return the mail attribute.<br>
<pre><code>peoplesearch -query 'daniel fisher' mail<br>
</code></pre>