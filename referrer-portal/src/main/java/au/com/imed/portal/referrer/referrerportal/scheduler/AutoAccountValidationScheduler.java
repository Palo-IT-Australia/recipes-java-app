package au.com.imed.portal.referrer.referrerportal.scheduler;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_PASSED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_VALID;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerAutoValidationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerAutoValidationRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;

@Component
public class AutoAccountValidationScheduler {
	private Logger logger = LoggerFactory.getLogger(AutoAccountValidationScheduler.class);

	@Value("${imed.scheduler.auto.validation.server.name}")
	private String SCHEDULER_SERVER_NAME;
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	@Autowired
	private ReferrerAutoValidationRepository referrerAutoValidationRepository;
	
	@Autowired
	private ReferrerCreateAccountService createAccountService;
	
	//
	// all in midnight version
	//
//	@Scheduled(cron="1 0 0 * * *") // 0 AM 1 sec
//	public void scheduleDailyValidationTask() {
//		// TODO May separate to 4 servers by id?
//		try
//		{
//			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
//				logger.info("Starting validation scheduler task...");
//				LocalDate now = LocalDate.now();
//				logger.info("now is " + now);
//				SimpleDateFormat formatter =new SimpleDateFormat("dd/MMM/yyyy"); 
//				Date to = formatter.parse(String.format("%02d/%s/%04d", now.getDayOfMonth(), now.getMonth().toString(), now.getYear()));
//				LocalDate past = now.minusDays(1L);
//				Date from = formatter.parse(String.format("%02d/%s/%04d", past.getDayOfMonth(), past.getMonth().toString(), past.getYear()));
//				logger.info("Getting entries from Db from {} to {}", from, to);
//				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndApplyAtBetween(VALIDATION_STATUS_PASSED, from, to);
//				makeandsendscv(createAccountService.validateOnDb(list));
//				logger.info("Finished validation scheduler task...");
//			}
//		}catch(Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
	
	//
	// Separated 30 minutes and csv midnight versions
	//
	/**
	 * 1) validation
	 */
	@Scheduled(cron="0 0/30 * * * ?") // every 30 mins 30 * 60 * 1000
	public void scheduleOftenValidationTask() {
		// TODO May separate to 4 servers by id?
		try
		{
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("Starting often validation scheduler task...");
				Date now = new Date();
			  Calendar cal = Calendar.getInstance();
				 
				cal.setTime(now);
		    cal.add(Calendar.MINUTE, 30 * -1); // Should match cron
		    final Date from = cal.getTime();
		    
				logger.info("Getting entries from Db from {} to {}", from, now);
				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndApplyAtBetween(VALIDATION_STATUS_PASSED, from, now);
				createAccountService.validateOnDb(list);
				logger.info("Finished often validation scheduler task...");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 2) csv
	 */
	@Scheduled(cron="1 0 0 * * *") // 0 AM 1 sec
	public void scheduleDailyCsvEmailTask() {
		try
		{
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("Starting csv email scheduler task...");
				LocalDate now = LocalDate.now();
				logger.info("now is " + now);
				SimpleDateFormat formatter =new SimpleDateFormat("dd/MMM/yyyy"); 
				Date to = formatter.parse(String.format("%02d/%s/%04d", now.getDayOfMonth(), now.getMonth().toString(), now.getYear()));
				LocalDate past = now.minusDays(1L);
				Date from = formatter.parse(String.format("%02d/%s/%04d", past.getDayOfMonth(), past.getMonth().toString(), past.getYear()));
				logger.info("Getting entries from Db from {} to {}", from, to);
				// valid means created
				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndApplyAtBetween(VALIDATION_STATUS_VALID, from, to);
				createAccountService.makeAndSendCsvEmails(list);
				logger.info("Finished csv email scheduler task...");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Notify to crm and referrers supposing visage intelerad accounts created in last day
	 */
	@Scheduled(cron="0 0 6 * * *")  // 6 AM
	public void scheduleDailyNotifyTask() {
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
				logger.info("Starting notification task...");
				// one day delay notification
				LocalDate now = LocalDate.now().minusDays(1L);
				SimpleDateFormat formatter =new SimpleDateFormat("dd/MMM/yyyy"); 
				Date to = formatter.parse(String.format("%02d/%s/%04d", now.getDayOfMonth(), now.getMonth().toString(), now.getYear()));
				LocalDate past = now.minusDays(1L);
				Date from = formatter.parse(String.format("%02d/%s/%04d", past.getDayOfMonth(), past.getMonth().toString(), past.getYear()));
				logger.info("Getting entries from Db from {} to {}", from, to);
				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndAccountAtBetween(VALIDATION_STATUS_VALID, from, to);
				createAccountService.notifyNewAccounts(list);
				logger.info("Finishing notification task...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
