package au.com.imed.portal.referrer.referrerportal.common.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Generic string encoder BASE64/AES128 such as password in DB
 *
 */
public class Aes128StringEncodeUtil {
	//Key variables
  private static final String STR_PASSWORD = "7Hhg00skb6AAtr33";
  private static final String IV_STR = "9!!sld0ljYEDDsm1";
  // Encryption variables
  private static final SecretKeySpec SECRET_KEY ;
  private static final byte[] IV = IV_STR.getBytes();

  static {
    SECRET_KEY = createSecretKey(STR_PASSWORD);
  }

  private static SecretKeySpec createSecretKey(String password) {
    MessageDigest sha = null;
    byte [] key;
    try {
      key = password.getBytes("UTF-8");
      sha = MessageDigest.getInstance("SHA-1");
      key = sha.digest(key);
      key = Arrays.copyOf(key, 16); 
      return new SecretKeySpec(key, "AES");          
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static String encrypt(String strToEncrypt)
  {
    String str = null;
    try
    {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // = 7Padding
      cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new IvParameterSpec(IV));
      str = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }
    catch (Exception e)
    {
      System.out.println("Error while encrypting: "+e.toString());
      e.printStackTrace();
    }
    return str;
  }

  public static String decrypt(String strToDecrypt)
  {
    String str = null;
    try
    {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new IvParameterSpec(IV));
      str = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
    }
    catch (Exception e)
    {

      System.out.println("Error while decrypting: "+e.toString());
      e.printStackTrace();
    }
    return str;
  }

//  public static void main(String args[]) throws Exception
//  {
//    final String strToEncrypt = "1234567890";
//    System.out.println("String to Encrypt: " + strToEncrypt); 
//
//    String encoded = encrypt(strToEncrypt.trim());
//    System.out.println("Encrypted: " + encoded);
//
//    String decoded = decrypt(encoded.trim());
//    System.out.println("Decrypted : " + decoded);
//  }
}
