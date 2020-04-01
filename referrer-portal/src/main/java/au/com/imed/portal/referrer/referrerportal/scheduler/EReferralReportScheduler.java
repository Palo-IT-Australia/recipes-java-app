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

	@Scheduled(cron = "0 47 14 * * ?")
	public void schedulEReferralReport() {
		try {
			logger.info("Started to send ereferral audit report to CRM");
			if (SCHEDULER_SERVER_NAME.equals(InetAddress.getLocalHost().getHostName())) {
				electronicReferralService.sendDailyEReferralToCrm();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
