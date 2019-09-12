package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.PatientOrder;


@Service
public class GetPatientOrdersService extends AVisageRestClientService<Map<String, String>, PatientOrder> {

  @Override
  protected String getCommandPath(Map<String, String> requestParams) {
    return requestParams.get("patientUri") + "/orders";
  }

  @Override
  protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
    builder.queryParam("searchType", "all");
  }

}
