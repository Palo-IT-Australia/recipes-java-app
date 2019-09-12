package au.com.imed.portal.referrer.referrerportal.jpa.history.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VISAGE_USER_PREFERENCES", catalog = "dbo")
public class HospitalPreferencesEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "username")
  private String username;
  
  @Column(name = "hospital_uri")
  private String hospitalUri;
  
  @Column(name = "hospital_status")
  private String hospitalStatus;

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
