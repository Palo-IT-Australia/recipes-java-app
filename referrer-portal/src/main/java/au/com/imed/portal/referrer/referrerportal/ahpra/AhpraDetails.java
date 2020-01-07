package au.com.imed.portal.referrer.referrerportal.ahpra;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"Profession",
	"Name",
	"Personal Details",
	"Principal Place of Practice",
	"Registration Details",
	"Registration Type - General"
})
public class AhpraDetails {
	@JsonProperty("Profession")
	private String profession;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Personal Details")
	private PersonalDetails personalDetails;
	@JsonProperty("Principal Place of Practice")
	private PrincipalPlaceOfPractice principalPlaceOfPractice;
	@JsonProperty("Registration Details")
	private RegistrationDetails registrationDetails;
	@JsonProperty("Registration Type - General")
	private RegistrationTypeGeneral registrationTypeGeneral;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("Profession")
	public String getProfession() {
		return profession;
	}

	@JsonProperty("Profession")
	public void setProfession(String profession) {
		this.profession = profession;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("Personal Details")
	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	@JsonProperty("Personal Details")
	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	@JsonProperty("Principal Place of Practice")
	public PrincipalPlaceOfPractice getPrincipalPlaceOfPractice() {
		return principalPlaceOfPractice;
	}

	@JsonProperty("Principal Place of Practice")
	public void setPrincipalPlaceOfPractice(PrincipalPlaceOfPractice principalPlaceOfPractice) {
		this.principalPlaceOfPractice = principalPlaceOfPractice;
	}

	@JsonProperty("Registration Details")
	public RegistrationDetails getRegistrationDetails() {
		return registrationDetails;
	}

	@JsonProperty("Registration Details")
	public void setRegistrationDetails(RegistrationDetails registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	@JsonProperty("Registration Type - General")
	public RegistrationTypeGeneral getRegistrationTypeGeneral() {
		return registrationTypeGeneral;
	}

	@JsonProperty("Registration Type - General")
	public void setRegistrationTypeGeneral(RegistrationTypeGeneral registrationTypeGeneral) {
		this.registrationTypeGeneral = registrationTypeGeneral;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"Sex",
		"Languages (in addition to English)",
		"Qualifications"
	})
	public class PersonalDetails {

		@JsonProperty("Sex")
		private String sex;
		@JsonProperty("Languages (in addition to English)")
		private String languagesInAdditionToEnglish;
		@JsonProperty("Qualifications")
		private String qualifications;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@JsonProperty("Sex")
		public String getSex() {
			return sex;
		}

		@JsonProperty("Sex")
		public void setSex(String sex) {
			this.sex = sex;
		}

		@JsonProperty("Languages (in addition to English)")
		public String getLanguagesInAdditionToEnglish() {
			return languagesInAdditionToEnglish;
		}

		@JsonProperty("Languages (in addition to English)")
		public void setLanguagesInAdditionToEnglish(String languagesInAdditionToEnglish) {
			this.languagesInAdditionToEnglish = languagesInAdditionToEnglish;
		}

		@JsonProperty("Qualifications")
		public String getQualifications() {
			return qualifications;
		}

		@JsonProperty("Qualifications")
		public void setQualifications(String qualifications) {
			this.qualifications = qualifications;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		@Override
		public String toString() {
			return "PersonalDetails [sex=" + sex + ", languagesInAdditionToEnglish=" + languagesInAdditionToEnglish
					+ ", qualifications=" + qualifications + ", additionalProperties=" + additionalProperties + "]";
		}
		
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"Suburb",
		"State",
		"Postcode",
		"Country"
	})
	public class PrincipalPlaceOfPractice {

		@JsonProperty("Suburb")
		private String suburb;
		@JsonProperty("State")
		private String state;
		@JsonProperty("Postcode")
		private String postcode;
		@JsonProperty("Country")
		private String country;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@JsonProperty("Suburb")
		public String getSuburb() {
			return suburb;
		}

		@JsonProperty("Suburb")
		public void setSuburb(String suburb) {
			this.suburb = suburb;
		}

		@JsonProperty("State")
		public String getState() {
			return state;
		}

		@JsonProperty("State")
		public void setState(String state) {
			this.state = state;
		}

		@JsonProperty("Postcode")
		public String getPostcode() {
			return postcode;
		}

		@JsonProperty("Postcode")
		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}

		@JsonProperty("Country")
		public String getCountry() {
			return country;
		}

		@JsonProperty("Country")
		public void setCountry(String country) {
			this.country = country;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		@Override
		public String toString() {
			return "PrincipalPlaceOfPractice [suburb=" + suburb + ", state=" + state + ", postcode=" + postcode + ", country="
					+ country + ", additionalProperties=" + additionalProperties + "]";
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"Profession",
		"Registration number",
		"Date of first Registration in Profession",
		"Registration status",
		"Registration expiry date",
		"Conditions",
		"Undertakings",
		"Reprimands"
	})
	public class RegistrationDetails {

		@JsonProperty("Profession")
		private String profession;
		@JsonProperty("Registration number")
		private String registrationNumber;
		@JsonProperty("Date of first Registration in Profession")
		private String dateOfFirstRegistrationInProfession;
		@JsonProperty("Registration status")
		private String registrationStatus;
		@JsonProperty("Registration expiry date")
		private String registrationExpiryDate;
		@JsonProperty("Conditions")
		private String conditions;
		@JsonProperty("Undertakings")
		private String undertakings;
		@JsonProperty("Reprimands")
		private String reprimands;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@JsonProperty("Profession")
		public String getProfession() {
			return profession;
		}

		@JsonProperty("Profession")
		public void setProfession(String profession) {
			this.profession = profession;
		}

		@JsonProperty("Registration number")
		public String getRegistrationNumber() {
			return registrationNumber;
		}

		@JsonProperty("Registration number")
		public void setRegistrationNumber(String registrationNumber) {
			this.registrationNumber = registrationNumber;
		}

		@JsonProperty("Date of first Registration in Profession")
		public String getDateOfFirstRegistrationInProfession() {
			return dateOfFirstRegistrationInProfession;
		}

		@JsonProperty("Date of first Registration in Profession")
		public void setDateOfFirstRegistrationInProfession(String dateOfFirstRegistrationInProfession) {
			this.dateOfFirstRegistrationInProfession = dateOfFirstRegistrationInProfession;
		}

		@JsonProperty("Registration status")
		public String getRegistrationStatus() {
			return registrationStatus;
		}

		@JsonProperty("Registration status")
		public void setRegistrationStatus(String registrationStatus) {
			this.registrationStatus = registrationStatus;
		}

		@JsonProperty("Registration expiry date")
		public String getRegistrationExpiryDate() {
			return registrationExpiryDate;
		}

		@JsonProperty("Registration expiry date")
		public void setRegistrationExpiryDate(String registrationExpiryDate) {
			this.registrationExpiryDate = registrationExpiryDate;
		}

		@JsonProperty("Conditions")
		public String getConditions() {
			return conditions;
		}

		@JsonProperty("Conditions")
		public void setConditions(String conditions) {
			this.conditions = conditions;
		}

		@JsonProperty("Undertakings")
		public String getUndertakings() {
			return undertakings;
		}

		@JsonProperty("Undertakings")
		public void setUndertakings(String undertakings) {
			this.undertakings = undertakings;
		}

		@JsonProperty("Reprimands")
		public String getReprimands() {
			return reprimands;
		}

		@JsonProperty("Reprimands")
		public void setReprimands(String reprimands) {
			this.reprimands = reprimands;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		@Override
		public String toString() {
			return "RegistrationDetails [profession=" + profession + ", registrationNumber=" + registrationNumber
					+ ", dateOfFirstRegistrationInProfession=" + dateOfFirstRegistrationInProfession + ", registrationStatus="
					+ registrationStatus + ", registrationExpiryDate=" + registrationExpiryDate + ", conditions=" + conditions
					+ ", undertakings=" + undertakings + ", reprimands=" + reprimands + ", additionalProperties="
					+ additionalProperties + "]";
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		"Registration expiry date",
		"Endorsements",
		"Notations - General",
		"Registration Requirements"
	})
	public class RegistrationTypeGeneral {

		@JsonProperty("Registration expiry date")
		private String registrationExpiryDate;
		@JsonProperty("Endorsements")
		private String endorsements;
		@JsonProperty("Notations - General")
		private String notationsGeneral;
		@JsonProperty("Registration Requirements")
		private String registrationRequirements;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@JsonProperty("Registration expiry date")
		public String getRegistrationExpiryDate() {
			return registrationExpiryDate;
		}

		@JsonProperty("Registration expiry date")
		public void setRegistrationExpiryDate(String registrationExpiryDate) {
			this.registrationExpiryDate = registrationExpiryDate;
		}

		@JsonProperty("Endorsements")
		public String getEndorsements() {
			return endorsements;
		}

		@JsonProperty("Endorsements")
		public void setEndorsements(String endorsements) {
			this.endorsements = endorsements;
		}

		@JsonProperty("Notations - General")
		public String getNotationsGeneral() {
			return notationsGeneral;
		}

		@JsonProperty("Notations - General")
		public void setNotationsGeneral(String notationsGeneral) {
			this.notationsGeneral = notationsGeneral;
		}

		@JsonProperty("Registration Requirements")
		public String getRegistrationRequirements() {
			return registrationRequirements;
		}

		@JsonProperty("Registration Requirements")
		public void setRegistrationRequirements(String registrationRequirements) {
			this.registrationRequirements = registrationRequirements;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		@Override
		public String toString() {
			return "RegistrationTypeGeneral [registrationExpiryDate=" + registrationExpiryDate + ", endorsements="
					+ endorsements + ", notationsGeneral=" + notationsGeneral + ", registrationRequirements="
					+ registrationRequirements + ", additionalProperties=" + additionalProperties + "]";
		}
		
	}

	@Override
	public String toString() {
		return "AhpraDetails [profession=" + profession + ", name=" + name + ", personalDetails=" + personalDetails
				+ ", principalPlaceOfPractice=" + principalPlaceOfPractice + ", registrationDetails=" + registrationDetails
				+ ", registrationTypeGeneral=" + registrationTypeGeneral + ", additionalProperties=" + additionalProperties
				+ "]";
	}
}
