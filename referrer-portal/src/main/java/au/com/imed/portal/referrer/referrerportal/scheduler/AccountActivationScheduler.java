package au.com.imed.portal.referrer.referrerportal.scheduler;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.email.UserMessageUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerActivationJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.UserPreferencesJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.VisageRequestAuditJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.UserPreferencesEntity;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer.Practice;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;

@Component
public class AccountActivationScheduler {
	private Logger logger = LoggerFactory.getLogger(AccountActivationScheduler.class);
	
	@Autowired
	private ReferrerActivationJpaRepository activationRepository;
	
	@Autowired
	private VisageRequestAuditJPARepository auditRepository;
	
	@Autowired
	private UserPreferencesJPARepository preferencesRepository;
	
	@Autowired
	private CrmPostcodeJpaRepository crmPostcodeRepository;
	
	@Autowired
	private CrmProfileJpaRepository crmProfileRepository;
	
	@Autowired
	private ReferrerAccountService accountService;
	
	@Autowired
	private ReferrerMailService emailService;
	
	@Autowired
	private GetReferrerService getReferrerService;
	
	@Value("${imed.scheduler.server.name}")
	private String SCHEDULER_SERVER_NAME;
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
  @Value("#{servletContext.contextPath}")
  private String APPLICAION_CONTEXT_PATH;
	
	@Scheduled(cron = "0 0 1 ? * MON-FRI")
	public void scheduleAccountReminderTask() {
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("scheduleAccountReminderTask() starting...");
				final Date now = new Date();    
		    Calendar cal = Calendar.getInstance();
		    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		    
		    /**
		     * Activation 7 day
		     */
		    cal.setTime(now);
		    cal.add(Calendar.DATE, -7);
		    final Date to = cal.getTime();
		    
		    cal.setTime(now);
		    cal.add(Calendar.DATE, -8);
		    final Date from = cal.getTime();
		    
		    List<ReferrerActivationEntity> accounts = activationRepository.findByActivatedAtBetween(from, to);		    
		    for(ReferrerActivationEntity acnt : accounts) {
		    	String uid = acnt.getUid();
		    	logger.info("Checking if any audit for uid : " + uid);
		    	if(auditRepository.countByUsernameAndAuditAtGreaterThan(uid, acnt.getActivatedAt()) == 0) {
		    		CrmProfileEntity crm = getCrm(getReferrerPostcode(uid));
		    		String email = acnt.getEmail();
		    		logger.info("Sending referrer email : " + email + ", crm = " + crm);
		    		if("prod".equals(ACTIVE_PROFILE)) {
							try {
								emailService.sendImoHtmlMail(new String [] {acnt.getEmail()}, new String [] {}, 
										UserMessageUtil.LOGIN_PROMPT_SUBJECT, 
										UserMessageUtil.getLoginPromptHtmlContent(acnt, crm, APPLICAION_CONTEXT_PATH));
							} catch (Exception e) {
								e.printStackTrace();
							}

		    			if(crm != null) {
								try {
									emailService.sendImoHtmlMail(new String [] {crm.getEmail()}, new String [] {"Julie-Ann.Evans@i-med.com.au"},
											UserMessageUtil.getLoginPromptCrmSubject(acnt),
											UserMessageUtil.getNotLoginPromptBody(acnt, crm));
								} catch (Exception e) {
									e.printStackTrace();
								}
		    			}
		    		}
		    		else
		    		{
    					String [] testEmails = new String [] {"Hidehiro.Uehara@i-med.com.au"};
		    			try {
								emailService.sendImoHtmlMail(testEmails, new String [] {}, 
										UserMessageUtil.LOGIN_PROMPT_SUBJECT, 
										UserMessageUtil.getLoginPromptHtmlContent(acnt, crm, APPLICAION_CONTEXT_PATH));
							} catch (Exception e) {
								e.printStackTrace();
							}
		    			if(crm != null) {
	    					try {
									emailService.sendImoHtmlMail(testEmails, new String [] {"Hidehiro.Uehara@i-med.com.au"},
										UserMessageUtil.getLoginPromptCrmSubject(acnt),
										UserMessageUtil.getNotLoginPromptBody(acnt, crm));
								} catch (Exception e) {
									e.printStackTrace();
								}
		    			}
		    		}
		    	}
		    }
		    
		    /**
		     * T & C 3 weekdays
		     */
		    if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
		    {
		    	logger.info("Skipping T&C reminder for weekend.");
		    } else {
		    	int daysFrom = -4;
		    	int daysTo = -3;
		    	switch(dayOfWeek) {
		    	case Calendar.MONDAY:
		    	case Calendar.TUESDAY:
		    	case Calendar.WEDNESDAY:
		    		daysFrom = -6;
			    	daysTo = -5;
		    		break;
		    	case Calendar.THURSDAY:
		    		daysFrom = -6;
			    	daysTo = -3;
		    		break;
		    	default:
		    		break;
		    	}
		    	
		    	cal.setTime(now);
			    cal.add(Calendar.DATE, daysTo);
			    final Date toDate = cal.getTime();
			    
		    	cal.setTime(now);
			    cal.add(Calendar.DATE, daysFrom);
			    final Date fromDate = cal.getTime();
			    
			    List<String> uidList = auditRepository.getDistinctLoginUsernamesBetween(fromDate, toDate);
			    for(String uid : uidList) {
			    	List<UserPreferencesEntity> plist = preferencesRepository.findByUsername(uid);
			    	if(plist.size() == 0 || !"hide".equals(plist.get(0).getHelp())) {
			    		CrmProfileEntity crm = getCrm(getReferrerPostcode(uid));
			    		List<LdapUserDetails> dlist;
							try {
								dlist = accountService.findReferrerAccountsByUid(uid);
							} catch (Exception e1) {
								dlist = new ArrayList<>(0);
								e1.printStackTrace();
							}
			    		logger.info("Sending referrer : " + dlist + ", crm = " + crm);
			    		if(dlist.size() > 0) {
			    			LdapUserDetails doctorDetails = dlist.get(0);
				    		if("prod".equals(ACTIVE_PROFILE)) {
				    			try {
										emailService.sendImoHtmlMail(new String [] {doctorDetails.getEmail()}, new String [] {},
												UserMessageUtil.TANDC_PROMPT_SUBJECT, 
												UserMessageUtil.getTandcPromptHtmlContent(doctorDetails, crm, APPLICAION_CONTEXT_PATH));
									} catch (Exception e) {
										e.printStackTrace();
									}
		    					if(crm != null) {
										try {
											emailService.sendImoHtmlMail(new String [] {crm.getEmail()}, new String [] {"Julie-Ann.Evans@i-med.com.au"}, 
													UserMessageUtil.getTandcPromptCrmSubject(doctorDetails),
													UserMessageUtil.getTandcPromptBody(doctorDetails, crm));
										} catch (Exception e) {
											e.printStackTrace();
										}
		    					}
		    				}
		    				else
		    				{
		    					//String [] testEmails = new String [] {"Hidehiro.Uehara@i-med.com.au", "Martin.Cox@i-med.com.au", "Julie-Ann.Evans@i-med.com.au"};
		    					try {
										emailService.sendImoHtmlMail(new String [] {"Hidehiro.Uehara@i-med.com.au"}, new String [] {},
												UserMessageUtil.TANDC_PROMPT_SUBJECT, 
												UserMessageUtil.getTandcPromptHtmlContent(doctorDetails, crm, APPLICAION_CONTEXT_PATH));
									} catch (Exception e) {
										e.printStackTrace();
									}
		    					if(crm != null) {
	    							try {
											emailService.sendImoHtmlMail(new String [] {"Hidehiro.Uehara@i-med.com.au"}, new String [] {}, 
												UserMessageUtil.getTandcPromptCrmSubject(doctorDetails),
												UserMessageUtil.getTandcPromptBody(doctorDetails, crm));
										} catch (Exception e) {
											e.printStackTrace();
										}
		    					}
		    				}
			    		}
			    	}
			    }
		    }
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private String getReferrerPostcode(final String uid) {
		String postcode = null;
		Map<String, String> internalParams = new HashMap<String, String>(1);
		internalParams.put(GetReferrerService.PARAM_CURRENT_USER_NAME, uid);
		ResponseEntity<Referrer> entity = getReferrerService.doRestGet(uid, internalParams, Referrer.class);
		if(HttpStatus.OK.equals(entity.getStatusCode())) {
			Practice [] practices = entity.getBody().getPractices();
			if(practices.length > 0 && practices[0].getAddress() != null) {
				postcode = practices[0].getAddress().getPostcode();
			}
		}
		logger.info("getReferrerPostcode() postcode = " + postcode); 
		return postcode;
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
		logger.info("Found crm for postcode {} ? {}", postcode, profile);
		return profile;
	}
}
