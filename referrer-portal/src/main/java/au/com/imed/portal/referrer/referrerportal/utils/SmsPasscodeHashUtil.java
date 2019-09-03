package au.com.imed.portal.referrer.referrerportal.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SmsPasscodeHashUtil {
  private static final int ITERATIONS = 800;

//  public static void main(String[] args) throws Exception {
//    final String  originalPassword = randomString(8);
//    System.out.println("original password = " + originalPassword);
//    String generatedSecuredPasswordHash = generateStorngPasswordHash(originalPassword);
//    System.out.println(generatedSecuredPasswordHash);
//    
//    String [] places = generatedSecuredPasswordHash.split(":");
//    boolean matched = validatePassword(originalPassword, places[1], places[0]);
//    System.out.println(matched);
//     
//    matched = validatePassword("password", places[1], places[0]);
//    System.out.println(matched);
//  }
  
  private static final String CHARSET = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"; // No IlO to avoid confusion
  private static SecureRandom rnd = new SecureRandom();

  public static String randomString(int len)
  {
     StringBuilder sb = new StringBuilder( len );
     for( int i = 0; i < len; i++ ) 
        sb.append( CHARSET.charAt( rnd.nextInt(CHARSET.length()) ) );
     return sb.toString();
  }
  
  /**
   * @return salt:hashpassword
   */
  public static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
  {
      int iterations = ITERATIONS;
      char[] chars = password.toCharArray();
      byte[] salt = getSalt();
       
      PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] hash = skf.generateSecret(spec).getEncoded();
      return toHex(salt) + ":" + toHex(hash);
  }
   
  private static byte[] getSalt() throws NoSuchAlgorithmException
  {
      SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
      byte[] salt = new byte[16];
      sr.nextBytes(salt);
      return salt;
  }
   
  private static String toHex(byte[] array) throws NoSuchAlgorithmException
  {
      BigInteger bi = new BigInteger(1, array);
      String hex = bi.toString(16);
      int paddingLength = (array.length * 2) - hex.length();
      if(paddingLength > 0)
      {
          return String.format("%0"  +paddingLength + "d", 0) + hex;
      }else{
          return hex;
      }
  }
  
  public static boolean validatePassword(String password, String storedPassword, String saltCode) throws NoSuchAlgorithmException, InvalidKeySpecException
  {
      int iterations = ITERATIONS;
      byte[] salt = fromHex(saltCode);
      byte[] hash = fromHex(storedPassword);
       
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] testHash = skf.generateSecret(spec).getEncoded();
       
      int diff = hash.length ^ testHash.length;
      for(int i = 0; i < hash.length && i < testHash.length; i++)
      {
          diff |= hash[i] ^ testHash[i];
      }
      return diff == 0;
  }
  private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
  {
      byte[] bytes = new byte[hex.length() / 2];
      for(int i = 0; i<bytes.length ;i++)
      {
          bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
      }
      return bytes;
  }
}
