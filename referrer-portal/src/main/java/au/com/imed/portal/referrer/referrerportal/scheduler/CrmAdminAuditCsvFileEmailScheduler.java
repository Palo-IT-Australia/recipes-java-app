package au.com.imed.portal.referrer.referrerportal.scheduler;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.common.util.StringConversionUtil;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmAdminAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmAdminAuditJpaRepository;

@Component
public class CrmAdminAuditCsvFileEmailScheduler {
	private Logger logger = LoggerFactory.getLogger(CrmAdminAuditCsvFileEmailScheduler.class);
	
	@Autowired 
	private CrmAdminAuditJpaRepository repository;
	
	@Value("${imed.scheduler.server.name}")
	private String SCHEDULER_SERVER_NAME;

	@Autowired
	private ReferrerMailService mailService;
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	@Scheduled(cron = "0 0 4 ? * MON")
	public void scheduleCrmAdminWeeklyCsvFileEmail() {
		Map<String, File> fileMap = new HashMap<>(1);
		try {
			if(SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) { 
				logger.info("scheduleCrmAdminWeeklyCsvFileEmail() starting...");
				Date now = new Date();
				Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Date from = calendar.getTime();
        
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        Date to = calendar.getTime();
        
				File tempFile = File.createTempFile("crmadmin-audit-", "-csv");
		    PrintWriter printWriter = new PrintWriter(tempFile);
		    printWriter.println("Time,Command,CRM,Referrer,Parameter,AccountType,FirstName,LastName,Mobile,Phone,Email,Ahpra");
		    List<CrmAdminAuditEntity> list = repository.findByAuditAtBetween(from, to);
		    for(CrmAdminAuditEntity entity : list) {
		      printWriter.print(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
		      		entity.getAuditAt().toString(),
		      		StringConversionUtil.nonQuote(entity.getCommand()),
		      		StringConversionUtil.nonQuote(entity.getCrm()),
      				StringConversionUtil.nonQuote(entity.getReferrer()),
      				StringConversionUtil.nonQuote(entity.getParameter()),
      				StringConversionUtil.nonQuote(entity.getAccountType()),
      				StringConversionUtil.nonQuote(entity.getFirstName()),
      				StringConversionUtil.nonQuote(entity.getLastName()),
      				StringConversionUtil.nonQuote(entity.getMobile()),
      				StringConversionUtil.nonQuote(entity.getPhone()),
      				StringConversionUtil.nonQuote(entity.getEmail()),
      				StringConversionUtil.nonQuote(entity.getAhpra())
		      ));
		    }
		    printWriter.close();
				
				fileMap.put("audit.csv", tempFile);
				logger.info("fileMap : " + fileMap);
				
				if("prod".equals(ACTIVE_PROFILE)) {
					mailService.sendWithFileMap(new String [] {"Hidehiro.Uehara@i-med.com.au"}, 
							"IMED Online 2.0 CRM Admin Audit", "Please find attached csv files", fileMap);
				} else {
					mailService.sendWithFileMap(new String[] {"Hidehiro.Uehara@i-med.com.au"}, 
							"IMED Online 2.0 CRM Admin Audit", "Please find attached csv files", fileMap);
				}
				logger.info("scheduleCrmAdminWeeklyCsvFileEmail() task ending... {} {} ", from, to);
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
