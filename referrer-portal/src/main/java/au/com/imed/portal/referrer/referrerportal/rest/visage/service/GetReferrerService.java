package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;


@Service
public class GetReferrerService extends AVisageRestClientService<Map<String, String>, Referrer> {
  public static final String PARAM_CURRENT_USER_NAME = "currentUserName";
  public static final String PARAM_PROVIDER_NUMBER = "providerNumber";
  public static final String PARAM_AHPRA_NUMBER = "ahpraNumber";

  @Override
  protected String getCommandPath(Map<String, String> requestParams) {
  	String path;
  	if(requestParams.containsKey(PARAM_PROVIDER_NUMBER)) 
  	{
  		path = "providerNumber/" + requestParams.get(PARAM_PROVIDER_NUMBER);
  	}
  	else if(requestParams.containsKey(PARAM_AHPRA_NUMBER)) 
  	{
  		path = "ahpraNumber/" + requestParams.get(PARAM_AHPRA_NUMBER);
  	}
  	else
  	{
  		path = requestParams.get(PARAM_CURRENT_USER_NAME);
  	}
    return "/referrer/" + path;
  }

  @Override
  protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
    // NOP
  }


}
