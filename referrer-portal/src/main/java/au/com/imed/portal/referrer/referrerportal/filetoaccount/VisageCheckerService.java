package au.com.imed.portal.referrer.referrerportal.filetoaccount;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer.Practice;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;

@Service
public class VisageCheckerService {
	@Autowired
	private GetReferrerService referrerService;
	
	public boolean isUsernameTaken(final String username) {
		Map<String, String> ps = new HashMap<>(1);
		ps.put(GetReferrerService.PARAM_CURRENT_USER_NAME, username);
		return isReferrerTaken(ps);
	}
	
	public boolean isAhpraTaken(final String ahpra) {
		Map<String, String> ps = new HashMap<>(1);
		ps.put(GetReferrerService.PARAM_AHPRA_NUMBER, ahpra);
		return isReferrerTaken(ps);
	}
	
	public boolean isProviderNumberTaken(final String provider) {
		Map<String, String> ps = new HashMap<>(1);
		ps.put(GetReferrerService.PARAM_PROVIDER_NUMBER, provider);
		return isReferrerTaken(ps);
	}

	private boolean isReferrerTaken(final Map<String, String> pmap) {
		boolean taken = false;
		ResponseEntity<Referrer> entity = referrerService.doRestGet(PortalConstant.REP_VISAGE_USER, pmap, Referrer.class);
		if(HttpStatus.OK.equals(entity.getStatusCode())) {
			taken = entity.getBody().getUri().length() > 0;
		}
		return taken;
	}
	
	public boolean isAccountWithProviders(final String username, final List<String> providerNumbers) {
		boolean isValid = false;
		if(!StringUtil.isBlank(username) && providerNumbers.size() > 0) {
			Map<String, String> ps = new HashMap<>(1);
			ps.put(GetReferrerService.PARAM_CURRENT_USER_NAME, username);
			ResponseEntity<Referrer> entity = referrerService.doRestGet(PortalConstant.REP_VISAGE_USER, ps, Referrer.class);
			if(HttpStatus.OK.equals(entity.getStatusCode())) {
				if(entity.getBody().getUri().length() > 0) {
					Practice [] practices = entity.getBody().getPractices();
					if(practices != null && practices.length >= providerNumbers.size()) {
						isValid = Arrays.asList(practices).stream().map(p -> p.getProviderNumber()).collect(Collectors.toList()).containsAll(providerNumbers);
					}
				}
			}
		}
		return isValid;
	}
}
