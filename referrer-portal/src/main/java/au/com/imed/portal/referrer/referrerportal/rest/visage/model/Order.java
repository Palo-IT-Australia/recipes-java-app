package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import au.com.imed.portal.referrer.referrerportal.rest.consts.OrderStatusConst;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Order {
  private boolean accessible;
  private String accessionNumber;
  private String uri;
  private String facility;
  private String procedureDescription;
  private String highestStatus;
  private String dateOfService;
  private Patient patient;
  private Procedure [] procedures;
  private Report report;
  private Referrer referrer;
  
  public boolean isAccessible() {
    return accessible;
  }
  public void setAccessible(boolean accessible) {
    this.accessible = accessible;
  }
  public String getAccessionNumber() {
    return accessionNumber;
  }
  public void setAccessionNumber(String accessionNumber) {
    this.accessionNumber = accessionNumber;
  }
  public String getUri() {
    return uri;
  }
  public void setUri(String uri) {
    this.uri = uri;
  }
  public String getFacility() {
    return facility;
  }
  public void setFacility(String facility) {
    this.facility = facility;
  }
  @JsonProperty("description")
  public String getProcedureDescription() {
    return procedureDescription;
  }
  @JsonProperty("procedureDescription")
  public void setProcedureDescription(String procedureDescription) {
    this.procedureDescription = procedureDescription;
  }
  @JsonIgnore
  public String getHighestStatus() {
    return highestStatus;
  }
  @JsonProperty("status")
  public String getStatus() {
    String sts = OrderStatusConst.GROUP_STATUS_MAP.get(this.highestStatus);
    return sts != null ? sts : "";
  }
  @JsonProperty("highestStatus")
  public void setHighestStatus(String highestStatus) {
    this.highestStatus = highestStatus;
  }
  @JsonProperty("date")
  public String getDateOfService() {
    return dateOfService;
  }
  @JsonProperty("dateOfService")
  public void setDateOfService(String dateOfService) {
    this.dateOfService = dateOfService;
  }
  
  public Patient getPatient() {
    return patient;
  }
  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public Referrer getReferrer() {
    return referrer;
  }
  public void setReferrer(Referrer referrer) {
    this.referrer = referrer;
  }
  public Procedure[] getProcedures() {
    return procedures;
  }
  public void setProcedures(Procedure[] procedures) {
    this.procedures = procedures;
  }

  @JsonProperty("reportUri")
  public Report getReport() {
    return report;
  }
  @JsonProperty("report")
  public void setReport(Report report) {
    this.report = report;
  }
  
  @JsonIgnoreProperties(ignoreUnknown=true)
  static class Referrer {
    private String fullName;

    @JsonProperty("name")
    public String getFullName() {
      return fullName;
    }

    @JsonProperty("fullName")
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    
  }
  
  @JsonIgnoreProperties(ignoreUnknown=true)
  static class Report {
    private String uri;

    @JsonValue
    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }
    
  }
  
//  @JsonIgnoreProperties(ignoreUnknown=true)
//  static class Procedure {
//    private String description;
//    private String modality;
//    private String procedureId;
//    public String getDescription() {
//      return description;
//    }
//    public void setDescription(String description) {
//      this.description = description;
//    }
//    public String getModality() {
//      return modality;
//    }
//    public void setModality(String modality) {
//      this.modality = modality;
//    }
//    public String getProcedureId() {
//      return procedureId;
//    }
//    public void setProcedureId(String procedureId) {
//      this.procedureId = procedureId;
//    }
//    
//  }
  
  @JsonIgnoreProperties(ignoreUnknown=true)
  public static class Patient {
    private String fullName;
    private String patientId;
    private String uri;
    private String dateOfBirth;

    @JsonProperty("name")
    public String getFullName() {
      return fullName;
    }
    @JsonProperty("fullName")
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    @JsonProperty("id")
    public String getPatientId() {
      return patientId;
    }
    @JsonProperty("patientId")
    public void setPatientId(String patientId) {
      this.patientId = patientId;
    }
    public String getUri() {
      return uri;
    }
    public void setUri(String uri) {
      this.uri = uri;
    }
    @JsonProperty("dob")
    public String getDateOfBirth() {
      return dateOfBirth;
    }
    @JsonProperty("dateOfBirth")
    public void setDateOfBirth(String dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
    }
  }
}
