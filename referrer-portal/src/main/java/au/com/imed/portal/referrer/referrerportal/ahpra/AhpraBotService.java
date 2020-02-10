package au.com.imed.portal.referrer.referrerportal.ahpra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import au.com.imed.portal.referrer.referrerportal.common.util.IgnoreCertFactoryUtil;

@Service
public class AhpraBotService {
	private Logger logger = LoggerFactory.getLogger(AhpraBotService.class);
			
  @Value("${imed.ahpra.bot.local.url}")
  private String LOCALBOTURL;
  
	/**
	 * With delay to avoid bot blocker
	 * @param ahpra
	 * @return
	 */
	public AhpraDetails [] findByNumber(final String ahpra) {
		return new RestTemplate().getForObject("https://connections.i-medonline.com.au/datascrapper/ahpraDetail?ahpraNumber=" + ahpra, AhpraDetails[].class);
	}
	
	/**
	 * No delay, error code means bot blocker
	 * @param ahpra
	 * @return 
	 */
	public ResponseEntity<AhpraDetails []> findByNumberImmediate(final String ahpra) {
		ResponseEntity<AhpraDetails []> entity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		try {
			HttpComponentsClientHttpRequestFactory factory = IgnoreCertFactoryUtil.createFactory();
			entity = new RestTemplate(factory).getForEntity(LOCALBOTURL + "?ahpraNumber=" + ahpra, AhpraDetails [].class);
		} catch (Exception e) {
			logger.info("404 means bot blocker..." + e.getMessage());
		}
		return entity;
	}
	
	/**
	 * Mixed version with retry
	 */
	public AhpraDetails [] findByNumberRetry(final String ahpra) {
		logger.info("findByNumberRetry() finding AHPRA : " + ahpra);
		AhpraDetails [] ahpras;
		ResponseEntity<AhpraDetails []> entity = this.findByNumberImmediate(ahpra);
		if(HttpStatus.OK.equals(entity.getStatusCode())) {
			// No bot blocker
			ahpras = entity.getBody();
		} else {
			// Retry with latency mode taking 1 min or so
			logger.info("findByNumberRetry() AHPRA scrapper retry with latency...");
			ahpras = this.findByNumber(ahpra);
			if(ahpras == null || ahpras.length == 0) {
				logger.info("findByNumberRetry() 1st retly returned [], doing 2nd retry with delay...");
				ahpras = this.findByNumber(ahpra);
			}
		}
		logger.info("findByNumberRetry() trials finished " + ahpra + " : " + ahpras);
		if(ahpras != null && ahpras.length > 0) {
			logger.info("findByNumberRetry() result : length = " + ahpras.length + ", name = " + ahpras[0].getName());
		} else {
			logger.info("findByNumberRetry() result : AHPRA not found in ahpra.gov.au for " + ahpra);
		}
		return ahpras;
	}
}
