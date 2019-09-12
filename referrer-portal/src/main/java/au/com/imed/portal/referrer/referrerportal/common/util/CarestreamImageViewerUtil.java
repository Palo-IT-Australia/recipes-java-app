package au.com.imed.portal.referrer.referrerportal.common.util;

public class CarestreamImageViewerUtil {
	public static String generateUrl(final String carestreamUrl, String vueMotionKey, final String userName,
			final String accessionNum, final String patientId) {
		String query = "";
		try {
			query = Aes256Util.encodeToUrlQuery(buildParamString(accessionNum, patientId, userName), vueMotionKey);
		} catch (Exception ex) {
			// NOP
		}
		return carestreamUrl + "?shs=" + query;
	}

	private static String buildParamString(final String accessionNum, final String patientId, final String userName) {
		// AUTH
		// return
		// String.format("user_name=%s&patient_id=%s&accession_number=%s&work_archive_ae_title=imedtscs01FED&details_bar=false&hide_top=partial&hide_Sides=report&force_all_browsers=true&signout=false",
		// userName, patientId, accessionNum);
		// PROD
		return String.format(
				"user_name=%s&patient_id=%s&accession_number=%s&work_archive_ae_title=imedpdcs01FED&details_bar=false&hide_top=partial&hide_Sides=report&force_all_browsers=true&signout=false",
				userName, patientId, accessionNum);
	}

	public static String generateUrlByStudyInstanceUid(final String carestreamUrl, String vueMotionKey, final String userName,
			final String studyInstanceUid, final String patientId) {
		String query = "";
		try {
			query = Aes256Util.encodeToUrlQuery(
					buildParamStringByStudyInstanceUid(studyInstanceUid, patientId, userName), vueMotionKey);
		} catch (Exception ex) {
			// NOP
		}
		return carestreamUrl + "?shs=" + query;
	}

	private static String buildParamStringByStudyInstanceUid(final String studyInstanceUid, final String patientId,
			final String userName) {
		// TODO remove dicom_priors_ae_title?
		// AUTH
		// return
		// String.format("user_name=%s&patient_id=%s&study_instance_uid=%s&work_archive_ae_title=imedtscs01FED&dicom_priors_ae_title=imedtscs01FED&details_bar=false&hide_top=partial&hide_Sides=all&force_all_browsers=true&signout=false",
		// userName, patientId, studyInstanceUid);
		// PROD
		return String.format(
				"user_name=%s&patient_id=%s&study_instance_uid=%s&work_archive_ae_title=imedpdcs01FED&dicom_priors_ae_title=imedpdcs01FED&details_bar=false&hide_top=partial&hide_Sides=all&force_all_browsers=true&signout=false",
				userName, patientId, studyInstanceUid);
	}

	// for testing
//  public static void main(String args[]) throws Exception
//  {
//    final String NSW = "https://csnsw.i-med.com.au/portal";
//    final String VIC = "https://csvic.i-med.com.au/portal";
//    
//    final String userName = "huehara";
//    final String accessionNum = "46.12543889"; 
//    final String accessionNumMulti = "46.10614142\\\\46.12543889";
//    final String patientId = "46.15379651";
//    final String studyInstanceUid = "1.3.6.1.4.1.1430.46.4.12543889.2"; //"1.3.6.1.4.1.1430.46.4.12543889.1"
//    
//    System.out.println("------NSW--------");
//    System.out.println(generateUrl(NSW, userName, accessionNum, patientId));
//    System.out.println(generateUrl(NSW, userName, accessionNumMulti, patientId));
//    System.out.println(generateUrlByStudyInstanceUid(NSW, userName, studyInstanceUid, patientId));
//    System.out.println("------VIC--------");
//    System.out.println(generateUrl(VIC, userName, accessionNum, patientId));
//    System.out.println(generateUrl(VIC, userName, accessionNumMulti, patientId));
//    System.out.println(generateUrlByStudyInstanceUid(VIC, userName, studyInstanceUid, patientId));
//  }
}
