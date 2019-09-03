package au.com.imed.portal.referrer.referrerportal.model;

import java.io.Serializable;

public class DetailModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private String email;
  private String mobile;
  private String displayName;
    
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
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  @Override
  public String toString() {
    return "DetailModel [email=" + email + ", mobile=" + mobile
        + ", displayName=" + displayName + "]";
  }
}
