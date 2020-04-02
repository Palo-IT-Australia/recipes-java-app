package au.com.imed.portal.referrer.referrerportal.scheduler;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service.ElectronicReferralService;

@Component
public class EReferralReportScheduler {

	private Logger logger = LoggerFactory.getLogger(EReferralReportScheduler.class);

	@Autowired
	ElectronicReferralService electronicReferralService;

	@Value("${imed.scheduler.ereferral.server.name}")
	private String SCHEDULER_SERVER_NAME;

//	@Scheduled(cron = "0 30 7 * * ?")
	@Scheduled(cron = "0 19 15 * * ?")
	public void schedulEReferralAuditReport() {
		try {
			logger.info("Started to send ereferral audit report to Martin");
			if (SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
				electronicReferralService.sendDailyEReferralAuditReport();
			}
		} catch (Exception e) {
			logger.info("Exception occured while trying to send ereferral audit report to Martin");
			e.printStackTrace();
		}
	}
	
//	@Scheduled(cron = "0 0 8 * * MON-FRI")
	@Scheduled(cron = "0 1 16 * * MON-FRI")
	public void schedulEReferralCrmReport() {
		try {
			logger.info("Started to send ereferral report to CRM");
			if (SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
				electronicReferralService.sendWeekdayEReferralReportToCrm();
			}
		} catch (Exception e) {
			logger.info("Exception occured while trying to send ereferral report to CRM");
			e.printStackTrace();
		}
	}

}
