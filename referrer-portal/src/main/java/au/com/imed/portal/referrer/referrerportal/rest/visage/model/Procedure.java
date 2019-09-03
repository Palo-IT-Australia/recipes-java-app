package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Procedure {
  private String description;
  private String modality;
  private String procedureId;
  private String accessionNumber;
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getModality() {
    return modality;
  }
  public void setModality(String modality) {
    this.modality = modality;
  }
  public String getProcedureId() {
    return procedureId;
  }
  public void setProcedureId(String procedureId) {
    this.procedureId = procedureId;
  }
  public String getAccessionNumber() {
    return accessionNumber;
  }
  public void setAccessionNumber(String accessionNumber) {
    this.accessionNumber = accessionNumber;
  }
  
}
