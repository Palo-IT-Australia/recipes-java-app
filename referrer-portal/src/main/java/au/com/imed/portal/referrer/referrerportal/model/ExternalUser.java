package au.com.imed.portal.referrer.referrerportal.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ExternalUser  implements Serializable{
  
  private static final long serialVersionUID = 1L;
  
  private String userid;
  private String password;
  private String confirmPassword;
  private String email;
  private String firstName;
  private String lastName;
  private String ahpraNumber;
  private String preferredPhone;
  private String mobile;
  private String contactAdvanced;
  private String os;
  private String filmless;
  private String accountType;

  private List<ExternalPractice> practices;
  
  public String getUserid() {
    return userid;
  }
  public void setUserid(String userid) {
    this.userid = userid;
  }
  public String getAccountType() {
    return accountType;
  }
  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getConfirmPassword() {
    return confirmPassword;
  }
  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
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
  public String getAhpraNumber() {
    return ahpraNumber;
  }
  public void setAhpraNumber(String ahpraNumber) {
    this.ahpraNumber = ahpraNumber;
  }
  public String getPreferredPhone() {
    return preferredPhone;
  }
  public void setPreferredPhone(String preferredPhone) {
    this.preferredPhone = preferredPhone;
  }
  public String getMobile() {
    return mobile;
  }
  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  public String getContactAdvanced() {
    return contactAdvanced;
  }
  public void setContactAdvanced(String contactAdvanced) {
    this.contactAdvanced = contactAdvanced;
  }
  public String getOs() {
    return os;
  }
  public void setOs(String os) {
    this.os = os;
  }
  public String getFilmless() {
    return filmless;
  }
  public void setFilmless(String filmless) {
    this.filmless = filmless;
  }
  public List<ExternalPractice> getPractices() {
    return practices;
  }
  public void setPractices(List<ExternalPractice> practices) {
    this.practices = practices;
  }
	@Override
	public String toString() {
		return "ExternalUser [userid=" + userid + ", password=" + password + ", confirmPassword=" + confirmPassword
				+ ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + ", ahpraNumber=" + ahpraNumber
				+ ", preferredPhone=" + preferredPhone + ", mobile=" + mobile + ", contactAdvanced=" + contactAdvanced + ", os="
				+ os + ", filmless=" + filmless + ", accountType=" + accountType + ", practices=" + practices + "]";
	}
  
}
  