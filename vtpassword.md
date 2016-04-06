

<br><br><br>
<hr />
<h1>This project has finished incubation and has moved to <a href='http://www.passay.org'>Passay</a></h1>
<hr />
<br><br><br>

<h1>Introduction</h1>
VT Password is a Java library for verifying that a password meets a define ruleset.<br />
This library includes the following rule implementations:<br>
<ul><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/AlphabeticalSequenceRule.html'>AllowedCharacterRule</a> - Does a password contain only a specific list of characters<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/AlphabeticalSequenceRule.html'>AlphabeticalSequenceRule</a> - Does a password contain an alphabetical sequence<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/CharacterCharacteristicRule.html'>CharacterCharacteristicRule</a> - Does a password contain the desired mix of character types<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/DictionaryRule.html'>DictionaryRule</a> - Does a password match a word in a dictionary<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/DictionarySubstringRule.html'>DictionarySubstringRule</a> - Does a password contain a word in a dictionary<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/DigitCharacterRule.html'>DigitCharacterRule</a> - Does a password contain a digit<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/HistoryRule.html'>HistoryRule</a> - Does a password match a previous password, supports hashes<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/IllegalCharacterRule.html'>IllegalCharacterRule</a> - Does a password contain an illegal character<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/LengthRule.html'>LengthRule</a> - Is a password of a certain length<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/LowercaseCharacterRule.html'>LowercaseCharacterRule</a> - Does a password contain a lowercase character<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/NonAlphanumericCharacterRule.html'>NonAlphanumericCharacterRule</a> - Does a password contain a non-alphanumeric character<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/NumericalSequenceRule.html'>NumericalSequenceRule</a> - Does a password contain a numerical sequence<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/RegexRule.html'>RegexRule</a> - Does a password match a regular expression<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/RepeatCharacterRegexRule.html'>RepeatCharacterRegexRule</a> - Does a password contain a repeated character<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/SequenceRule.html'>SequenceRule</a> - Does a password contain a keyboard sequence<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/SourceRule.html'>SourceRule</a> - Does a password match the password from another system or source<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/QwertySequenceRule.html'>QwertySequenceRule</a> - Does a password contain a QWERTY keyboard sequence<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/UppercaseCharacterRule.html'>UppercaseCharacterRule</a> - Does a password contain an uppercase character<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/UsernameRule.html'>UsernameRule</a> - Does a password contain a username<br>
</li><li><a href='http://vt-middleware.googlecode.com/svn/vt-password/javadoc/vt-password-3.1.2/edu/vt/middleware/password/WhitespaceRule.html'>WhitespaceRule</a> - Does a password contain whitespace</li></ul>

<hr />
<h1>Installation</h1>
The latest version can be downloaded from the <a href='http://code.google.com/p/vt-middleware/downloads/list'>downloads</a> page.<br>
<br>
This project is available from Maven Central. If you would like to use this project in your maven build, include the following in your pom.xml:<br>
<pre><code>&lt;dependencies&gt;<br>
  &lt;dependency&gt;<br>
      &lt;groupId&gt;edu.vt.middleware&lt;/groupId&gt;<br>
      &lt;artifactId&gt;vt-password&lt;/artifactId&gt;<br>
      &lt;version&gt;3.1.2&lt;/version&gt;<br>
  &lt;/dependency&gt;<br>
&lt;dependencies&gt;<br>
</code></pre>

<hr />
<h1>Code Samples</h1>
Unless otherwise noted, classes appearing in the following samples are included in the JSE libraries or VT Password.<br>
<br>
<pre><code>// password must be between 8 and 16 chars long<br>
LengthRule lengthRule = new LengthRule(8, 16);<br>
<br>
// don't allow whitespace<br>
WhitespaceRule whitespaceRule = new WhitespaceRule();<br>
<br>
// control allowed characters<br>
CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();<br>
// require at least 1 digit in passwords<br>
charRule.getRules().add(new DigitCharacterRule(1));<br>
// require at least 1 non-alphanumeric char<br>
charRule.getRules().add(new NonAlphanumericCharacterRule(1));<br>
// require at least 1 upper case char<br>
charRule.getRules().add(new UppercaseCharacterRule(1));<br>
// require at least 1 lower case char<br>
charRule.getRules().add(new LowercaseCharacterRule(1));<br>
// require at least 3 of the previous rules be met<br>
charRule.setNumberOfCharacteristics(3);<br>
<br>
// don't allow alphabetical sequences<br>
AlphabeticalSequenceRule alphaSeqRule = new AlphabeticalSequenceRule();<br>
<br>
// don't allow numerical sequences of length 3<br>
NumericalSequenceRule numSeqRule = new NumericalSequenceRule(3);<br>
<br>
// don't allow qwerty sequences<br>
QwertySequenceRule qwertySeqRule = new QwertySequenceRule();<br>
<br>
// don't allow 4 repeat characters<br>
RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(4);<br>
<br>
// group all rules together in a List<br>
List&lt;Rule&gt; ruleList = new ArrayList&lt;Rule&gt;();<br>
ruleList.add(lengthRule);<br>
ruleList.add(whitespaceRule);<br>
ruleList.add(charRule);<br>
ruleList.add(alphaSeqRule);<br>
ruleList.add(numSeqRule);<br>
ruleList.add(qwertySeqRule);<br>
ruleList.add(repeatRule);<br>
<br>
PasswordValidator validator = new PasswordValidator(ruleList);<br>
PasswordData passwordData = new PasswordData(new Password("testpassword"));<br>
<br>
RuleResult result = validator.validate(passwordData);<br>
if (result.isValid()) {<br>
  System.out.println("Valid password");<br>
} else {<br>
  System.out.println("Invalid password:");<br>
  for (String msg : validator.getMessages(result)) {<br>
    System.out.println(msg);<br>
  }<br>
}<br>
</code></pre>

<h2>Using a dictionary</h2>
Using a dictionary requires using the <a href='vtdictionary.md'>vt-dictionary</a> library.<br>
<pre><code>// create a case sensitive word list and sort it<br>
ArrayWordList awl = WordLists.createFromReader(<br>
  new FileReader[] {new FileReader("/path/to/dictionary")},<br>
  true,<br>
  new ArraysSort());<br>
<br>
// create a dictionary for searching<br>
WordListDictionary dict = new WordListDictionary(awl);<br>
<br>
DictionarySubstringRule dictRule = new DictionarySubstringRule(dict);<br>
dictRule.setWordLength(4); // size of words to check in the password<br>
dictRule.setMatchBackwards(true); // match dictionary words backwards<br>
<br>
List&lt;Rule&gt; ruleList = new ArrayList&lt;Rule&gt;();<br>
ruleList.add(dictRule);<br>
<br>
PasswordValidator validator = new PasswordValidator(ruleList);<br>
PasswordData passwordData = new PasswordData(new Password("testpassword"));<br>
<br>
RuleResult result = validator.validate(passwordData);<br>
if (result.isValid()) {<br>
  System.out.println("Valid password");<br>
} else {<br>
  System.out.println("Invalid password:");<br>
  for (String msg : validator.getMessages(result)) {<br>
    System.out.println(msg);<br>
  }<br>
}<br>
</code></pre>

<h2>Using password history</h2>
Using password history requires using the <a href='vtcrypt.md'>vt-crypt</a> library if hashed passwords are used.<br />
Typically password history would be retrieved from a datasource.<br />
This example uses an array for simplicity.<br>
<pre><code>String passwd = "testpassword";<br>
<br>
// base64 encoded, SHA-1 passwords<br>
String[] history = new String[] {<br>
  "MwRLPWHiwj49VmNSmTsSBeFECqk=",<br>
  "EqCiqolu0z8+T+5COOSO/+XfTCA=",<br>
  "V0DGHz3umagyKKAbbFEbpfByzsQ=",<br>
};<br>
<br>
HistoryRule historyRule = new HistoryRule();<br>
historyRule.setDigest("SHA-1", new Base64Converter());<br>
<br>
List&lt;Rule&gt; ruleList = new ArrayList&lt;Rule&gt;();<br>
ruleList.add(historyRule);<br>
<br>
PasswordValidator validator = new PasswordValidator(ruleList);<br>
PasswordData passwordData = new PasswordData(new Password("testpassword"));<br>
passwordData.setPasswordHistory(Arrays.asList(history));<br>
<br>
RuleResult result = validator.validate(passwordData);<br>
if (result.isValid()) {<br>
  System.out.println("Valid password");<br>
} else {<br>
  System.out.println("Invalid password:");<br>
  for (String msg : validator.getMessages(result)) {<br>
    System.out.println(msg);<br>
  }<br>
}<br>
</code></pre>

<h2>Customizing error messages</h2>
Custom error messages require using the MessageResolver class and storing your messages in Properties. Each message is processed with <code>String.format(String, Object[])</code>.<br />

<i>default error messages</i>
<pre><code>HISTORY_VIOLATION=Password matches one of %1$s previous passwords.<br>
ILLEGAL_WORD=Password contains the dictionary word '%1$s'.<br>
ILLEGAL_WORD_REVERSED=Password contains the reversed dictionary word '%1$s'.<br>
ILLEGAL_MATCH=Password matches the illegal sequence '%1$s'.<br>
ILLEGAL_CHAR=Password contains the illegal character '%1$s'.<br>
ILLEGAL_SEQUENCE=Password contains the illegal sequence '%1$s'.<br>
ILLEGAL_USERNAME=Password contains the user id '%1$s'.<br>
ILLEGAL_USERNAME_REVERSED=Password contains the user id '%1$s' in reverse.<br>
ILLEGAL_WHITESPACE=Password cannot contain whitespace characters.<br>
INSUFFICIENT_CHARACTERS=Password must contain at least %1$s %2$s characters.<br>
INSUFFICIENT_CHARACTERISTICS=Password matches %1$s of %2$s character rules, but %3$s are required.<br>
SOURCE_VIOLATION=Password cannot be the same as your %1$s password.<br>
TOO_LONG=Password must be no more than %2$s characters in length.<br>
TOO_SHORT=Password must be at least %1$s characters in length.<br>
</code></pre>

Once you have created a custom properties file, load it into a Properties object and pass it to a PasswordValidator.<br>
<br>
<pre><code>Properties props = new Properties();<br>
props.load(new FileInputStream("/path_to_my/messages.properties"));<br>
MessageResolver resolver = new MessageResolver(props);<br>
<br>
PasswordValidator validator = new PasswordValidator(resolver, ruleList);<br>
</code></pre>

<h2>Generating passwords</h2>
Password suggestions can be generated to meet a list of CharacterRule.<br>
<pre><code>// create a password generator<br>
PasswordGenerator generator = new PasswordGenerator();<br>
<br>
// create character rules to generate passwords with<br>
List&lt;CharacterRule&gt; rules = new ArrayList&lt;CharacterRule&gt;();<br>
rules.add(new DigitCharacterRule(1));<br>
rules.add(new NonAlphanumericCharacterRule(1));<br>
rules.add(new UppercaseCharacterRule(1));<br>
rules.add(new LowercaseCharacterRule(1));<br>
<br>
// generate 5 passwords, each 8 characters long<br>
List&lt;String&gt; passwords = new ArrayList&lt;String&gt;();<br>
for (int i = 0; i &lt; 5; i++) {<br>
  passwords.add(generator.generatePassword(8, rules));<br>
}<br>
</code></pre>

<i>Thanks to Sean Sullivan for this implementation</i>

<hr />
<h1>Scripts</h1>
Script execution requirements vary by platform.  For the following platform-specific instructions, let VTPASS_HOME be the location where the VT Password distribution was unpacked.<br>
<br>
<b>Unix</b>
<ol><li>Ensure the java executable is on your path.<br>
</li><li>Ensure $VTPASS_HOME/bin is on your path.<br>
</li><li>If you encounter classpath problems executing the scripts, export VTPASS_HOME as a separate shell variable.  This is not necessary in most cases (e.g. Linux, OSX, FreeBSD).</li></ol>

<h2>password - Checker Operations</h2>
Check a password has the following qualities:<br>
<ul><li>length >= 8<br>
</li><li>length <= 16<br>
</li><li>contains at least 1 digit<br>
</li><li>contains at least 1 non-alphanumeric<br>
</li><li>contains at least 1 uppercase</li></ul>

<pre><code>bin/password -l 8 16 -c 1 0 1 1 0 3 'T#sting01'<br>
</code></pre>

Print command-line usage statement<br>
<pre><code>bin/password -h<br>
</code></pre>