package au.com.imed.portal.referrer.referrerportal.reportaccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReportAccessEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReportAccessRepository;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Order;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Patient;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.SearchOrders;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetPatientService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.ReportService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.SearchOrdersService;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;

@Service
public class ReportAccessService {
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;

	@Value("${imed.application.url}")
	private String PORTAL_ROOT_URL;

	@Value("${imed.email.test.receiver}")
	private String[] testEmailReceivers;

	private static final int MAX_FAILURES = 3;
	
	@Autowired
	private SearchOrdersService searchOrderSerive;

	@Autowired
	private GetPatientService patientService;

	@Autowired
	@Qualifier("ViewHtmlReportService")
	private ReportService reportService;

	@Autowired 
	private ReportAccessRepository reportAccessRepository;

	@Autowired
	private GoFaxSmsService smsService;

	@Autowired
	private ReferrerMailService emailService;

	public List<Order> listOrders(final String patientId) {
		List<Order>list = new ArrayList<>(0);
		if(patientId != null && patientId.trim().length() > 0)
		{
			Map<String, String> paramMap = new HashMap<>(3);
			paramMap.put("searchType", "all");
			paramMap.put("patientId", patientId.trim());
			paramMap.put("orderStatus", "in progress,complete");
			list = searchOrderSerive.doRestGet(PortalConstant.REP_VISAGE_USER, paramMap, SearchOrders.class).getBody().getOrders();
		}
		return list;
	}

	public boolean makeAvailable(final String reportUri, final String orderUri, final String patientUri) {
		ReportAccessEntity entity = new ReportAccessEntity();
		entity.setReportUri(reportUri);
		entity.setOrderUri(orderUri);

		boolean isSuccess = false;
		String msg = "Email and SMS have been sent to the patient";
		try 
		{
			final String  originalPassword = HashPasscodeUtil.randomString(8);
			String generatedSecuredPasswordHash = HashPasscodeUtil.generateStorngPasswordHash(originalPassword);
			String [] places = generatedSecuredPasswordHash.split(":");
			entity.setPasscodeSalt(places[0]);
			entity.setPasscodeHash(places[1]);

			final String urlCode = HashPasscodeUtil.randomString(32);
			entity.setUrlCode(urlCode);    
			String encoded = AesStringUtil.encrypt(urlCode);
			System.out.println("encoded = " + encoded);
			final String baseUrl = PORTAL_ROOT_URL + "/reportdownload";

			Map<String, String> paramMap = new HashMap<>(2);
			paramMap.put("patientUri", patientUri);

			ResponseEntity<Patient> patientEntity = patientService.doRestGet(PortalConstant.REP_VISAGE_USER, paramMap, Patient.class);
			System.out.println("available() status code " + patientEntity.getStatusCode());
			if(HttpStatus.OK.equals(patientEntity.getStatusCode())) {
				Patient patient = patientEntity.getBody();
				final String patemail = patient.getEmail();
				final String patmobile = patient.getMobile();
				System.out.println("available() email and mobile = " + patemail + ", " + patmobile);    
				if(patemail != null && patemail.length() > 0 && patmobile != null && patmobile.length() > 0 && patmobile.startsWith("04")) {
					if ("prod".equals(ACTIVE_PROFILE)) {
						emailService.sendReportHtml(new String [] {patemail}, baseUrl + "?code=" + encoded);   
						smsService.send(new String [] {patmobile}, "I-MED Radiology Network Patient Report Access Passcode = " + originalPassword);
					} else {
						emailService.sendReportHtml(testEmailReceivers, baseUrl + "?code=" + encoded);
						smsService.send(new String [] {"0437118213"}, "I-MED Radiology Network Patient Report Access Passcode = " + originalPassword);
					}

					entity.setExpiredAt(AesStringUtil.getExpiryDate());
					reportAccessRepository.saveAndFlush(entity);
					isSuccess = true;
				} 
				else { 
					msg = "Patient's email address or mobile number is missing on Visage.";
				}
			}
			else
			{
				msg = "Patient could not be found in Visage.";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("msg " + msg);
		return isSuccess;
	}

	public boolean isUrlcodeValid(final String urlcode) {
		boolean isValid = false;
		try
		{
			final String urlcodedb = AesStringUtil.decrypt(urlcode.replaceAll(" ", "+"));
			List<ReportAccessEntity> list = reportAccessRepository.findByUrlCode(urlcodedb);
			if(list.size() > 0)
			{
				isValid = list.get(0).getFailures() < MAX_FAILURES;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return isValid;
	}

	public void download(final String urlcode, final String passcode, HttpServletResponse response) {
		try
		{
			System.out.println("download() urlcode = " + urlcode);
			System.out.println("download() passcode = " + passcode);
			final String urlcodedb = AesStringUtil.decrypt(urlcode.replaceAll(" ", "+"));
			List<ReportAccessEntity> list = reportAccessRepository.findByUrlCode(urlcodedb);
			if(list.size() > 0)
			{
				ReportAccessEntity reportAccess = list.get(0);
				boolean expired = AesStringUtil.isExpired(reportAccess.getExpiredAt());
				System.out.println("expired ? " + expired); 
				boolean active = reportAccess.getFailures() <= MAX_FAILURES;
				if(active) {
					if(!expired && HashPasscodeUtil.validatePassword(passcode, reportAccess.getPasscodeHash(), reportAccess.getPasscodeSalt()))
					{
						System.out.println("download() orderUri" + reportAccess.getOrderUri());
						System.out.println("download() reportUri" + reportAccess.getReportUri());
						Map<String, String> paramMap = new HashMap<>(2);
						paramMap.put("orderUri", reportAccess.getOrderUri());
						paramMap.put("reportUri", reportAccess.getReportUri());
						ResponseEntity<byte []> entity = reportService.doRestGet(PortalConstant.REP_VISAGE_USER, paramMap, byte[].class);
						if(HttpStatus.OK.equals(entity.getStatusCode())) {
							response.setContentType("application/pdf; name=I-MEDRadiology_Report.pdf");
							response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=I-MEDRadiology_Report.pdf"); // inline=open on browser, attachment=download
							// Secure PDF
							//response.getPortletOutputStream().write(entity.getBody());
							PdfReader reader = new PdfReader(entity.getBody());
							PdfStamper stamper = new PdfStamper(reader, response.getOutputStream());
							Map<String, String> info = reader.getInfo();
							info.put("Title", "IMED PORTAL REPORT DOWNLOAD");
							info.put("Subject", "IMED PATIENT REPORT");
							info.put("Keywords", "IMED PATIENT REPORT");
							info.put("Author", "OBTAINED BY IMED REPORT DOWNLOAD at " + new PdfDate().toString());
							stamper.setMoreInfo(info);
							stamper.setEncryption(passcode.getBytes(), "IMEDPDFOWNERPASSCODE".getBytes(),
									0, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
							stamper.close();
							reader.close();
						}
						else
						{
							System.out.println("Failed to obtain report.");  
							response.getWriter().write("Error: Failed to download report.");
						}
					}
					else {
						incrementFailureAccount(reportAccess);
						System.out.println("Passcode is wrong or expired.");
						response.getWriter().write("Error: Passcode is wrong or expired.");
					}
				}
				else {
					response.getWriter().write("Error: Too many failed attempts.");          
				}
			}
			else {
				System.out.println("No DB entity found for the url code.");
				response.getWriter().write("Error: URL is invalid.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().write("Error: Failed to download the report.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void incrementFailureAccount(ReportAccessEntity reportAccess) {
		byte current = reportAccess.getFailures();
		reportAccess.setFailures(++current);
		reportAccessRepository.saveAndFlush(reportAccess);
	}
}
