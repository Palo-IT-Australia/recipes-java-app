package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.consts.OrderStatusConst;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.SearchOrders;

@Service
public class SearchOrdersService extends AVisageRestClientService<Map<String, String>, SearchOrders> {

	@Override
	protected String getCommandPath(Map<String, String> requestParams) {
		return "orders";
	}

	@Override
	protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
		if (requestParams != null && requestParams.size() > 0) {
			Set<String> keys = requestParams.keySet();
			for (String key : keys) {
				String val = requestParams.get(key);
				if ("practices".equalsIgnoreCase(key) && val.length() > 0) {
					String[] pracs = val.split(",");
					for (String p : pracs) {
						System.out.println("setParameters() setting practiceUri " + p);
						builder.queryParam("practiceUri", p);
					}
				} else if ("orderStatus".equalsIgnoreCase(key) && val.length() > 0) {
					String[] stses = val.split(",");
					for (String stskey : stses) {
						String[] stsary = OrderStatusConst.STATUS_GROUP_MAP.get(stskey);
						System.out.println(stskey + " : " + stsary);
						if (stsary != null) {
							for (String reqsts : stsary) {
								System.out.println("setParameters() setting status " + reqsts);
								builder.queryParam("status", reqsts);
							}
						}
					}
				} else {
					if (val.length() > 0) {
						System.out.println("setParameters() setting " + key + " " + val);
						builder.queryParam(key, val);
					}
				}
			}
		} else {
			builder.queryParam("searchType", "all");
		}
	}

}
