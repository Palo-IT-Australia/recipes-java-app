package au.com.imed.portal.referrer.referrerportal.jpa.clinic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {
	private String firstName;
	private String lastName;
	private String phone;
	private String medicareNumber;
	private String email;
	private String dob;
	private String postcode;
	private String notes;
	private String clinic;
	private int clinicId;
	private String modality;
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getClinicId() {
		return clinicId;
	}

	public void setClinicId(int clinicId) {
		this.clinicId = clinicId;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getClinic() {
		return clinic;
	}

	public void setClinic(String clinic) {
		this.clinic = clinic;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMedicareNumber() {
		return medicareNumber;
	}

	public void setMedicareNumber(String medicareNumber) {
		this.medicareNumber = medicareNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "Appointment [firstName=" + firstName + ", lastName=" + lastName + ", phone=" + phone
				+ ", medicareNumber=" + medicareNumber + ", email=" + email + ", dob=" + dob + ", postcode=" + postcode
				+ ", notes=" + notes + ", clinic=" + clinic + ", clinicId=" + clinicId + ", modality=" + modality
				+ ", time=" + time + "]";
	}

}
