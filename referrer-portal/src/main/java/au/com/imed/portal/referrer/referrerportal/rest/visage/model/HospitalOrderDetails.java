package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
  "priorityType": {
    "code": "20",
    "description": "In-Patient"
  },
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class HospitalOrderDetails {
  private PriorityType priorityType;
  
  public PriorityType getPriorityType() {
    return priorityType;
  }

  public void setPriorityType(PriorityType priorityType) {
    this.priorityType = priorityType;
  }

  public static class PriorityType {
    private String code;
    private String description;
    public String getCode() {
      return code;
    }
    public void setCode(String code) {
      this.code = code;
    }
    public String getDescription() {
      return description;
    }
    public void setDescription(String description) {
      this.description = description;
    }
  }
}
