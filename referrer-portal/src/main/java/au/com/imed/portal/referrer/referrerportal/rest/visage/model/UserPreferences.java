package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UserPreferences {
  private String help;
  private String autoimg;
  private String notify;

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public String getAutoimg() {
    return autoimg;
  }

  public void setAutoimg(String autoimg) {
    this.autoimg = autoimg;
  }

  public String getNotify() {
    return notify;
  }

  public void setNotify(String notify) {
    this.notify = notify;
  }
  
}
