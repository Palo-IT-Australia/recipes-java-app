package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Patient;


@Service
public class GetPatientService extends AVisageRestClientService<Map<String, String>, Patient> {
  
  @Override
  protected String getCommandPath(Map<String, String> requestParams) {
    return requestParams.get("patientUri");
  }

  @Override
  protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
    //nop
  }
}
