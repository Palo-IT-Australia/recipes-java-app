package au.com.imed.portal.referrer.referrerportal.rest.forms.model;

public class InstallSupport {
	private String firstName;
	 private String lastName;
	 private String practiceName;
	 private String practicePhone;
	 private String practicePostcode;

	 // Getter Methods 

	 public String getFirstName() {
	  return firstName;
	 }

	 public String getLastName() {
	  return lastName;
	 }

	 public String getPracticeName() {
	  return practiceName;
	 }

	 public String getPracticePhone() {
	  return practicePhone;
	 }

	 public String getPracticePostcode() {
	  return practicePostcode;
	 }

	 // Setter Methods 

	 public void setFirstName(String firstName) {
	  this.firstName = firstName;
	 }

	 public void setLastName(String lastName) {
	  this.lastName = lastName;
	 }

	 public void setPracticeName(String practiceName) {
	  this.practiceName = practiceName;
	 }

	 public void setPracticePhone(String practicePhone) {
	  this.practicePhone = practicePhone;
	 }

	 public void setPracticePostcode(String practicePostcode) {
	  this.practicePostcode = practicePostcode;
	 }

	@Override
	public String toString() {
		return "InstallSupport [firstName=" + firstName + ", lastName=" + lastName + ", practiceName=" + practiceName
				+ ", practicePhone=" + practicePhone + ", practicePostcode=" + practicePostcode + "]";
	}
	 
	 
}
