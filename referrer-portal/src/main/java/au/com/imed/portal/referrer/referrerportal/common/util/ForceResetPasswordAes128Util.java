package au.com.imed.portal.referrer.referrerportal.common.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.imed.portal.referrer.referrerportal.model.ForceResetPassword;

public class ForceResetPasswordAes128Util {
private static Logger logger = LoggerFactory.getLogger(ForceResetPasswordAes128Util.class);
	
  // Key variables
  private static final String STR_PASSWORD = "Lpp76T54HH434xUt";
  private static final String IV_STR = "99HH!GrRf87#NNmu";

  // Encryption variables
  private static final SecretKeySpec SECRET_KEY ;
  private static final byte[] IV = IV_STR.getBytes();
 
  static {
    SECRET_KEY = createSecretKey(STR_PASSWORD);
  }

  private static SecretKeySpec createSecretKey(String password) {
    SecretKeySpec secret = null;
    try {
      secret = new SecretKeySpec(STR_PASSWORD.getBytes("UTF-8"), 0, 128/8, "AES");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return secret;
  }

  private static String encrypt(String strToEncrypt)
  {
    String str = null;
    try
    {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // = 7Padding
      cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new IvParameterSpec(IV));
      str = Base64.encodeBase64URLSafeString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return str;
  }

  private static String decrypt(String strToDecrypt)
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
      e.printStackTrace();
    }
    return str;
  }
  
  public static String getSecretParameterValue(final String uid, final String temp) throws Exception {
  	ForceResetPassword frp = new ForceResetPassword();
  	frp.setUid(uid);
  	frp.setTemp(temp);
  	String json = new ObjectMapper().writeValueAsString(frp);
  	logger.info("Original JSON : " + json);
  	return encrypt(json);
  }
  
  public static ForceResetPassword getObjectFromSecretParameterValue(final String secret) throws Exception {
  	String json = decrypt(secret);
  	ForceResetPassword frp = new ObjectMapper().readValue(json, ForceResetPassword.class);
  	logger.info("Object " + frp);
  	return frp;
  }
  
  private static final String CHARSET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"; // No 01IlO to avoid confusion
  private static SecureRandom rnd = new SecureRandom();

  public static String randomString(int len)
  {
     StringBuilder sb = new StringBuilder( len );
     for( int i = 0; i < len; i++ ) 
        sb.append( CHARSET.charAt( rnd.nextInt(CHARSET.length()) ) );
     return sb.toString();
  }
}
