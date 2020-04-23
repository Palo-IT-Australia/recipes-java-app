package au.com.imed.portal.referrer.referrerportal.electronicreferraldownload;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.imed.portal.referrer.referrerportal.common.util.PdfGenerator;
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
	
//	@Autowired
//	private ReferrerMailService emailService;
	
	@Autowired
	private ElectronicReferralJPARepository electronicReferralRepository;
	
	@Autowired
	PdfGenerator pdfReferralGenerator;
	
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
				logger.info("From table Id " + secretModel.getTableId());
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
			String json = objectMapper.writeValueAsString(model);
			logger.info("encodeToSecretString() json = " + json);
			secret = ElectronicReferralDownloadAesUtil.encrypt(json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return secret;
	}
	
	public void download(ElectronicReferralForm entity, ElectronicReferralDownloadSecretModel secretModel, HttpServletResponse response) {
		try
		{
			logger.info("Downloading pdf...");
			response.setContentType("application/pdf; name=I-MED_E-Referral.pdf");
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=I-MED_E-Referral.pdf"); // inline=open on browser, attachment=download
			byte [] pdf = this.pdfReferralGenerator.generatePdfReferral(entity, 
					"patient".equalsIgnoreCase(secretModel.getMode()), 
					"referrer".equalsIgnoreCase(secretModel.getMode()),
					false, null);
			response.setContentLength(pdf.length);
			ServletOutputStream os = response.getOutputStream();
			try {
				os.write(pdf , 0, pdf.length);
			} catch (Exception excp) {
				excp.printStackTrace();
			} finally {
				os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().write("Error: Failed to download the E-Referral.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
//	public static void main(String args[]) {
//		System.out.println(new ElectronicReferralDownloadService().generateSecretUrl(78, "patient"));
//	}

}
