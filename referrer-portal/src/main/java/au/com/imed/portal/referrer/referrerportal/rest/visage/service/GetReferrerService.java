package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;


@Service
public class GetReferrerService extends AVisageRestClientService<Map<String, String>, Referrer> {
  public static final String PARAM_CURRENT_USER_NAME = "currentUserName";

  @Override
  protected String getCommandPath(Map<String, String> requestParams) {
    return "/referrer/" + requestParams.get(PARAM_CURRENT_USER_NAME);
  }

  @Override
  protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
    // NOP
  }


}
