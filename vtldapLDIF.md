

# Introduction #
This library provides provides LDIF and DSML implementations for exporting LDAP data. If sorting is needed each implementation accepts an LdapBeanFactory:
```
Ldif.setLdapBeanFactory(new SortedLdapBeanFactory());
```

# Samples #

## LDIF ##
```
Ldap ldap = new Ldap(
  new LdapConfig("ldap://directory.vt.edu/dc=vt,dc=edu"));
Iterator<SearchResult> i = ldap.search(new SearchFilter("sn=Fisher"), new String[]{"mail", "displayName"});
Ldif ldif = new Ldif();
StringWriter sw = new StringWriter();
ldif.outputLdif(i, sw);
String results = sw.toString();
```

```
dn: uid=818037,ou=People,dc=vt,dc=edu
mail: dfisher@vt.edu
displayName: Daniel W Fisher
```

## DSML ##

### Version 1 ###
```
Ldap ldap = new Ldap(
  new LdapConfig("ldap://directory.vt.edu/dc=vt,dc=edu"));
Iterator<SearchResult> i = ldap.search(new SearchFilter("sn=Fisher"), new String[]{"mail", "displayName"});
Dsmlv1 dsml = new Dsmlv1();
StringWriter sw = new StringWriter();
dsml.outputDsml(i, sw);
String results = sw.toString();
```

```
<?xml version="1.0" encoding="UTF-8"?>
<dsml:dsml xmlns:dsml="http://www.dsml.org/DSML">
  <dsml:directory-entries>
    <dsml:entry dn="uid=818037,ou=People,dc=vt,dc=edu">
      <dsml:attr name="mail">
        <dsml:value>dfisher@vt.edu</dsml:value>
      </dsml:attr>
      <dsml:attr name="displayName">
        <dsml:value>Daniel W Fisher</dsml:value>
      </dsml:attr>
    </dsml:entry>
  </dsml:directory-entries>
</dsml:dsml>
```

### Version 2 ###
```
Ldap ldap = new Ldap(
  new LdapConfig("ldap://directory.vt.edu/dc=vt,dc=edu"));
Iterator<SearchResult> i = ldap.search(new SearchFilter("sn=Fisher"), new String[]{"mail", "displayName"});
Dsmlv2 dsml = new Dsmlv2();
StringWriter sw = new StringWriter();
dsml.outputDsml(i, sw);
String results = sw.toString();
```

```
<?xml version="1.0" encoding="UTF-8"?>
<batchResponse xmlns="urn:oasis:names:tc:DSML:2:0:core">
  <searchResponse>
    <searchResultEntry dn="uid=818037,ou=People,dc=vt,dc=edu">
      <attr name="mail">
        <value>dfisher@vt.edu</value>
      </attr>
      <attr name="displayName">
        <value>Daniel W Fisher</value>
      </attr>
    </searchResultEntry>
    <searchResultDone>
      <resultCode code="0"/>
    </searchResultDone>
  </searchResponse>
</batchResponse>
```