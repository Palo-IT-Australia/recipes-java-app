package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

public class FindPatient {
	private String uri;
	PatientAddress address;
	private String dateOfBirth;
	private String email;
	private String fax;
	private String fullName;
	private String title;
	private String givenNames;
	private String familyName;
	private String referenceNumber;
	private String sex;
	private String consentGiven;
	private String mobile;
	private String patientId;
	private String phone1;
	private String phone2;
	private String businessUnit;


	public String getUri() {
		return uri;
	}




	public void setUri(String uri) {
		this.uri = uri;
	}




	public PatientAddress getAddress() {
		return address;
	}




	public void setAddress(PatientAddress address) {
		this.address = address;
	}




	public String getDateOfBirth() {
		return dateOfBirth;
	}




	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}




	public String getEmail() {
		return email;
	}




	public void setEmail(String email) {
		this.email = email;
	}




	public String getFax() {
		return fax;
	}




	public void setFax(String fax) {
		this.fax = fax;
	}




	public String getFullName() {
		return fullName;
	}




	public void setFullName(String fullName) {
		this.fullName = fullName;
	}




	public String getTitle() {
		return title;
	}




	public void setTitle(String title) {
		this.title = title;
	}




	public String getGivenNames() {
		return givenNames;
	}




	public void setGivenNames(String givenNames) {
		this.givenNames = givenNames;
	}




	public String getFamilyName() {
		return familyName;
	}




	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}




	public String getReferenceNumber() {
		return referenceNumber;
	}




	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}




	public String getSex() {
		return sex;
	}




	public void setSex(String sex) {
		this.sex = sex;
	}




	public String getConsentGiven() {
		return consentGiven;
	}




	public void setConsentGiven(String consentGiven) {
		this.consentGiven = consentGiven;
	}




	public String getMobile() {
		return mobile;
	}




	public void setMobile(String mobile) {
		this.mobile = mobile;
	}




	public String getPatientId() {
		return patientId;
	}




	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}




	public String getPhone1() {
		return phone1;
	}




	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}




	public String getPhone2() {
		return phone2;
	}




	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}




	public String getBusinessUnit() {
		return businessUnit;
	}




	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}




	public class PatientAddress {
		private String city;
		private String country;
		private String line1;
		private String line2;
		private String line3;
		private String postcode;
		private String state;


		// Getter Methods 

		public String getCity() {
			return city;
		}

		public String getCountry() {
			return country;
		}

		public String getLine1() {
			return line1;
		}

		public String getLine2() {
			return line2;
		}

		public String getLine3() {
			return line3;
		}

		public String getPostcode() {
			return postcode;
		}

		public String getState() {
			return state;
		}

		// Setter Methods 

		public void setCity(String city) {
			this.city = city;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public void setLine1(String line1) {
			this.line1 = line1;
		}

		public void setLine2(String line2) {
			this.line2 = line2;
		}

		public void setLine3(String line3) {
			this.line3 = line3;
		}

		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}

		public void setState(String state) {
			this.state = state;
		}
	}
}
