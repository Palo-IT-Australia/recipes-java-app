package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ShareReport {
  private String accessionNumber;
  private String item;
  private String email;
  private String mobile;
  private String patientId;
  
  public String getAccessionNumber() {
    return accessionNumber;
  }
  public void setAccessionNumber(String accessionNumber) {
    this.accessionNumber = accessionNumber;
  }
  public String getItem() {
    return item;
  }
  public void setItem(String item) {
    this.item = item;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getMobile() {
    return mobile;
  }
  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  public String getPatientId() {
    return patientId;
  }
  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }
  
}
