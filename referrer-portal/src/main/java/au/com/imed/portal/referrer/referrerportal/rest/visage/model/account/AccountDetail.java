package au.com.imed.portal.referrer.referrerportal.rest.visage.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountDetail {
  private String name;
  private String email;
  private String mobile;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
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
  
  @Override
  public String toString() {
    return "AccountDetail [name=" + name + ", email=" + email + ", mobile=" + mobile + "]";
  }
}
