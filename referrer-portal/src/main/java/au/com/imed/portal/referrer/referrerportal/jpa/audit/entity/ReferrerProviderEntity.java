package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="REFERRER_PROVIDER", catalog = "dbo")
public class ReferrerProviderEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "username")
  private String username;
  
  @Column(name = "provider_number")
  private String providerNumber;

  @Column(name = "practice_name")
  private String practiceName;
 
  @Column(name = "practice_phone")
  private String practicePhone;
 
  @Column(name = "practice_fax")
  private String practiceFax;
 
  @Column(name = "practice_address")
  private String practiceAddress;
  
  
  @Column(name = "practice_street")
  private String practiceStreet;

  @Column(name = "practice_suburb")
  private String practiceSuburb;
  
  @Column(name = "practice_state")
  private String practiceState;
  
  @Column(name = "practice_postcode")
  private String practicePostcode;
  
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
  public String getProviderNumber() {
    return providerNumber;
  }
  public void setProviderNumber(String providerNumber) {
    this.providerNumber = providerNumber;
  }
  public String getPracticeName() {
    return practiceName;
  }
  public void setPracticeName(String practiceName) {
    this.practiceName = practiceName;
  }
  public String getPracticePhone() {
    return practicePhone;
  }
  public void setPracticePhone(String practicePhone) {
    this.practicePhone = practicePhone;
  }
  public String getPracticeFax() {
    return practiceFax;
  }
  public void setPracticeFax(String practiceFax) {
    this.practiceFax = practiceFax;
  }
  public String getPracticeAddress() {
    return practiceAddress;
  }
  public void setPracticeAddress(String practiceAddress) {
    this.practiceAddress = practiceAddress;
  }
  public String getPracticeStreet() {
    return practiceStreet;
  }
  public void setPracticeStreet(String practiceStreet) {
    this.practiceStreet = practiceStreet;
  }
  public String getPracticeSuburb() {
    return practiceSuburb;
  }
  public void setPracticeSuburb(String practiceSuburb) {
    this.practiceSuburb = practiceSuburb;
  }
  public String getPracticeState() {
    return practiceState;
  }
  public void setPracticeState(String practiceState) {
    this.practiceState = practiceState;
  }
  public String getPracticePostcode() {
    return practicePostcode;
  }
  public void setPracticePostcode(String practicePostcode) {
    this.practicePostcode = practicePostcode;
  }
  
	@Override
	public String toString() {
		return "ReferrerProviderEntity [id=" + id + ", username=" + username + ", providerNumber=" + providerNumber
				+ ", practiceName=" + practiceName + ", practicePhone=" + practicePhone + ", practiceFax=" + practiceFax
				+ ", practiceAddress=" + practiceAddress + ", practiceStreet=" + practiceStreet + ", practiceSuburb="
				+ practiceSuburb + ", practiceState=" + practiceState + ", practicePostcode=" + practicePostcode + "]";
	}
}
