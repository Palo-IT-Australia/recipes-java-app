package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.SearchHospitalOrders;

@Service
public class SearchHospitalOrderSummaryService extends AVisageRestClientService<Map<String, String>, SearchHospitalOrders> {
  @Override
  protected String getCommandPath(Map<String, String> requestParams) {
    return "orders";
  }

  @Override
  protected void setParameters(UriComponentsBuilder builder, Map<String, String> requestParams) {
    if(requestParams != null && requestParams.size() > 0) {
      Set<String> keys = requestParams.keySet();
      for(String key : keys) {
        String val = requestParams.get(key);
        if("practices".equalsIgnoreCase(key) && val.length() > 0) {
          String [] pracs = val.split(",");
          for(String p : pracs) {
            System.out.println("SearchHospitalOrderSummaryService setParameters() setting practiceUri " + p);
            builder.queryParam("practiceUri", p);
          }
        }
        else if("hospitalUri".equalsIgnoreCase(key) && val.length() > 0) {
          String [] hosps = val.split(",");
          for(String h : hosps) {
            System.out.println("SearchHospitalOrderSummaryService setParameters() setting hospitalUri " + h);
            builder.queryParam("hospitalUri", h);
          }
        }
        else if("statuses".equalsIgnoreCase(key) && val.length() > 0) {
          String [] stses = val.split(",");
          for(String s : stses) {
            System.out.println("SearchHospitalOrderSummaryService setParameters() setting status " + s);
            builder.queryParam("status", s);
          }
        }
        else {
          if(val.length() > 0) {
            System.out.println("SearchHospitalOrderSummaryService setParameters() setting " + key + " " + val);
            builder.queryParam(key, val);
          }
        }
      }
    }
    else
    {
      builder.queryParam("searchType", "all");
    }
  }

}