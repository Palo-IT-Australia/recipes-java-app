package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CRM_ADMIN_AUDIT")

public class CrmAdminAuditEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private int id;
	
  @Column(name = "audit_at")
  private Date auditAt;
  @Column(name = "command")
  private String command;
  @Column(name = "crm")
  private String crm;
  @Column(name = "referrer")
  private String referrer;
  @Column(name = "parameter")
  private String parameter;
  
  @Column(name = "account_type")
  private String accountType;
  
  @Column(name = "password_encoded")
  private String passwordEncoded;
  
  @Column(name = "first_name")
  private String firstName;
  
  @Column(name = "last_name")
  private String lastName;
  
  @Column(name = "mobile")
  private String mobile;
  
  @Column(name = "phone")
  private String phone;
  
  @Column(name = "email")
  private String email;
  
  @Column(name = "ahpra")
  private String ahpra;
  
  @Column(name = "validation_id")
  private Integer validationId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getAuditAt() {
		return auditAt;
	}

	public void setAuditAt(Date auditAt) {
		this.auditAt = auditAt;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCrm() {
		return crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getPasswordEncoded() {
		return passwordEncoded;
	}

	public void setPasswordEncoded(String passwordEncoded) {
		this.passwordEncoded = passwordEncoded;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAhpra() {
		return ahpra;
	}

	public void setAhpra(String ahpra) {
		this.ahpra = ahpra;
	}

	public Integer getValidationId() {
		return validationId;
	}

	public void setValidationId(Integer validationId) {
		this.validationId = validationId;
	}
  
}
