package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Visit {
  private List<Order> list;
  private OrderDetails order;
  private String report;
  private Patient patient;
  private String orderUri;
  
  public List<Order> getList() {
    return list;
  }
  public void setList(List<Order> list) {
    this.list = list;
  }
  public OrderDetails getOrder() {
    return order;
  }
  public void setOrder(OrderDetails order) {
    this.order = order;
  }
  public String getReport() {
    return report;
  }
  public void setReport(String report) {
    this.report = report;
  }
  public Patient getPatient() {
    return patient;
  }
  public void setPatient(Patient patient) {
    this.patient = patient;
  }
  public String getOrderUri() {
    return orderUri;
  }
  public void setOrderUri(String orderUri) {
    this.orderUri = orderUri;
  }
  
  
}
