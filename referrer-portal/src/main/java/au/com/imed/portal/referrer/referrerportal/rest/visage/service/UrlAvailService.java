package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class UrlAvailService {

	private static final String CHECKER_METHOD = "/Login.aspx";
	private static List<String> availableUrls = Collections.synchronizedList(new ArrayList<String>());
	private static final RestTemplate restTemplate = createRestTemplate();
	private static long lastCheckedAt = -1;
	private static final long INTERVAL = 60 * 60 * 1000; // 1 hour unless forced

	@Value("#{'${imed.vuemotion.url}'.split(',')}")
	private List<String> vueMotionUrlList;

	public String getUrl(final boolean forceCheck) {
		synchronized (availableUrls) {
			long current = System.currentTimeMillis();
			System.out.println(
					"forceCheck ? " + forceCheck + ", current " + current + ", lastCheckedAt " + lastCheckedAt);
			if (vueMotionUrlList.size() == 1) {
				// No balancer required, always one without checking its availability
				if (availableUrls.size() == 0) {
					availableUrls.add(vueMotionUrlList.get(0));
					lastCheckedAt = current;
				}
			} else if (forceCheck || lastCheckedAt < 0 || (current - lastCheckedAt > INTERVAL)) {
				availableUrls.clear();
				try {
					ResponseEntity<String> entity = restTemplate.getForEntity(vueMotionUrlList.get(0) + CHECKER_METHOD,
							String.class);
					System.out.println(vueMotionUrlList.get(0) + CHECKER_METHOD + " - " + entity.getStatusCodeValue());
					if (entity.getStatusCode() == HttpStatus.OK) {
						availableUrls.add(vueMotionUrlList.get(0));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					ResponseEntity<String> entity = restTemplate.getForEntity(vueMotionUrlList.get(1) + CHECKER_METHOD,
							String.class);
					System.out.println(vueMotionUrlList.get(1) + CHECKER_METHOD + " - " + entity.getStatusCodeValue());
					if (entity.getStatusCode() == HttpStatus.OK) {
						availableUrls.add(vueMotionUrlList.get(1));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("Checked available urls " + availableUrls.size() + " " + availableUrls);
				lastCheckedAt = current;
			}

			int size = availableUrls.size();
			System.out.println("getUrl() Available urls " + size + " " + availableUrls);
			String url = null;
			if (size > 1) {
				url = Math.random() < 0.5 ? availableUrls.get(0) : availableUrls.get(1);
			} else if (size == 1) {
				url = availableUrls.get(0);
			} else {
				System.out.println("No server is available, falling back to " + vueMotionUrlList.get(1));
				url = vueMotionUrlList.get(0);
			}
			return url;
		}
	}

	private static RestTemplate createRestTemplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(3000);
		factory.setConnectTimeout(3000);
		return new RestTemplate(factory);
	}

}
