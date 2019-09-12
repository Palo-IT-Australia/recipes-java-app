package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service("ViewHtmlReportService")
public class ReportService extends AVisageRestClientService<Map<String, String>, byte[]> {

  @Override
  protected String getCommandPath(Map<String, String> requestParams) {
    return requestParams.get("reportUri"); 
  }

  @Override
  protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
    if(requestParams != null && requestParams.size() > 0) {
      Set<String> keys = requestParams.keySet();
      for(String key : keys) {
        builder.queryParam(key, requestParams.get(key));
      }
    }
  }


}
