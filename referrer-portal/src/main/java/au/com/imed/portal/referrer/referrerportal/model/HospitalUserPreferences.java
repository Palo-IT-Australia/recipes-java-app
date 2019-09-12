package au.com.imed.portal.referrer.referrerportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class HospitalUserPreferences {
  private String hospitalUri;
  private String hospitalStatus;
  
  public String getHospitalUri() {
    return hospitalUri;
  }
  public void setHospitalUri(String hospitalUri) {
    this.hospitalUri = hospitalUri;
  }
  public String getHospitalStatus() {
    return hospitalStatus;
  }
  public void setHospitalStatus(String hospitalStatus) {
    this.hospitalStatus = hospitalStatus;
  }
  
}
