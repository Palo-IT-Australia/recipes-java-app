package au.com.imed.portal.referrer.referrerportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LdapUserDetails {
	private String cn;
  private String uid;
  private String email;
  
  private String givenName;
  private String surname;
  private String password;
  
  private String mobile;
  private String ahpra;
  private String accountType;
  private String businessUnit;
  
  private boolean locked;

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAhpra() {
		return ahpra;
	}

	public void setAhpra(String ahpra) {
		this.ahpra = ahpra;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
  
  
}
