package au.com.imed.portal.referrer.referrerportal.model;

import java.io.Serializable;

public class ExternalPractice implements Serializable {
  private static final long serialVersionUID = 1L;

  private String providerNumber;
  private String practiceName;
  private String practicePhone;
  private String practiceFax;
  private String practiceAddress;
  
  private String practiceStreet;
  private String practiceSuburb;
  private String practiceState;
  private String practicePostcode;
  
  private String practiceType;

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

	public String getPracticeType() {
		return practiceType;
	}

	public void setPracticeType(String practiceType) {
		this.practiceType = practiceType;
	}

	@Override
	public String toString() {
		return "ExternalPractice [providerNumber=" + providerNumber + ", practiceName=" + practiceName + ", practicePhone="
				+ practicePhone + ", practiceFax=" + practiceFax + ", practiceAddress=" + practiceAddress + ", practiceStreet="
				+ practiceStreet + ", practiceSuburb=" + practiceSuburb + ", practiceState=" + practiceState
				+ ", practicePostcode=" + practicePostcode + ", practiceType=" + practiceType + "]";
	}
  
}
