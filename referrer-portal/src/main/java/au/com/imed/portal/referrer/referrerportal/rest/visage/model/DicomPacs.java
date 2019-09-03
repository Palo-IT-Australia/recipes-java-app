package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DicomPacs {
  private String studyInstanceUID;
  private String modalitiesInStudy;
  private String accessionNumber;
  private String studyDescription;
  
  public String getStudyInstanceUID() {
    return studyInstanceUID;
  }
  public void setStudyInstanceUID(String studyInstanceUID) {
    this.studyInstanceUID = studyInstanceUID;
  }
  public String getModalitiesInStudy() {
    return modalitiesInStudy;
  }
  public void setModalitiesInStudy(String modalitiesInStudy) {
    this.modalitiesInStudy = modalitiesInStudy;
  }
  public String getAccessionNumber() {
    return accessionNumber;
  }
  public void setAccessionNumber(String accessionNumber) {
    this.accessionNumber = accessionNumber;
  }
  public String getStudyDescription() {
    return studyDescription;
  }
  public void setStudyDescription(String studyDescription) {
    this.studyDescription = studyDescription;
  }
 
}
