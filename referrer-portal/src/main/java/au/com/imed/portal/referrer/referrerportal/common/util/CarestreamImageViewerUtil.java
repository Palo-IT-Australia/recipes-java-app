package au.com.imed.portal.referrer.referrerportal.common.util;

import au.com.imed.portal.referrer.referrerportal.common.GlobalVals;

public class CarestreamImageViewerUtil {
	
	public static String generateUrl(final String carestreamUrl, final String userName, final String accessionNum, final String patientId) {    
    String query = "";
    try {
      query = Aes256Util.encodeToUrlQuery(buildParamString(accessionNum, patientId, userName));
    } catch (Exception ex) {
      // NOP
    }
    return carestreamUrl + "/?shs=" + query; 
  }
  
  private static String buildParamString(final String accessionNum, final String patientId, final String userName) {
    return String.format(GlobalVals.CARESTREAM_PARAM_FORMAT, userName, patientId, accessionNum);  
  }  
  
  public static String generateUrlByStudyInstanceUid(final String carestreamUrl, final String userName, final String studyInstanceUid, final String patientId) {    
    String query = "";
    try {
      query = Aes256Util.encodeToUrlQuery(buildParamStringByStudyInstanceUid(studyInstanceUid, patientId, userName));
    } catch (Exception ex) {
      // NOP
    }
    return carestreamUrl + "/?shs=" + query; 
  }
  
  private static String buildParamStringByStudyInstanceUid(final String studyInstanceUid, final String patientId, final String userName) {
    return String.format(GlobalVals.CARESTREAM_PARAM_FORMAT_SUID, userName, patientId, studyInstanceUid);    
  }  
}
