package au.com.imed.portal.referrer.referrerportal.filetoaccount;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
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

	private boolean isReferrerTaken(final Map<String, String> pmap) {
		boolean taken = false;
		ResponseEntity<Referrer> entity = referrerService.doRestGet(PortalConstant.REP_VISAGE_USER, pmap, Referrer.class);
		if(HttpStatus.OK.equals(entity.getStatusCode())) {
			taken = entity.getBody().getName().length() > 0;
		}
		return taken;
	}
}
