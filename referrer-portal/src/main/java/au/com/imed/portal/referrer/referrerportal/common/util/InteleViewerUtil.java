package au.com.imed.portal.referrer.referrerportal.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import au.com.imed.portal.referrer.referrerportal.common.GlobalVals;

public class InteleViewerUtil {
  //private static final String [] SERVERS = new String [] {"10.20.160.100", "10.20.160.101", "10.30.160.100", "10.30.160.101"};
  //private static final String AUTH_TOKEN = "ea14d1651fb169a70f8a79fddb44d0d9";
  private static final List<String> ERROR_STRINGS = new ArrayList<String>(4);  // OK status but error message given
  
  public static final String URL_EV = "URL_EV";
  public static final String URL_IV = "URL_IV";
  public static final String URL_EV_MOBILE = "URL_EV_MOBILE";
  public static final String URL_EV_REST = "URL_EV_REST";

  static {
    ERROR_STRINGS.add("ACCOUNT_EXPIRED");
    ERROR_STRINGS.add("NEW_ACCOUNT");
    ERROR_STRINGS.add("ACCOUNT_LOCKED");
    ERROR_STRINGS.add("BAD_AUTH_TOKEN");
    ERROR_STRINGS.add("BAD_FALLBACK_USER");
    ERROR_STRINGS.add("UNAUTHORIZED_HOST");
  }
  
//  public static void main(String args[]) {
//    //System.out.println(generateUrls("macox", "77.9517264", "46.08952491")); // Need to run on portal servers
//    System.out.println(buildIvEvUrl("macox", "46.8328162", "46.20983401", "57e2d249fdfd9932885e02e4c4e0276d"));
//  }

  public static String [] generateUrls(final String userName, final String accessionNum, final String patientId) {
    return generateUrls(userName, accessionNum, patientId, URL_IV);
  }
  
  public static String [] generateUrls(final String userName, final String accessionNum, final String patientId, final String mode) {
    HttpComponentsClientHttpRequestFactory factory = null;
    try {
      factory = IgnoreCertFactoryUtil.createFactory();
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    RestTemplate restTemplate = (factory == null) ? new RestTemplate() : new RestTemplate(factory);
    
    final String postFix = buildAuthUrlPostfix(userName, GlobalVals.PACS_AUTH_TOKEN);
    List<String> urlList = new ArrayList<String>(GlobalVals.PACS_AUTH_SERVERS.length);
    for(int i = 0; i < GlobalVals.PACS_AUTH_SERVERS.length; i++) {
      try {
        String url = "https://" + GlobalVals.PACS_AUTH_SERVERS[i] + postFix;
        System.out.println(url);
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        if(HttpStatus.OK.equals(entity.getStatusCode())) {
          String tokenString = entity.getBody().replaceAll("\n", "");
          System.out.println(tokenString);
          if(!ERROR_STRINGS.contains(tokenString)) {
            String sessionId = tokenString.replace("OK|", "");
            if (URL_EV.equals(mode)) {
              urlList.add(buildIvEvUrl(userName, accessionNum, patientId, sessionId));
            }
            else if (URL_EV_MOBILE.equals(mode)) {
              urlList.add(buildIvEvMobileUrl(userName, accessionNum, patientId, sessionId));     
            }
            else if (URL_EV_REST.equals(mode)) {
              urlList.add(buildIvEvRestrictedUrl(accessionNum, sessionId));     
            }
            else
            {
              urlList.add(buildViewUrl(userName, accessionNum, patientId, sessionId));
            }
          }
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    return urlList.toArray(new String[urlList.size()]);
  }
  
  private static String buildAuthUrlPostfix(final String userName, final String token) {
    StringBuffer sb = new StringBuffer(256);
    sb.append("/UserAuthentication?action=createSession&username=");
    sb.append(userName);
    sb.append("&pacsUser=");
    sb.append(GlobalVals.PACS_FALLBACK);  // Falling back to this user if userName unavailable
    sb.append("&clientId=portal&authToken=");
    sb.append(token);
    return sb.toString();
  }
  
  private static String buildViewUrl(final String userName, final String accessionNumber, final String patientId, final String sessionId) {
    StringBuffer sb = new StringBuffer(256);
    sb.append(GlobalVals.PACS_URL + "/InteleBrowser/ViewImages?sessionId=");
    sb.append(sessionId);
    sb.append("&username=");
    sb.append(userName);
    sb.append("&accessionNumber=");
    sb.append(accessionNumber);
    sb.append("&patientId=");
    sb.append(patientId);
    sb.append("&clientId=portal");
    System.out.println(sb.toString());
    return sb.toString();
  }
  
  private static final String IVEV_IMAGE_URL_FMT = GlobalVals.PACS_URL + "/enhancedviewer/viewer/%s/%s?sessionId=%s&toolmode=superstacking&seriesLayoutName=1x1";
  private static final String IVEV_MOBILE_IMAGE_URL_FMT = GlobalVals.PACS_URL + "/m.enhancedviewer/viewer/%s/%s?sessionId=%s&toolmode=superstacking&seriesLayoutName=1x1";
  private static final String IVEV_REST_IMAGE_URL_FMT = GlobalVals.PACS_URL + "/Portal/view/orders/%s?sessionId=%s";
  //private static final String IVEV_REST_IMAGE_URL_FMT = GlobalVals.PACS_URL + "/Portal/view/orders/%s?SID=%s&signature=%s";
  private static String buildIvEvUrl(final String userName, final String accessionNum, final String patientId, final String sessionId) {
    return String.format(IVEV_IMAGE_URL_FMT, patientId, accessionNum, sessionId);
  }
  private static String buildIvEvMobileUrl(final String userName, final String accessionNum, final String patientId, final String sessionId) {
    return String.format(IVEV_MOBILE_IMAGE_URL_FMT, patientId, accessionNum, sessionId);
  }
  private static String buildIvEvRestrictedUrl(final String accessionNum, final String sessionId) {
    return String.format(IVEV_REST_IMAGE_URL_FMT, accessionNum, sessionId);
    //return String.format(IVEV_REST_IMAGE_URL_FMT, accessionNum, sessionId, getHash(accessionNum, sessionId));
  }
  
  private static final String SIGNKEY = "tobedecided";
  private static String getHash(final String accessionNumber, final String sessionId) {
    return DigestUtils.sha1Hex(sessionId + "|" + accessionNumber + "|" + SIGNKEY).substring(0, 20);
  }

}
