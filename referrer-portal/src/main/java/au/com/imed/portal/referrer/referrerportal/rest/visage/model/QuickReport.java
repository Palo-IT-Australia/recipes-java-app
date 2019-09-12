package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class QuickReport {
  private OrderDetails order;
  private String report;
  private Patient patient;
  private String viewUrl;
  private String orderUri;
  private boolean accessible;
  
  public String getOrderUri() {
    return orderUri;
  }
  public void setOrderUri(String orderUri) {
    this.orderUri = orderUri;
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
  public String getViewUrl() {
    return viewUrl;
  }
  public void setViewUrl(String viewUrl) {
    this.viewUrl = viewUrl;
  }
  public boolean isAccessible() {
    return accessible;
  }
  public void setAccessible(boolean accessible) {
    this.accessible = accessible;
  }
}
