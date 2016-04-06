# MySQL AES Decryption #
MySQL uses a form of passphrase-based encryption with AES for its [AES\_ENCRYPT(str,key\_str)](http://dev.mysql.com/doc/mysql/en/encryption-functions.html) function.  The following code sample demonstrates how to decrypt it with the symmetric encryption functions of vt-crypt.

```
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.crypto.spec.SecretKeySpec;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;
import edu.vt.middleware.crypt.util.Convert;

public final class DecryptMySQL
{
  public static void main(String[] args) throws Exception
  {
    SymmetricAlgorithm aes = SymmetricAlgorithm.newInstance("AES", "ECB");
    String passphrase = "STRING_USED_TO_ENCRYPT_DATA";
    byte[] keyBytes =  new byte[16];
    for (int i = 0; i < passphrase.length(); i++) {
      keyBytes[i % keyBytes.length] ^= passphrase.charAt(i);
    }
    aes.setKey(new SecretKeySpec(keyBytes, "AES"));
    aes.initDecrypt();

    Connection conn = null;
    Statement stmt = null;
    try {
      conn = DriverManager.getConnection(
        "jdbc:mysql://YOUR_HOSTNAME:3306/YOUR_DATABASE");
      stmt = conn.createStatement();
      ResultSet result = stmt.executeQuery(
        "SELECT ENCRYPT_COLUMN FROM ENCRYPT_TABLE");
      while (result.next()) {
        byte[] encrypted = result.getBytes("ENCRYPT_COLUMN");
        byte[] decrypted = aes.decrypt(encrypted);
        // Note this prints the decrypted bytes in hex in order to be
        // somewhat human-readable
        System.out.println(Convert.toHex(decrypted));
      }
    } catch (Exception ex) {
      if (stmt != null) {
        stmt.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
  }
}
```