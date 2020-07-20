package au.com.imed.portal.referrer.referrerportal.scheduler;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.csv.AuditCsvService;
import au.com.imed.portal.referrer.referrerportal.csv.PreferencesCsvService;
import au.com.imed.portal.referrer.referrerportal.csv.ProvidersCsvService;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.ldap.LdapCsvCreationService;

@Component
public class CsvFileEmailScheduler {
	private Logger logger = LoggerFactory.getLogger(CsvFileEmailScheduler.class);
	
	private static final String FILE_REFERRERS = "referrers.csv";
	private static final String FILE_PROVIDERS = "providers.csv";
	private static final String FILE_AUDIT = "audit.csv";
	private static final String FILE_PREFERENCES = "preferences.csv";
	
	@Autowired
	private LdapCsvCreationService ldapCsvCreationService;
	
	@Autowired
	private AuditCsvService auditCsvService;

	@Autowired
	private PreferencesCsvService preferencesCsvService;

	@Autowired
	private ProvidersCsvService providersCsvService;
	
	@Autowired
	private ReferrerMailService mailService;

	@Value("${imed.scheduler.server.name}")
	private String SCHEDULER_SERVER_NAME;
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	/**
	 * Monthly csv files
	 */
	@Scheduled(cron = "0 1 3 1 * ?")
	public void scheduleMonthlyCsvFileEmail() {
		Map<String, File> fileMap = new HashMap<>(2);
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("scheduleCsvFileEmail() starting...");
				File ldapFile = ldapCsvCreationService.createCsv(false);
				File provFile = providersCsvService.createCsv();
				
				fileMap.put(FILE_PROVIDERS, provFile);
				fileMap.put(FILE_REFERRERS, ldapFile);
				logger.info("fileMap : " + fileMap);
				
				if("prod".equals(ACTIVE_PROFILE)) {
					mailService.sendWithFileMap(new String [] {"Jeremy.Chan@i-med.com.au", "Chang.Chui@i-med.com.au"}, 
						"Referrer Csv files", "Please find attached csv files", fileMap);
				} else {
					mailService.sendWithFileMap(new String[] {"Hidehiro.Uehara@i-med.com.au"}, 
						"Referrer Csv files", "Please find attached csv files", fileMap);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			deleteTempFiles(fileMap);
		}
	}
	
	/**
	 * Weekly csv files
	 */
	@Scheduled(cron = "0 0 2 ? * MON")
	public void scheduleWeeklyCsvFileEmail() {
		Map<String, File> fileMap = new HashMap<>(3);
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("scheduleWeeklyCsvFileEmail() starting...");
				File refFile = ldapCsvCreationService.createCsv(true);
				File prefFile = preferencesCsvService.createCsv();
				File auditFile = auditCsvService.createCsv();
				
				fileMap.put(FILE_AUDIT, auditFile);
				fileMap.put(FILE_PREFERENCES, prefFile);
				fileMap.put(FILE_REFERRERS, refFile);
				logger.info("fileMap : " + fileMap);
				
				if("prod".equals(ACTIVE_PROFILE)) {
					mailService.sendWithFileMap(new String [] {"Alexandra.Arter@i-med.com.au", "Giles.Cox@i-med.com.au", "Julie-Ann.Evans@i-med.com.au", "tania.armstrong@i-med.com.au", "Georgia.McMeniman@i-med.com.au"}, 
							"IMED Online 2.0 Audit", "Please find attached csv files", fileMap);
				} else {
					mailService.sendWithFileMap(new String[] {"Hidehiro.Uehara@i-med.com.au"}, 
							"IMED Online 2.0 Audit", "Please find attached csv files", fileMap);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			deleteTempFiles(fileMap);
		}
	}
	
	private void deleteTempFiles(Map<String, File> fileMap) {
		if(fileMap != null) {
			for(String fname : fileMap.keySet()) {
				File file = fileMap.get(fname);
				if(file != null) {
					file.delete();
				}
			}
		}
	}
}
