package au.com.imed.portal.referrer.referrerportal.common.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.ShareReport;

/**
 * Quick report access query string encode/decoder 
 *
 */
public class Aes128Util {
	private static Logger logger = LoggerFactory.getLogger(Aes128Util.class);
	
  // Key variables
  private static final String STR_PASSWORD = "M3dIc0MP72w0rdxZ";
  private static final String IV_STR = "K8h!f#P088naF1Mq";
  // Encryption variables
  private static final SecretKeySpec SECRET_KEY ;
  private static final byte[] IV = IV_STR.getBytes();
  // Query params
  private static final String PARAM_ACC_NUM = "accessionnumber";
  //private static final String PARAM_EXPIRED_AT = "expireat";
  public static final String PARAM_ITEM = "item";
  private static final String PARAM_PATIENT_ID = "patientId";
  //private static final String PARAM_FMT = PARAM_ACC_NUM + "=%s&" + PARAM_EXPIRED_AT + "=%s&" + PARAM_ITEM + "=%s&" + PARAM_PATIENT_ID + "=%s";
 
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
      str = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
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
  
  /**
   * [0] accnum [1] expireat or null
   */
  private static Map<String, String> getParams(final String decodedString) {
    Map<String, String> params = new HashMap<String, String>(5);
    try {
      String [] splits = decodedString.split("&|=");
      final int setlen = splits.length / 2;
      for(int i = 0; i < setlen; i ++) {
        params.put(splits[i*2], splits[(i*2) + 1]);
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    logger.info("getParams " + params);
    return params;
  }
  
  private static boolean isFreshDate(final String expireat) throws Exception 
  {
    Date expdate = new SimpleDateFormat("yyyyMMddHHmm").parse(expireat);
    return expdate.compareTo(new Date()) > 0 && expdate.compareTo(getMaxExpiryDate()) < 0;
  }
  
  private static Date getMaxExpiryDate() 
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, 1);
    return cal.getTime();
  }
  
  public static ShareReport getAccessionNumberFromSecurityCode(final String imsec) {
    ShareReport sr = new ShareReport();
    logger.info("getAccessionNumberFromSecurityCode() imsec = " + imsec);
    if(imsec != null && imsec.length() > 0) {
      try
      {
        String decoded = decrypt(URLDecoder.decode(imsec.trim(), StandardCharsets.UTF_8).replaceAll(" ", "+")); // Convert spaced plus signs
        logger.info("getAccessionNumberFromSecurityCode(): decoded = " + decoded);
        Map<String, String> params = getParams(decoded);
        // urls are perpetual
        sr.setAccessionNumber(params.get(PARAM_ACC_NUM));
        String itemstr = params.get(PARAM_ITEM);
        sr.setItem(StringUtils.isNotEmpty(itemstr) ? itemstr : "report"); // Special treatment for old format
        sr.setPatientId(params.get(PARAM_PATIENT_ID));
      }
      catch(Exception ex) {
        logger.info("getAccessionNumberFromSecurityCode() Invalid imsec");
        ex.printStackTrace();
      }
    }
    logger.info("getAccessionNumberFromSecurityCode(): ShareReport acc# = " + sr.getAccessionNumber()); 
    return sr;
  }
  
//  public static String encodeShareReportParameters(final ShareReport shareReport) {
//    String imsec = null;
//    
//    if(shareReport != null) {
//      imsec = encrypt(String.format(PARAM_FMT,
//          shareReport.getAccessionNumber(),
//          new SimpleDateFormat("yyyyMMddHHmm").format(getMaxExpiryDate()),
//          shareReport.getItem(),
//          shareReport.getPatientId()));
//    }
//    
//    return imsec;
//  }

//  public static void main(String args[]) throws Exception
//  {
//    final String strToEncrypt = "accessionnumber=77.160951&item=image,report";
//   // final String strToEncrypt = "accessionnumber=12.10720408&item=image,report";
//    System.out.println("String to Encrypt: " + strToEncrypt); 
//    
//    String encoded = encrypt(strToEncrypt.trim());
//    System.out.println("Encrypted: " + encoded);
//
//    System.out.println(getAccessionNumberFromSecurityCode(encoded));
////    String decoded = decrypt(encoded.trim());
////    System.out.println("Decrypted : " + decoded);
////    
////    String [] params = getParams(decoded);
////    for(int i = 0; i < params.length; i++)
////      System.out.println(params[i]);
////    System.out.println(isFreshDate(params[1]));
//  }
}
