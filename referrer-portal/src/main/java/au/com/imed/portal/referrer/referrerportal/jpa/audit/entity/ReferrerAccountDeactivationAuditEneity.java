package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="REFERRER_ACCOUNT_DEACTIVATION_AUDIT", catalog = "dbo")
public class ReferrerAccountDeactivationAuditEneity {
	@Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "username")
  private String username;

  @Column(name = "command")
  private String command;

  @Column(name = "uid")
  private String uid;

  @Column(name = "mail")
  private String mail;

  @Column(name = "ahpra")
  private String ahpra;
  @Column(name = "dn")
  private String dn;
  @Column(name = "givenName")
  private String givenName;
  @Column(name = "sn")
  private String sn;
  
  @Column(name = "audit_at")
  private Date auditAt;

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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAhpra() {
		return ahpra;
	}

	public void setAhpra(String ahpra) {
		this.ahpra = ahpra;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Date getAuditAt() {
		return auditAt;
	}

	public void setAuditAt(Date auditAt) {
		this.auditAt = auditAt;
	}

}
