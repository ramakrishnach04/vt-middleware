#labels java,crypto,bouncycastle
#sidebar vtcryptSidebar



<br><br><br>
<hr />
<h1>This project has finished incubation and has moved to <a href='http://www.cryptacular.org'>Cryptacular</a></h1>
<hr />
<br><br><br>
<h1>Introduction</h1>
VT Crypt is a high level, general purpose Java cryptograhic library that meets the following design goals:<br>
<ol><li>Flexible JCE provider.  Prefers the <a href='http://www.bouncycastle.org/java.html'>Bouncy Castle Java Provider</a>, but can fall back to other providers defined in the environment for algorithms not implemented by BC.<br>
</li><li>Ease of use for common cryptographic operations.  A one liner highlights this well; the following prints the MD5 hash of a password as a string of HEX characters:  <code>System.out.println(new MD5().digest(passBytes, new HexConverter()));</code>
</li><li>Convenient and performant handling of cryptographic operations on large data streams.<br>
</li><li>Support for base-64 and hexadecimal encoding of ciphertext input/output.<br>
</li><li>Support for I/O operations on cryptographic primitives including generating and writing symmetric encryption keys, public/private key pairs, and X.509 certificates.  Both PEM and DER encoding is handled conveniently.<br>
</li><li>Command line interface for each class of cryptographic operation (digest, symmetric encryption, public-key encryption, message signing, etc).  A command line interface for keystore operations is also included, which is notable as it supports features above and beyond the the Java <a href='http://java.sun.com/j2se/1.5.0/docs/tooldocs/solaris/keytool.html'>keytool</a> utility.</li></ol>

It is important to note that no cryptographic algorithms are implemented; <a href='http://www.bouncycastle.org/java.html'>Bouncy Castle</a> provides all cryptographic algorithms where required.<br>
<br>
<h1>Installation</h1>
The latest version can be downloaded from the <a href='http://code.google.com/p/vt-middleware/downloads/list'>downloads</a> page.<br>
<br>
This project is available from Maven Central. If you would like to use this project in your maven build, include the following in your pom.xml:<br>
<pre><code>&lt;dependencies&gt;<br>
  &lt;dependency&gt;<br>
      &lt;groupId&gt;edu.vt.middleware&lt;/groupId&gt;<br>
      &lt;artifactId&gt;vt-crypt&lt;/artifactId&gt;<br>
      &lt;version&gt;2.2&lt;/version&gt;<br>
  &lt;/dependency&gt;<br>
&lt;dependencies&gt;<br>
</code></pre>

<h1>Code Samples</h1>
Unless otherwise noted, classes appearing in the following samples are included in the JSE libraries or VT Crypt.<br>
<br>
<h2>Message Digests/Hashing</h2>
Generate the MD5 hash of a password.  The result is a string of hexadecimal characters as is the common use case for password verification.<br>
<pre><code>String hash = new MD5().digest(Convert.toBytes(passChars), new HexConverter());<br>
</code></pre>

Compute the SHA-1 fingerprint of a file, which produces a string of colon-delimited hex bytes, 1A:2B:3C... as is common with fingerprint utilities.  Note the <code>true</code> argument to the HexConverter constructor that causes colon-delimited bytes to be produced in the hexadecimal output string.<br>
<pre><code>String fingerprint = null;<br>
InputStream in = new BufferedInputStream(<br>
  new FileInputStream(new File(big-file.txt)));<br>
try {<br>
  fingerprint = new SHA1().digest(in, new HexConverter(true));<br>
} finally {<br>
  in.close();<br>
}<br>
</code></pre>

<h2>Cryptographic Signatures</h2>
Compute a base 64-encoded DSA signature on a file.<br>
<pre><code>final InputStream in = new BufferedInputStream(<br>
  new FileInputStream(new File("path/to/file.txt")));<br>
final SignatureAlgorithm signature = new DSASignature();<br>
signature.setSignKey(dsaPrivateKey);<br>
signature.initSign();<br>
final String b64Sig = signature.sign(in, new Base64Converter());<br>
</code></pre>

Verify an RSA signature on a file.  Use a custom SHA-512 digest instead of the default SHA-1.<br>
<pre><code>final InputStream in = new BufferedInputStream(<br>
  new FileInputStream(new File("path/to/file.txt")));<br>
final SignatureAlgorithm signature = new RSASignature(new SHA512());<br>
signature.setVerifyKey(rsaPublicKey);<br>
signature.initVerify();<br>
if (signature.verify(in, sigBytes)) {<br>
  System.out.println("Signature is valid.");<br>
} else {<br>
  System.out.println("Invalid signature.");<br>
}<br>
</code></pre>

<h2>Symmetric Encryption</h2>
Generate a 256-bit symmetric encryption key suitable for use with the Blowfish cipher and write it out to a file.<br>
<pre><code>CryptWriter.writeEncodedKey(<br>
  Blowfish.generateKey(256),<br>
  new File("path/to/blowfish.key"));<br>
</code></pre>

Encrypt a file with the Blowfish cipher in OFB mode producing hex-encoded ciphertext output.<br>
<pre><code>final SymmetricAlgorithm alg = new Blowfish(<br>
  "OFB",<br>
  SymmetricAlgorithm.DEFAULT_PADDING);<br>
alg.setIV(iv);<br>
alg.setKey(key);<br>
alg.initEncrypt();<br>
final InputStream in = new BufferedInputStream(<br>
  new FileInputStream(new File("path/to/plain.txt")));<br>
final OutputStream out = new HexFilterOutputStream(<br>
  new BufferedOutputStream(<br>
    new FileOutputStream(new File("path/to/cipher.out"))));<br>
try {<br>
  alg.encrypt(in, out);<br>
} finally {<br>
  in.close();<br>
  out.close();<br>
}<br>
</code></pre>

Decrypt a base 64-encoded ciphertext file with the triple DES cipher in default CBC mode.<br>
<pre><code>final SymmetricAlgorithm alg = new DESede();<br>
alg.setIV(iv);<br>
alg.setKey(key);<br>
alg.initDecrypt();<br>
final InputStream in = new Base64FilterInputStream(<br>
  new BufferedInputStream(<br>
    new FileInputStream(new File("path/to/cipher.out")));<br>
final OutputStream out = new BufferedOutputStream(<br>
  new FileOutputStream(new File("path/to/plain.txt"))));<br>
try {<br>
  alg.decrypt(in, out);<br>
} finally {<br>
  in.close();<br>
  out.close();<br>
}<br>
</code></pre>

Encrypt a file using PKCS#12 password-based encryption with AES cipher.<br>
<pre><code>final KeyWithIV keyWithIV = PbeKeyGenerator.generatePkcs12(<br>
  passChars,<br>
  keyBitLength,<br>
  new SHA512(),<br>
  salt);<br>
final SymmetricAlgorithm alg = new AES();<br>
alg.setIV(keyWithIV.getIV());<br>
alg.setKey(keyWithIV.getKey());<br>
final InputStream in = new BufferedInputStream(<br>
  new FileInputStream(new File("path/to/plain.txt")));<br>
final OutputStream out = new BufferedOutputStream(<br>
  new FileOutputStream(new File("path/to/cipher.out"))));<br>
try {<br>
  alg.encrypt(in, out);<br>
} finally {<br>
  in.close();<br>
  out.close();<br>
}<br>
</code></pre>

<h2>Public Key (Asymmetric) Cryptography</h2>
Generate a new 2048-bit RSA key pair and save both keys as PEM-encoded files.  Password protect the private key.<br>
<pre><code>final KeyPair keyPair = RSA.generateKeys(2048);<br>
CryptWriter.writePemKey(<br>
  keyPair.getPrivate(),<br>
  passChars,<br>
  new SecureRandom(),<br>
  new File("path/to/rsa-priv-key.pem"));<br>
CryptWriter.writePemKey(<br>
  keyPair.getPublic(),<br>
  new File("path/to/rsa-pub-key.pem"));<br>
</code></pre>

Encrypt a string with the public key and produce base 64-encoded ciphertext.  Note that for the RSA cipher, the size of data to be encrypted must be less than or equal to the size of the encryption key.<br>
<pre><code>final AsymmetricAlgorithm alg = new RSA();<br>
alg.setKey(publicKey);<br>
alg.initEncrypt();<br>
final String b64CipherText = alg.encrypt(<br>
  plainText.getBytes(),<br>
  new Base64Converter());<br>
</code></pre>

<h1>Scripts</h1>
Script execution requirements vary by platform.  For the following platform-specific instructions, let VTCRYPT_HOME be the location where the VT Crypt distribution was unpacked.<br>
<br>
<b>Unix</b>
<ol><li>Ensure the java executable is on your path.<br>
</li><li>Ensure $VTCRYPT_HOME/bin is on your path.<br>
</li><li>If you encounter classpath problems executing the scripts, export VTCRYPT_HOME as a separate shell variable.  This is not necessary in most cases (e.g. Linux, OSX, FreeBSD).</li></ol>

<b>Windows</b>
<ol><li>Set the JAVA_HOME environment variable to a JDK or JRE installation path.<br>
</li><li>Set the VTCRYPT_HOME environment variable.</li></ol>

<img src='http://vt-middleware.googlecode.com/svn/wiki/images/windows-env-var.png' />

<h2>keystore - Keystore Operations</h2>
Print contents of a JKS keystore<br>
<pre><code>keystore -list -keystore path/to/keystore.jks -storepass changeit<br>
</code></pre>

Import a trusted PEM-encoded X.509 certificate into a BKS keystore<br>
<pre><code>keystore -import -keystore path/to/keystore.bks -storetype bks \<br>
         -storepass changeit -alias mycert -cert path/to/cert.pem<br>
</code></pre>

Import a key pair consisting of a DER-encoded PKCS#8 RSA key and a PEM-encoded X.509 certificate into a JKS keystore<br>
<pre><code>keystore -import -keystore path/to/keystore.jks -storepass changeit \<br>
         -alias mykeypair -cert path/to/cert.pem -key path/to/rsa-p8-key.der<br>
</code></pre>

Export a trusted certificate to a PEM-encoded file (PEM-encoded key is determined by .pem file extension)<br>
<pre><code>keystore -export -keystore path/to/keystore.jks -storepass changeit \<br>
         -alias mycert -cert path/to/exported/cert.pem<br>
</code></pre>

Export key and certificate of a keypair entry to DER-encoded files<br>
<pre><code>keystore -export -keystore path/to/keystore.jks -storepass changeit \<br>
         -alias mykeypair -cert path/to/exported/cert.der \<br>
         -key path/to/exported/key.der<br>
</code></pre>

<h2>digest - Message Digest Operations</h2>
Print the MD5 hash of a word or phrase as a hex string<br>
<pre><code>echo -n secret | digest -alg md5 -encoding hex<br>
</code></pre>

Print the SHA-1 fingerprint of a file<br>
<pre><code>digest -alg sha1 -in path/to/file.txt -encoding hex<br>
</code></pre>

<h2>pkc - Public Key (Asymmetric) Cryptography Operations</h2>
Generate a 2048-bit RSA public/private key pair as DER-encoded files<br>
<pre><code>pkc -cipher RSA -genkeys 2048 -out rsa-pub-key.der -privkey rsa-priv-key.der<br>
</code></pre>

Encrypt a file to base-64-encoded ciphertext<br>
<pre><code>pkc -cipher RSA -encrypt path/to/rsa-pub-key.der -encoding base64 \<br>
    -in path/to/plain.txt -out path/to/cipher.txt<br>
</code></pre>

Decrypt a base-64-encoded ciphertext file with a PEM-encoded private key (PEM-encoded key is determined by .pem file extension)<br>
<pre><code>pkc -cipher RSA -decrypt path/to/rsa-priv-key.pem -encoding base64 \<br>
    -in path/to/cipher.txt -out path/to/plain.txt<br>
</code></pre>

<h2>enc - Symmetric Encryption Operations</h2>
Generate a new 256-bit AES key<br>
<pre><code>enc -genkey -cipher AES -keysize 256 -out aes.key<br>
</code></pre>

Encrypt a file with AES cipher in default CBC mode<br>
<pre><code>enc -encrypt -cipher AES -key path/to/aes.key \<br>
    -iv 3858f62230ac3c915f300c664312c63f \<br>
    -in path/to/plain.txt -out path/to/cipher.out<br>
</code></pre>

Decrypt a file with AES cipher in default CBC mode<br>
<pre><code>enc -decrypt -cipher AES -key path/to/aes.key \<br>
    -iv 3858f62230ac3c915f300c664312c63f \<br>
    -in path/to/cipher.out -out path/to/plain.txt<br>
</code></pre>

Encrypt a file with AES cipher in OFB mode producing base-64-encoded ciphertext<br>
<pre><code>enc -encrypt -cipher AES -key path/to/aes.key -mode OFB -encoding base64 \<br>
    -iv 3858f62230ac3c915f300c664312c63f \<br>
    -in path/to/plain.txt -out path/to/cipher.out<br>
</code></pre>

Decrypt a base-64-encoded ciphertext file using AES in OFB mode<br>
<pre><code>enc -decrypt -cipher AES -key path/to/aes.key -mode OFB -encoding base64 \<br>
    -iv 3858f62230ac3c915f300c664312c63f \<br>
    -in path/to/cipher.out -out path/to/plain.txt<br>
</code></pre>

Encrypt a file using PKCS#5s2 password-based encryption with AES cipher<br>
<pre><code>enc -encrypt -cipher AES -pbe Seekr1t -pbemode pkcs5s2 \<br>
    -salt A1B2C3D4E5F6 -keysize 256 \<br>
    -in path/to/plain.txt -out path/to/cipher.out<br>
</code></pre>

Decrypt a file that was originally encrypted with PKCS#5s2 password-based encryption using AES cipher<br>
<pre><code>enc -decrypt -cipher AES -pbe Seekr1t -pbemode pkcs5s2 \<br>
    -salt A1B2C3D4E5F6 -keysize 256 \<br>
    -in path/to/cipher.out -out path/to/plain.txt<br>
</code></pre>

<h2>sign - Cryptographic Signature Operations</h2>
Create a base-64-encoded DSA signature of a file<br>
<pre><code>sign -sign -alg DSA -key path/to/dsa-priv-key.der -encoding base64 \<br>
     -in path/to/file.txt<br>
</code></pre>

Create a hex-encoded RSA signature of a file using an MD5 digest<br>
<pre><code>sign -sign -alg RSA -key path/to/dsa-priv-key.der -digest MD5 \<br>
     -encoding base64 -in path/to/file.txt<br>
</code></pre>

Verify a hex-encoded RSA signature using a PEM-encoded RSA public key (PEM-encoded key is determined by .pem file extension)<br>
<pre><code>sign -verify path/to/sig.hex -alg RSA -key path/to/rsa-pub-key.pem \<br>
     -encoding hex -in path/to/file.txt<br>
</code></pre>


<h1>Maven Integration</h1>
<b>New in version 2.1</b>

The new maven-crypt-plugin module supports property decryption using symmetric key cryptography.  The primary use case of this feature is to support the use of ciphertext credentials in POM properties that are decrypted prior to artifact generation.  The following example demonstrates the prototypical use case:<br>
<br>
<pre><code>&lt;project&gt;<br>
  &lt;properties&gt;<br>
    &lt;password&gt;fFsW5Z0WmPmqJO8ot21Nmg==&lt;/password&gt;<br>
  &lt;/properties&gt;<br>
  &lt;!-- Other POM elements here as usual --&gt;<br>
  &lt;build&gt;<br>
    &lt;plugins&gt;<br>
      &lt;plugin&gt;<br>
        &lt;groupId&gt;edu.vt.middleware.maven.plugins&lt;/groupId&gt;<br>
        &lt;artifactId&gt;maven-crypt-plugin&lt;/artifactId&gt;<br>
        &lt;version&gt;2.1.1&lt;/version&gt;<br>
        &lt;configuration&gt;<br>
          &lt;keyFile&gt;path/to/decryption/key.file&lt;/keyFile&gt;<br>
          &lt;algorithm&gt;AES&lt;/algorithm&gt;<br>
          &lt;mode&gt;CBC&lt;/mode&gt;<br>
          &lt;padding&gt;PKCS5Padding&lt;/padding&gt;<br>
          &lt;iv&gt;63366164343061393663356661353062&lt;/iv&gt;<br>
          &lt;properties&gt;<br>
            &lt;property&gt;password&lt;/property&gt;<br>
          &lt;/properties&gt;<br>
        &lt;/configuration&gt;<br>
        &lt;executions&gt;<br>
          &lt;execution&gt;<br>
            &lt;id&gt;decrypt-property&lt;/id&gt;<br>
            &lt;phase&gt;generate-resources&lt;/phase&gt;<br>
            &lt;goals&gt;<br>
              &lt;goal&gt;decrypt&lt;/goal&gt;<br>
            &lt;/goals&gt;<br>
          &lt;/execution&gt;<br>
        &lt;/executions&gt;<br>
      &lt;/plugin&gt;<br>
    &lt;/plugins&gt;<br>
  &lt;/build&gt;<br>
&lt;/project&gt;<br>
<br>
</code></pre>

The <code>password</code> property contains base-64 encoded ciphertext generated by a command such as the following:<br>
<br>
<pre><code>echo -n 'seekr1t' | enc -cipher AES -encrypt \<br>
     -iv 63366164343061393663356661353062 \<br>
     -key path/to/decryption/key.file -encoding base64<br>
</code></pre>

The above POM configuration decrypts <code>password</code> to its plaintext value (<code>seekr1t</code> in the example), which may be subsequently used by the build system.  The generate-resources build phase is the most reasonable place for property decryption in most cases, but any build phase could be used.