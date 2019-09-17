package au.com.imed.portal.referrer.referrerportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountPatientLdap {
  private String firstName;
  private String lastName;
  private String dob;
  private String dobAus;
  private String postcode;
  private String medicare;
  private String mobile;
  private String email;
  
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getDob() {
    return dob;
  }
  public void setDob(String dob) {
    this.dob = dob;
  }
  public String getDobAus() {
    return dobAus;
  }
  public void setDobAus(String dobAus) {
    this.dobAus = dobAus;
  }
  public String getPostcode() {
    return postcode;
  }
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }
  public String getMedicare() {
    return medicare;
  }
  public void setMedicare(String medicare) {
    this.medicare = medicare;
  }
  public String getMobile() {
    return mobile;
  }
  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  
  
}
