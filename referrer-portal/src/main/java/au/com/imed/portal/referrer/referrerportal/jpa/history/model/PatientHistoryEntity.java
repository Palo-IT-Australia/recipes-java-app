package au.com.imed.portal.referrer.referrerportal.jpa.history.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VISAGE_PATIENT_HISTORY", catalog = "dbo")
public class PatientHistoryEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "username")
  private String username;

  @Column(name = "patient_name")
  private String patientName;

  @Column(name = "patient_dob")
  private String patientDob;

  @Column(name = "patient_uri")
  private String patientUri;

  @Column(name = "patient_id")
  private String patientId;

  @Column(name = "modified_at")
  private Date modifiedAt;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public String getPatientUri() {
    return patientUri;
  }

  public void setPatientUri(String patientUri) {
    this.patientUri = patientUri;
  }

  public String getPatientId() {
    return patientId;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public Date getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(Date modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
  
}
