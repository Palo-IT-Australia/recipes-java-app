package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.repository.ElectronicReferralJPARepository;

@Service
public class ElectronicReferralService {

	@Autowired
	ElectronicReferralJPARepository electronicReferralJPARepository;
	
	@Autowired
	ReferrerMailService emailService;
	
	

	public ElectronicReferralForm save(ElectronicReferralForm electronicReferralForm) {
		electronicReferralForm = electronicReferralJPARepository.save(electronicReferralForm);
		
		return electronicReferralForm;
	}
	
	
//	private void sendEmailToCrm() {
//		String subject = "Electronic referral submitted";
//		String emailBody = "";
//		emailService.sendHtmlMail(crmEmailIdList, subject, content);
//	}
	
	
	
	

}
