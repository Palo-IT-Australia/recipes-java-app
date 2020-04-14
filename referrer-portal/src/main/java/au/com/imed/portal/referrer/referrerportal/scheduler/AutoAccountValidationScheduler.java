package au.com.imed.portal.referrer.referrerportal.scheduler;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_MSG_ACCOUNT_CREATED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_PASSED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_VALID;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
//		// May separate to 4 servers by id?
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
	
	/**
	 * Separate list into 4 servers.
	 * @param original
	 * @return assigned list for this server based on row id and server number
	 */
	private List<ReferrerAutoValidationEntity> filterByServerNumber(List<ReferrerAutoValidationEntity> original) {
		logger.info("filterByServerNumber() original list size " + original.size());
		List<ReferrerAutoValidationEntity> allocs = new ArrayList<ReferrerAutoValidationEntity>();
		try {
			int remains = -1;
			String svname = InetAddress.getLocalHost().getHostName();
			if (svname.contains("05")) {
				remains = 0;
			} else if (svname.contains("06")) {
				remains = 1;
			} else if (svname.contains("07")) {
				remains = 2;
			} else if (svname.contains("08")) {
				remains = 3;
			}
			logger.info("filterByServerNumber() Allocating list for server " + svname + " with id % 4 == " + remains);
			if(remains > -1) {
				final int rem = remains;
				allocs = original.stream().filter(r -> r.getId() % 4 == rem).collect(Collectors.toList());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.info("filterByServerNumber() allocated list size " + allocs.size());
		return allocs;
	}
	
	//
	// Separated 30 minutes and csv midnight versions
	//
	/**
	 * 1) validation
	 */
	@Scheduled(cron="0 0/30 * * * ?") // every 30 mins 0 and 30 minutes
	public void scheduleOftenValidationTask() {
		try
		{
			logger.info("Starting short period validation scheduler task...");
			Date now = new Date();
			Calendar cal = Calendar.getInstance();

			cal.setTime(now);
			cal.add(Calendar.MINUTE, 30 * -1); // Should match cron
			final Date from = cal.getTime();

			logger.info("Getting entries from Db from {} to {}", from, now);
			List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndApplyAtBetween(VALIDATION_STATUS_PASSED, from, now);
			List<ReferrerAutoValidationEntity> createdList = createAccountService.validateOnDb(filterByServerNumber(list));
			logger.info("Created account list size = " + createdList.size());
			if(createdList.size() > 0) {
				createAccountService.makeAndSendCsvEmails(createdList);
			}
			logger.info("Finished short period validation scheduler task...");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Notify by welcome and CRM email
	 */
	@Scheduled(cron="0 10/15 * ? * *")  // every 15 mins starting 10 mins past hours
	public void schedulePeriodicNotifyTask() {
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
				logger.info("Starting periodic notification task...");
				// Obtain all waiting notification valid entries
				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndValidationMsg(VALIDATION_STATUS_VALID, VALIDATION_MSG_ACCOUNT_CREATED);
				// Check if visage accounts created
				List<ReferrerAutoValidationEntity> createdList = createAccountService.filterToVisageAccoutCreated(list);
				createAccountService.notifyNewAccounts(createdList);
				logger.info("Finishing periodic notification task...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 2) csv
	 */
//	@Scheduled(cron="1 0 0 * * *") // 0 AM 1 second
//	public void scheduleDailyCsvEmailTask() {
//		try
//		{
//			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
//				logger.info("Starting csv email scheduler task...");
//				LocalDate now = LocalDate.now();
//				logger.info("now is " + now);
//				SimpleDateFormat formatter =new SimpleDateFormat("dd/MMM/yyyy"); 
//				Date to = formatter.parse(String.format("%02d/%s/%04d", now.getDayOfMonth(), now.getMonth().toString(), now.getYear()));
//				LocalDate past = now.minusDays(1L);
//				Date from = formatter.parse(String.format("%02d/%s/%04d", past.getDayOfMonth(), past.getMonth().toString(), past.getYear()));
//				logger.info("Getting entries from Db from {} to {}", from, to);
//				// valid means created
//				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndApplyAtBetween(VALIDATION_STATUS_VALID, from, to);
//				createAccountService.makeAndSendCsvEmails(list);
//				logger.info("Finished csv email scheduler task...");
//			}
//		}catch(Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * Notify to crm and referrers supposing visage intelerad accounts created in last day
	 */
//	@Scheduled(cron="0 0 6 * * *")  // 6 AM
//	public void scheduleDailyNotifyTask() {
//		try {
//			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
//				logger.info("Starting notification task...");
//				// one day delay notification
//				LocalDate now = LocalDate.now().minusDays(1L);
//				SimpleDateFormat formatter =new SimpleDateFormat("dd/MMM/yyyy"); 
//				Date to = formatter.parse(String.format("%02d/%s/%04d", now.getDayOfMonth(), now.getMonth().toString(), now.getYear()));
//				LocalDate past = now.minusDays(1L);
//				Date from = formatter.parse(String.format("%02d/%s/%04d", past.getDayOfMonth(), past.getMonth().toString(), past.getYear()));
//				logger.info("Getting entries from Db from {} to {}", from, to);
//				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndAccountAtBetween(VALIDATION_STATUS_VALID, from, to);
//				createAccountService.notifyNewAccounts(list);
//				logger.info("Finishing notification task...");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
