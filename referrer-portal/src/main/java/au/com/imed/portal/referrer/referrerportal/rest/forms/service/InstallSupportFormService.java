package au.com.imed.portal.referrer.referrerportal.rest.forms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;
import au.com.imed.portal.referrer.referrerportal.rest.forms.model.InstallSupport;

@Service
public class InstallSupportFormService {
	private Logger logger = LoggerFactory.getLogger(InstallSupportFormService.class);
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;

	@Value("${imed.email.test.receiver}")
	private String[] emailTestReceivers;

	@Autowired
	private ReferrerMailService emailService;
	
	@Autowired
	private CrmPostcodeJpaRepository crmPostcodeRepository;
	
	@Autowired
	private CrmProfileJpaRepository crmProfileRepository;
	
	private static final String SUBJECT = "Installation support required – BP/HL";
	public void processRequest(final InstallSupport installSupport) {
		CrmProfileEntity crm = getCrm(installSupport.getPracticePostcode()); 
		logger.info("processRequest() Send email to CRM " + crm);
		if("prod".equals(ACTIVE_PROFILE)) {
			try {
				String [] toCrm = crm != null ? new String [] {crm.getEmail()} : new String [0];
				emailService.sendMailWithCc(toCrm, new String [] {"alexandra.arter@i-med.com.au"}, SUBJECT, buildBody(installSupport));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			emailService.sendMailWithCc(new String [] {"alexandra.arter@i-med.com.au"}, emailTestReceivers, SUBJECT, buildBody(installSupport) + "CRM : " + crm.getName() + ", " + crm.getEmail());
		}
	}
	
	static private final String NL = "\n";
	private String buildBody(final InstallSupport installSupport) {
    StringBuffer sb = new StringBuffer();

    sb.append("Dear CRM,");
    sb.append(NL);
    sb.append("Please contact the person below who requires installation support for Best Practice/Healthlink e-Referral.");
    sb.append(NL);
    sb.append(NL);
    sb.append(NL);
    
    sb.append("First name: ");
    sb.append(installSupport.getFirstName());
    sb.append(NL);
    sb.append("Last name: ");
    sb.append(installSupport.getLastName());
    sb.append(NL);
    sb.append("Practice name: ");
    sb.append(installSupport.getPracticeName());
    sb.append(NL);
    sb.append("Practice phone number: ");
    sb.append(installSupport.getPracticePhone());
    sb.append(NL);
    sb.append("Practice postcode: ");
    sb.append(installSupport.getPracticePostcode());
    sb.append(NL);
    sb.append(NL);
    
    return sb.toString();
	}
	
	private CrmProfileEntity getCrm(final String postcode) {
		CrmProfileEntity profile = null;
		if(postcode != null && postcode.length() > 0) {
			List<CrmPostcodeEntity> plist = crmPostcodeRepository.findByPostcode(postcode);
			if(plist.size() > 0) {
				List<CrmProfileEntity> crmList = crmProfileRepository.findByName(plist.get(0).getName());
				profile = crmList.size() > 0 ? crmList.get(0) : null;
			}
		}
		logger.info("getCrm() Found crm for postcode {} ? {}", postcode, profile);
		return profile;
	}
}
