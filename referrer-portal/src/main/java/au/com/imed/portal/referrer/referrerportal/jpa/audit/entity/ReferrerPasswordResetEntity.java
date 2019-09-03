package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="REFERRER_PASSWORD_RESET", catalog = "dbo")
public class ReferrerPasswordResetEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "url_code")
  private String urlCode;

  @Column(name = "passcode_hash")
  private String passcodeHash;

  @Column(name = "passcode_salt")
  private String passcodeSalt;
  
  @Column(name = "expired_at")
  private Date expiredAt;
  
  @Column(name = "activated_at")
  private Date activatedAt;
  
  @Column(name = "failures")
  private byte failures = 0;

  @Column(name = "uid")
  private String uid;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUrlCode() {
    return urlCode;
  }

  public void setUrlCode(String urlCode) {
    this.urlCode = urlCode;
  }

  public String getPasscodeHash() {
    return passcodeHash;
  }

  public void setPasscodeHash(String passcodeHash) {
    this.passcodeHash = passcodeHash;
  }

  public String getPasscodeSalt() {
    return passcodeSalt;
  }

  public void setPasscodeSalt(String passcodeSalt) {
    this.passcodeSalt = passcodeSalt;
  }

  public Date getExpiredAt() {
    return expiredAt;
  }

  public void setExpiredAt(Date expiredAt) {
    this.expiredAt = expiredAt;
  }

  public Date getActivatedAt() {
    return activatedAt;
  }

  public void setActivatedAt(Date activatedAt) {
    this.activatedAt = activatedAt;
  }

  public byte getFailures() {
    return failures;
  }

  public void setFailures(byte failures) {
    this.failures = failures;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

}
