package au.com.imed.portal.referrer.referrerportal.jpa.history.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="REPORT_NOTIFICATION", catalog = "dbo")
public class ReportNotificationEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "message_at")
  private Date messageAt;
  @Column(name = "uid")
  private String uid;
  @Column(name = "patient_id")
  private String patientId;
  @Column(name = "patient_name")
  private String patientName;
  @Column(name = "patient_dob")
  private String patientDob;
  @Column(name = "referrer_name")
  private String referrerName;
  @Column(name = "provider_number")
  private String providerNumber;
  @Column(name = "accession_number")
  private String accessionNumber;
  @Column(name = "order_status")
  private String orderStatus;
  @Column(name = "order_at")
  private Date orderAt;
  @Column(name = "order_description")
  private String orderDescription;
  @Column(name = "viewed")
  private boolean viewed;
  @Column(name = "order_priority")
  private String orderPriority;
  @Column(name = "order_priority_type")
  private String orderPriorityType;
  @Column(name = "order_uri")
  private String orderUri;
  @Column(name = "patient_uri")
  private String patientUri;
  @Column(name = "report_uri")
  private String reportUri;
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public Date getMessageAt() {
    return messageAt;
  }
  public void setMessageAt(Date messageAt) {
    this.messageAt = messageAt;
  }
  public String getUid() {
    return uid;
  }
  public void setUid(String uid) {
    this.uid = uid;
  }
  public String getPatientId() {
    return patientId;
  }
  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }
  public String getPatientName() {
    return patientName;
  }
  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }
  public String getPatientDob() {
    return patientDob;
  }
  public void setPatientDob(String patientDob) {
    this.patientDob = patientDob;
  }
  public String getReferrerName() {
    return referrerName;
  }
  public void setReferrerName(String referrerName) {
    this.referrerName = referrerName;
  }
  public String getAccessionNumber() {
    return accessionNumber;
  }
  public void setAccessionNumber(String accessionNumber) {
    this.accessionNumber = accessionNumber;
  }
  public String getOrderStatus() {
    return orderStatus;
  }
  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
  public Date getOrderAt() {
    return orderAt;
  }
  public void setOrderAt(Date orderAt) {
    this.orderAt = orderAt;
  }
  public String getOrderDescription() {
    return orderDescription;
  }
  public void setOrderDescription(String orderDescription) {
    this.orderDescription = orderDescription;
  }
  public String getProviderNumber() {
    return providerNumber;
  }
  public void setProviderNumber(String providerNumber) {
    this.providerNumber = providerNumber;
  }
  public boolean isViewed() {
    return viewed;
  }
  public void setViewed(boolean viewed) {
    this.viewed = viewed;
  }

  public String getOrderPriority() {
    return orderPriority;
  }
  public void setOrderPriority(String orderPriority) {
    this.orderPriority = orderPriority;
  }
  public String getOrderUri() {
    return orderUri;
  }
  public void setOrderUri(String orderUri) {
    this.orderUri = orderUri;
  }
  public String getPatientUri() {
    return patientUri;
  }
  public void setPatientUri(String patientUri) {
    this.patientUri = patientUri;
  }
  
  public String getOrderPriorityType() {
    return orderPriorityType;
  }
  public void setOrderPriorityType(String orderPriorityType) {
    this.orderPriorityType = orderPriorityType;
  }
  public String getReportUri() {
    return reportUri;
  }
  public void setReportUri(String reportUri) {
    this.reportUri = reportUri;
  }
  @Override
  public String toString() {
    return "ReportNotificationEntity [id=" + id + ", messageAt=" + messageAt + ", uid=" + uid
        + ", patientId=" + patientId + ", patientName=" + patientName + ", patientDob="
        + patientDob + ", referrerName=" + referrerName + ", providerNumber=" + providerNumber
        + ", accessionNumber=" + accessionNumber + ", orderStatus=" + orderStatus + ", orderAt="
        + orderAt + ", orderDescription=" + orderDescription + ", viewed=" + viewed
        + ", orderPriority=" + orderPriority + ", orderPriorityType=" + orderPriorityType
        + ", orderUri=" + orderUri + ", patientUri=" + patientUri + ", reportUri=" + reportUri
        + "]";
  }
 
}
