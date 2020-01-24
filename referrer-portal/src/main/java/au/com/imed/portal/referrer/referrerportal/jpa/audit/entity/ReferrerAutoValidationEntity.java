package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REFERRER_AUTO_VALIDATION")
public class ReferrerAutoValidationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private int id;
	
  @Column(name = "uid")
  private String uid;
  
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
  
  @Column(name = "account_type")
  private String accountType;
  
  @Column(name = "contact_advanced")
  private String contactAdvanced;
  
  @Column(name = "filmless")
  private String filmless;
  
  @Column(name = "business_unit")
  private String businessUnit;
  
  @Column(name = "validation_msg")
  private String validationMsg;
  
  @Column(name = "validation_status")
  private String validationStatus;
  
  @Column(name = "apply_at")
  private Date applyAt;
  
  @Column(name = "account_at")
  private Date accountAt;
  
  @Column(name = "notify_at")
  private Date notifyAt;
  
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
	public String getContactAdvanced() {
		return contactAdvanced;
	}
	public void setContactAdvanced(String contactAdvanced) {
		this.contactAdvanced = contactAdvanced;
	}
	public String getFilmless() {
		return filmless;
	}
	public void setFilmless(String filmless) {
		this.filmless = filmless;
	}
	public String getBusinessUnit() {
		return businessUnit;
	}
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}
	public String getValidationMsg() {
		return validationMsg;
	}
	public void setValidationMsg(String validationMsg) {
		this.validationMsg = validationMsg;
	}
	public String getValidationStatus() {
		return validationStatus;
	}
	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}
	public Date getApplyAt() {
		return applyAt;
	}
	public void setApplyAt(Date applyAt) {
		this.applyAt = applyAt;
	}
	public Date getAccountAt() {
		return accountAt;
	}
	public void setAccountAt(Date accountAt) {
		this.accountAt = accountAt;
	}
	public Date getNotifyAt() {
		return notifyAt;
	}
	public void setNotifyAt(Date notifyAt) {
		this.notifyAt = notifyAt;
	}
	
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	@Override
	public String toString() {
		return "ReferrerAutoValidationEntity [id=" + id + ", uid=" + uid + ", firstName=" + firstName + ", lastName="
				+ lastName + ", mobile=" + mobile + ", phone=" + phone + ", email=" + email + ", ahpra=" + ahpra
				+ ", accountType=" + accountType + ", contactAdvanced=" + contactAdvanced + ", filmless=" + filmless
				+ ", businessUnit=" + businessUnit + ", validationMsg=" + validationMsg + ", validationStatus="
				+ validationStatus + ", applyAt=" + applyAt + ", accountAt=" + accountAt + ", notifyAt=" + notifyAt + "]";
	}
	

}
