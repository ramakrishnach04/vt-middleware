

# Introduction #
This library supports the GSSAPI authentication mechanism for use with LDAPs that provide support for Kerberos.


---

# Instructions #

  1. Obtain a keytab
    * can be obtained from your kerberos administrator
    * can also be created by the user
```
        ktutil addent -password -p USER@YOUR.DOMAIN -k 2 -e des3-hmac-sha1 wkt ./krb5.keytab
        kinit -k -t ./krb5.keytab
```
  1. Create a JAAS configuration for Kerberos
```
      com.sun.security.jgss.initiate {
        com.sun.security.auth.module.Krb5LoginModule required
          doNotPrompt="true"
          principal="principal_in_your_keytab"
          useKeyTab="true"
          keyTab="PATH_TO_YOUR/krb5.keytab";
      };
```
  1. Start Java with the following switches
```
      -Djava.security.auth.login.config=PATH_TO_YOUR/krb5_jaas.config
      -Djavax.security.auth.useSubjectCredsOnly=false
      -Djava.security.krb5.realm=YOUR.REALM
      -Djava.security.krb5.kdc=your.kerberos.hostname"
```

## Using a ticket cache ##
  1. Create a krb5.conf file at: $JAVA\_HOME/jre/lib/security/krb5.conf
```
      [libdefaults]
              default_realm = YOUR.REALM

      [realms]
              YOUR_REALM = { 
                      kdc = your.kerberos.hostname
              }
```
  1. Run kinit to create your ticket cache (make sure you are using the kinit provided with your jvm)
```
      kinit PRINCIPAL@YOUR.REALM -k -t PATH_TO_YOUR/krb5.keytab -c PATH_TO_YOUR/krb5cc
```
  1. Update your JAAS config with the ticket cache options
```
      com.sun.security.jgss.initiate {
        com.sun.security.auth.module.Krb5LoginModule required
          doNotPrompt="true"
          principal="principal_in_your_keytab"
          renewTGT="true"
          useTicketCache="true"
          ticketCache="PATH_TO_YOUR/krb5cc"
          useKeyTab="true"
          keyTab="PATH_TO_YOUR/krb5.keytab";
      };
```