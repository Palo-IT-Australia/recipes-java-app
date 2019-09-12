package au.com.imed.portal.referrer.referrerportal.jpa.history.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * use IMED_AUDITDB
create table REPORT_FCM_TOKEN (
       id INT IDENTITY(1,1) PRIMARY KEY,
       uid VARCHAR(256),
       token VARCHAR(3000),
       device_id VARCHAR(3000)
);
 *
 */
@Entity
@Table(name="REPORT_FCM_TOKEN", catalog = "dbo")
public class ReportFcmTokenEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private int id;
    
    @Column(name = "uid")
    private String uid;
    @Column(name = "token")
    private String token; 
    @Column(name = "device_id")
    private String deviceId;
    
    public int getId() {
      return id;
    }
    public void setId(int id) {
      this.id = id;
    }
    public String getUid() {
      return uid;
    }
    public void setUid(String uid) {
      this.uid = uid;
    }
    public String getToken() {
      return token;
    }
    public void setToken(String token) {
      this.token = token;
    }
    public String getDeviceId() {
      return deviceId;
    }
    public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
    }
    
    @Override
    public String toString() {
      return "ReportFcmTokenEntity [id=" + id + ", uid=" + uid + ", token=" + token + ", deviceId="
          + deviceId + "]";
    }
   
}
