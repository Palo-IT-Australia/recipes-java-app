package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;

@Service
public class GetOrderService extends AVisageRestClientService<Map<String, String>, OrderDetails> {

	@Override
	protected String getCommandPath(Map<String, String> requestParams) {
		return requestParams.get("orderUri");
	}

	@Override
	protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
		Set<String> keys = requestParams.keySet();
		for (String key : keys) {
			builder.queryParam(key, requestParams.get(key));
		}
	}

}
