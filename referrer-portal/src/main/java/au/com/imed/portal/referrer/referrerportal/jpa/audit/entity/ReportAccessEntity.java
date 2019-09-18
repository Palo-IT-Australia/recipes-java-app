package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
create table REPORT_ACCESS (
       id INT IDENTITY(1,1) PRIMARY KEY,
       url_code VARCHAR(4000),
       passcode_hash VARCHAR(4000),
       passcode_salt VARCHAR(4000),
       expired_at datetime2,
       report_uri VARCHAR(50),
       order_uri VARCHAR(50),
       failures tinyint
);
 *
 */
@Entity
@Table(name="REPORT_ACCESS", catalog = "dbo")
public class ReportAccessEntity {
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
  
  @Column(name = "report_uri")
  private String reportUri;
  
  @Column(name = "order_uri")
  private String orderUri;
  
  @Column(name = "failures")
  private byte failures = 0;

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

  public String getReportUri() {
    return reportUri;
  }

  public void setReportUri(String reportUri) {
    this.reportUri = reportUri;
  }

  public String getOrderUri() {
    return orderUri;
  }

  public void setOrderUri(String orderUri) {
    this.orderUri = orderUri;
  }

  public byte getFailures() {
    return failures;
  }

  public void setFailures(byte failures) {
    this.failures = failures;
  }

}
