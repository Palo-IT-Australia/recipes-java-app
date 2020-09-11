package au.com.imed.portal.referrer.referrerportal.scheduler;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_MSG_ACCOUNT_CREATED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_MSG_MANUAL_CREATEION;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_NOTIFIED;
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
			createAccountService.validateOnDb(filterByServerNumber(list));
			logger.info("Finished short period validation scheduler task...");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 2) csv files exchange with visage
	 */
	@Scheduled(cron="0 0 * * * *") // every hours
	public void scheduleProducingVisageCsvTask() {
		try
		{
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("Starting 24/7 visage csv exchange scheduler task...");
				
				// To Visage
				Date now = new Date();
				Calendar cal = Calendar.getInstance();

				cal.setTime(now);
				cal.add(Calendar.HOUR, -1); // Should match cron
				final Date from = cal.getTime();
				logger.info("Getting entries from Db from {} to {}", from, now);
				
				if(from != null) {
					// created at last hour and valid means portal account was created ready for Visage account creation
					List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndAccountAtBetween(VALIDATION_STATUS_VALID, from, now);
					createAccountService.makeAndShareVisageCsvFile(list);
				}
				
				// From Visage
				createAccountService.notifyNewAccountByVisageCsvFiles();
				logger.info("Finished 24/7 visage csv exchange scheduler task...");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 3) Notify by welcome and CRM email ifMANUAL Visage account creation is completed
	 */
	@Scheduled(cron="0 10/15 * ? * *")  // every 15 mins starting 10 mins past hours
	public void scheduleVisageAccountWelcomeNotifyTask() {
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
				logger.info("Starting visage account completion checks and notification task...");
				// Obtain all waiting manually created accounts notification valid entries
				List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByValidationStatusAndValidationMsg(VALIDATION_STATUS_NOTIFIED, VALIDATION_MSG_MANUAL_CREATEION);
				// Check if visage accounts created
				List<ReferrerAutoValidationEntity> createdList = createAccountService.filterToVisageAccoutCompleted(list);
				createAccountService.notifyNewAccounts(createdList);
				logger.info("Finishing visage account completion checks and notification task...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 2) csv nightly
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
	 * 3) Notify to crm and referrers supposing visage intelerad accounts created in last day
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
	
	private final static int HOUR_CSV_FROM = 8;
	private final static int HOUR_CSV_TO = 18;	
	/**
	 * 
	 * @param now current date
	 * @return from date
	 */
	private Date calculateCsvFromDate(final Date now) {
		logger.info("calculateCsvFromDate() now given = " + now);
		Date from = null;
    
		Calendar cal = Calendar.getInstance();
    cal.setTime(now);
    final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    final int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
    
    if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY)
    {
    	int hoursFrom = -1; // default one hour ago

    	if(hourOfDay == HOUR_CSV_FROM) 
    	{
    		hoursFrom = (HOUR_CSV_TO - HOUR_CSV_FROM) - 24; // yesterday TO date	
	    	if(dayOfWeek == Calendar.MONDAY) {
	    		hoursFrom = hoursFrom - (24 * 2); // weekend
	    	}
    	}
    	
	    cal.setTime(now);
	    cal.add(Calendar.HOUR, hoursFrom);
	    from = cal.getTime();
    }
    else
    {
    	logger.info("Skip weekend....");  
    }
    
    logger.info("calculateCsvFromDate() From date (null for weekend) = " + from);
    return from;
	}
	
}
