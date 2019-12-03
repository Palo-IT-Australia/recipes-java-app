package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.FindPatient;

@Service
public class FindPatientsService extends AVisageRestClientService<Map<String, String>, List<FindPatient>> {

	@Override
	protected String getCommandPath(Map<String, String> requestParams) {
		return "patients";
	}

	@Override
	protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
		if(requestParams != null && requestParams.size() > 0) {
      Set<String> keys = requestParams.keySet();
      for(String key : keys) {
        String val = requestParams.get(key);
        if(val.length() > 0) {
          System.out.println("FindPatientsService setParameters() setting " + key + " " + val);
          builder.queryParam(key, val);
        }
      }
		}
	}
}
