

# Version 2.2 #
  * Library now requires Java 1.6
  * Implement clone for algorithms. ([Issue 152](http://code.google.com/p/vt-middleware/issues/detail?id=152))
  * Convert logging from commons-logging to slf4j. ([Issue 128](http://code.google.com/p/vt-middleware/issues/detail?id=128))
  * Add additional X.509 helper methods. ([Issue 126](http://code.google.com/p/vt-middleware/issues/detail?id=126))
  * Support signing and verify signed MACs. ([Issue 125](http://code.google.com/p/vt-middleware/issues/detail?id=125))
  * Add some additional functionality to CryptReader. ([Issue 124](http://code.google.com/p/vt-middleware/issues/detail?id=124))
  * Add some additional support for keys. ([Issue 123](http://code.google.com/p/vt-middleware/issues/detail?id=123))
  * Mark commons-cli as an optional dependency. ([Issue 122](http://code.google.com/p/vt-middleware/issues/detail?id=122))

# Version 2.1.4 #
  * Fix typo that affected X.509 certificate DN parsing capabilities. ([Issue 119](http://code.google.com/p/vt-middleware/issues/detail?id=119))
# Version 2.1.3 #
  * Improve capability for reading encrypted private keys. ([Issue 91](http://code.google.com/p/vt-middleware/issues/detail?id=91&can=1&q=vt-crypt))
  * Fix for parsing DER encoding of CRLDistributionPoints. ([Issue 100](http://code.google.com/p/vt-middleware/issues/detail?id=100&can=1&q=vt-crypt))
# Version 2.1.2 #
  * Add EmailAddress to [AttributeType](http://vt-middleware.googlecode.com/svn/vt-crypt/javadoc/vt-crypt-2.1.2/edu/vt/middleware/crypt/x509/types/AttributeType.html) enumeration since this attribute commonly appears in X.509 certificate DNs
  * Other misc improvements to support X.509 DN parsing

# Version 2.1.1 #
  * First version available in [Maven Central](http://repo1.maven.org/maven2/edu/vt/middleware/vt-crypt/)
  * Moved maven plugin into separate project
# Version 2.1 #
  * [vt-crypt-1](http://code.google.com/p/vt-middleware/issues/detail?id=1) - Create API for Reading X.509v3 Certificate Extended Properties.
  * [vt-crypt-4](http://code.google.com/p/vt-middleware/issues/detail?id=4) - Keystore Incompatibility with keytool.
  * [vt-crypt-18](http://code.google.com/p/vt-middleware/issues/detail?id=18) - X.509 Certificate DN Formatter.
  * [vt-crypt-33](http://code.google.com/p/vt-middleware/issues/detail?id=33) - Add Support for Property Encryption/Decryption Under Maven

# Version 2.0.1 #
  * Added APL version 2, project is now dual licensed
  * Updated LGPL from version 2.1 to 3.0

# Version 2.0 #
Initial google code release.

## Known Issues ##
  * README.txt was not updated and includes documentation from the 1.5 release.
  * pom.xml incorrectly includes the jdk14 version of the Bouncy Castle provider.