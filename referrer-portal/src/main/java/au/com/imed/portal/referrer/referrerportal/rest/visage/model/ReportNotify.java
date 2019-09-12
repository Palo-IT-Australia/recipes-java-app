package au.com.imed.portal.referrer.referrerportal.rest.visage.model;


public class ReportNotify {
  private String patientUri;
  private String orderUri;
  private String patientId;
  private String patientDob;
  private String studyDate;
  private String patientName;
  private String orderPriority;
  private String orderPriorityType;
  private boolean isPush;
  
  public String getPatientUri() {
    return patientUri;
  }
  public void setPatientUri(String patientUri) {
    this.patientUri = patientUri;
  }
  public String getOrderUri() {
    return orderUri;
  }
  public void setOrderUri(String orderUri) {
    this.orderUri = orderUri;
  }
  public String getPatientId() {
    return patientId;
  }
  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }
  public String getPatientDob() {
    return patientDob;
  }
  public void setPatientDob(String patientDob) {
    this.patientDob = patientDob;
  }
  public String getStudyDate() {
    return studyDate;
  }
  public void setStudyDate(String studyDate) {
    this.studyDate = studyDate;
  }
  public String getPatientName() {
    return patientName;
  }
  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }
  public String getOrderPriority() {
    return orderPriority;
  }
  public void setOrderPriority(String orderPriority) {
    this.orderPriority = orderPriority;
  }
  public String getOrderPriorityType() {
    return orderPriorityType;
  }
  public void setOrderPriorityType(String orderPriorityType) {
    this.orderPriorityType = orderPriorityType;
  }
  public boolean isPush() {
    return isPush;
  }
  public void setPush(boolean isPush) {
    this.isPush = isPush;
  }  
  
}
