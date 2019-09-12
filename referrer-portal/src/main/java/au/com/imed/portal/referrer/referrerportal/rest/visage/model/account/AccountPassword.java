package au.com.imed.portal.referrer.referrerportal.rest.visage.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountPassword {
  private String old_password;
  private String new_password;
  
  public String getOld_password() {
    return old_password;
  }
  public void setOld_password(String old_password) {
    this.old_password = old_password;
  }
  public String getNew_password() {
    return new_password;
  }
  public void setNew_password(String new_password) {
    this.new_password = new_password;
  }
}
