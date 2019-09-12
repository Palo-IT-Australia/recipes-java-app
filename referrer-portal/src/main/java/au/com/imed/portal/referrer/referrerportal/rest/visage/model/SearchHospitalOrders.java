package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchHospitalOrders {
  private List<HospitalOrderSummary> orders;

  public List<HospitalOrderSummary> getOrders() {
    return orders;
  }

  public void setOrders(List<HospitalOrderSummary> orders) {
    this.orders = orders;
  }

}
