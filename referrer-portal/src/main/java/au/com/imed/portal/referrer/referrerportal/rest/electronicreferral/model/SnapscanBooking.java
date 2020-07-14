package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnapscanBooking {
	private String title;
	private String gender;
	private String firstName;
	private String lastName;
	private String dateOfBirth;
	private String contactNumber;
	private String email;
	private String address;
	private String postalCode;
	private String examNotes;
	private String notes;
	private String referringDoctorName;
	private String referringDoctorNumber;
	private String referringDoctorPhone;
	private String referringDoctorEmail;
	private String referringPracticeName;
	private String referringDoctorAddress;
	private String referringDoctorPostalCode;
	private String copyTo;
	private boolean sendEmail;

	// Getter Methods 

	public String getTitle() {
		return title;
	}

	public String getGender() {
		return gender;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public String getAddress() {
		return address;
	}

	public String getExamNotes() {
		return examNotes;
	}

	public String getNotes() {
		return notes;
	}

	public String getReferringDoctorName() {
		return referringDoctorName;
	}

	public String getReferringDoctorNumber() {
		return referringDoctorNumber;
	}

	public String getReferringDoctorPhone() {
		return referringDoctorPhone;
	}

	public String getReferringDoctorEmail() {
		return referringDoctorEmail;
	}

	public String getReferringPracticeName() {
		return referringPracticeName;
	}

	public String getReferringDoctorAddress() {
		return referringDoctorAddress;
	}

	public boolean getSendEmail() {
		return sendEmail;
	}

	// Setter Methods 

	public void setTitle(String title) {
		this.title = title;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setExamNotes(String examNotes) {
		this.examNotes = examNotes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setReferringDoctorName(String referringDoctorName) {
		this.referringDoctorName = referringDoctorName;
	}

	public void setReferringDoctorNumber(String referringDoctorNumber) {
		this.referringDoctorNumber = referringDoctorNumber;
	}

	public void setReferringDoctorPhone(String referringDoctorPhone) {
		this.referringDoctorPhone = referringDoctorPhone;
	}

	public void setReferringDoctorEmail(String referringDoctorEmail) {
		this.referringDoctorEmail = referringDoctorEmail;
	}

	public void setReferringPracticeName(String referringPracticeName) {
		this.referringPracticeName = referringPracticeName;
	}

	public void setReferringDoctorAddress(String referringDoctorAddress) {
		this.referringDoctorAddress = referringDoctorAddress;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getCopyTo() {
		return copyTo;
	}

	public void setCopyTo(String copyTo) {
		this.copyTo = copyTo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getReferringDoctorPostalCode() {
		return referringDoctorPostalCode;
	}

	public void setReferringDoctorPostalCode(String referringDoctorPostalCode) {
		this.referringDoctorPostalCode = referringDoctorPostalCode;
	}

	@Override
	public String toString() {
		return "SnapscanBooking [title=" + title + ", gender=" + gender + ", firstName=" + firstName + ", lastName="
				+ lastName + ", dateOfBirth=" + dateOfBirth + ", contactNumber=" + contactNumber + ", email=" + email
				+ ", address=" + address + ", postalCode=" + postalCode + ", examNotes=" + examNotes + ", notes=" + notes
				+ ", referringDoctorName=" + referringDoctorName + ", referringDoctorNumber=" + referringDoctorNumber
				+ ", referringDoctorPhone=" + referringDoctorPhone + ", referringDoctorEmail=" + referringDoctorEmail
				+ ", referringPracticeName=" + referringPracticeName + ", referringDoctorAddress=" + referringDoctorAddress
				+ ", referringDoctorPostalCode=" + referringDoctorPostalCode + ", copyTo=" + copyTo + ", sendEmail=" + sendEmail
				+ "]";
	}

}
