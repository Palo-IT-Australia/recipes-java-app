package au.com.imed.portal.referrer.referrerportal.common.util;

import java.net.URLEncoder;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.common.GlobalVals;


@Component
public class Aes256Util {
  private static final String ENCRYPTION_MODE = "AES/CBC/PKCS5Padding";
  //private static final String ENCRYPTION_KEY = "rpJupVvvHiX5kgrPllV8mWsurbzSu9D99kUwamFdL9I=";
  
  private static SecretKey getSecret() {
    byte [] binkey = Base64.decodeBase64(GlobalVals.CARESTREAM_KEY);
    return new SecretKeySpec(binkey, 0, 256/8, "AES"); 
  }
  
  public static String encodeToUrlQuery(final String plainText) throws Exception {
    Cipher aesCipher = Cipher.getInstance(ENCRYPTION_MODE);  //getting cipher for AES
    aesCipher.init(Cipher.ENCRYPT_MODE, getSecret(), getIv());  //initializing cipher for encryption with key
    byte [] enc = aesCipher.doFinal(plainText.getBytes("UTF-8"));
    String sixfour = Base64.encodeBase64String(enc);
    String query = URLEncoder.encode(sixfour, "UTF-8");
    return query;
  }
  
  private static IvParameterSpec getIv() {
    final byte[] iv = new byte[16];
    Arrays.fill(iv, (byte) 0x00);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    return ivParameterSpec;
  }

//  public static void main(String[] args) throws Exception {
//    System.out.println(encodeToUrlQuery("user_name=huehara&patient_id=46.21220991&accession_number=77.3592442&work_archive_ae_title=imeddvcst2FED&dicom_priors_ae_title=imeddvcst2FED&details_bar=false&hide_top=all&hide_Sides=all&force_all_browsers=true"));
//    //tQTFnMYQklYV%2FxfeEYFSJMdukeE5C337xXWF338dkl4kIOiL5DdLpugk36rRO5i91yG2fqTxg2xFtLaLAlD81Hay3YC1h%2BR23Exbt5Uxg8W7C0dHMBNN5UamYYl7lt1gFwAVIsp%2BPsM7aIZWGLfsdRnNZakup2xIJHqpxt8QracZ31vwCehoJOlCfPN11nSdCXDMp2XdpBYSBdOicVnza3ZyudE0cvpcB7OGS0drbIa3Wq%2BEaWBZgv9YZNC2ayBgVSHSNKlJupiYP1BTCRtDD8KpNvBwOq1Xp9AFzwhyL2g%3D
//  }
}
