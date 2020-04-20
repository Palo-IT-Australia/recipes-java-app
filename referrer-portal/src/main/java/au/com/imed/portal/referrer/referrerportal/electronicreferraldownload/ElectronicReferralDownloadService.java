package au.com.imed.portal.referrer.referrerportal.electronicreferraldownload;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.repository.ElectronicReferralJPARepository;

@Service
public class ElectronicReferralDownloadService {
	private Logger logger = LoggerFactory.getLogger(ElectronicReferralDownloadService.class);
	
	public static final String SECRET_MODE_PATIENT = "patient";
	public static final String SECRET_MODE_REFERRER = "referrer";
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;

	@Value("${imed.application.url}")
	private String PORTAL_ROOT_URL;
	
	@Autowired
	private ReferrerMailService emailService;
	
	@Autowired
	private ElectronicReferralJPARepository electronicReferralRepository;
	
	public ElectronicReferralDownloadSecretModel decodeToSecretModel(String secret) {
		ElectronicReferralDownloadSecretModel erdsm = null;
		if(StringUtils.isNotEmpty(secret)) {
			try {
				String json = ElectronicReferralDownloadAesUtil.decrypt(secret);
				if(StringUtils.isNotEmpty(json)) {
					logger.info("decodeToSecretModel json = " + json);
					ObjectMapper objectMapper = new ObjectMapper();
					erdsm = objectMapper.readValue(json, ElectronicReferralDownloadSecretModel.class);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		logger.info("decodeToSecretModel() returning " + erdsm);
		return erdsm;
	}
	
	public ElectronicReferralForm getMatchingEntity(ElectronicReferralDownloadSecretModel secretModel, String passcode) {
		ElectronicReferralForm entity = null;
		try {
			if(secretModel != null && secretModel.getTableId() > 0 && StringUtils.isNotEmpty(passcode)) {
				ElectronicReferralForm candidate = electronicReferralRepository.findById(secretModel.getTableId()).orElse(null);
				if(candidate != null) {
					if(SECRET_MODE_REFERRER.equals(secretModel.getMode())) {
						// provider #
						if(candidate.getDoctorProviderNumber().equals(passcode)) {
							logger.info("Found matching referrer provider#");
							entity = candidate;
						}
					} else if (SECRET_MODE_PATIENT.equals(secretModel.getMode())) {
						// dob
						if(candidate.getPatientDob().equals(passcode)) {
							logger.info("Found matching patient dob");
							entity = candidate;
						}
					}
					
					if(entity == null) {
						logger.info("Invalid passcode.");
						// TODO update failue# + 1 and save
					}
				} else {
					logger.info("No entity candidate found.");
				}
			} else {
				logger.info("Empty secret or passcode.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return entity;
	}
	
	public String generateSecretUrl(int tableId, String mode) {
		ElectronicReferralDownloadSecretModel model = new ElectronicReferralDownloadSecretModel();
		model.setMode(mode);
		model.setTableId(tableId);
		model.setDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		String url = PORTAL_ROOT_URL + "/electronicreferraldownload?code=" + encodeToSecretString(model);
		logger.info("generateSecretUrl() " + url); 
		return url;
	}
	
	private String encodeToSecretString(ElectronicReferralDownloadSecretModel model) {
		String secret = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.convertValue(model, String.class);
			logger.info("encodeToSecretString() json = " + json);
			secret = ElectronicReferralDownloadAesUtil.encrypt(json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return secret;
	}
	
	public void download(ElectronicReferralForm entity, HttpServletResponse response) {
		try
		{
//			final String urlcodedb = AesStringUtil.decrypt(urlcode.replaceAll(" ", "+"));
//			List<ReportAccessEntity> list = reportAccessRepository.findByUrlCode(urlcodedb);
//			if(list.size() > 0)
//			{
//				ReportAccessEntity reportAccess = list.get(0);
//				boolean expired = AesStringUtil.isExpired(reportAccess.getExpiredAt());
//				System.out.println("expired ? " + expired); 
//				boolean active = reportAccess.getFailures() <= MAX_FAILURES;
//				if(active) {
//					if(!expired && HashPasscodeUtil.validatePassword(passcode, reportAccess.getPasscodeHash(), reportAccess.getPasscodeSalt()))
//					{
//						System.out.println("download() orderUri" + reportAccess.getOrderUri());
//						System.out.println("download() reportUri" + reportAccess.getReportUri());
//						Map<String, String> paramMap = new HashMap<>(2);
//						paramMap.put("orderUri", reportAccess.getOrderUri());
//						paramMap.put("reportUri", reportAccess.getReportUri());
//						ResponseEntity<byte []> entity = reportService.doRestGet(PortalConstant.REP_VISAGE_USER, paramMap, byte[].class);
//						if(HttpStatus.OK.equals(entity.getStatusCode())) {
//							response.setContentType("application/pdf; name=I-MEDRadiology_Report.pdf");
//							response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=I-MEDRadiology_Report.pdf"); // inline=open on browser, attachment=download
//							// Secure PDF
//							//response.getPortletOutputStream().write(entity.getBody());
//							PdfReader reader = new PdfReader(entity.getBody());
//							PdfStamper stamper = new PdfStamper(reader, response.getOutputStream());
//							Map<String, String> info = reader.getInfo();
//							info.put("Title", "IMED PORTAL REPORT DOWNLOAD");
//							info.put("Subject", "IMED PATIENT REPORT");
//							info.put("Keywords", "IMED PATIENT REPORT");
//							info.put("Author", "OBTAINED BY IMED REPORT DOWNLOAD at " + new PdfDate().toString());
//							stamper.setMoreInfo(info);
//							stamper.setEncryption(passcode.getBytes(), "IMEDPDFOWNERPASSCODE".getBytes(),
//									0, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
//							stamper.close();
//							reader.close();
//						}
//						else
//						{
//							System.out.println("Failed to obtain report.");  
//							response.getWriter().write("Error: Failed to download report.");
//						}
//					}
//					else {
//						incrementFailureAccount(reportAccess);
//						System.out.println("Passcode is wrong or expired.");
//						response.getWriter().write("Error: Passcode is wrong or expired.");
//					}
//				}
//				else {
//					response.getWriter().write("Error: Too many failed attempts.");          
//				}
//			}
//			else {
//				System.out.println("No DB entity found for the url code.");
//				response.getWriter().write("Error: URL is invalid.");
//			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().write("Error: Failed to download the E-Referral.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
