package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ExternalIdentifier {

	private ExternalSystem externalSystem;
	private String orderIdentifier;
	private String patientIdentifier;

	public ExternalSystem getExternalSystem() {
		return externalSystem;
	}

	public void setExternalSystem(ExternalSystem externalSystem) {
		this.externalSystem = externalSystem;
	}

	public String getOrderIdentifier() {
		return orderIdentifier;
	}

	public String getPatientIdentifier() {
		return patientIdentifier;
	}

	public void setOrderIdentifier(String orderIdentifier) {
		this.orderIdentifier = orderIdentifier;
	}

	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class ExternalSystem {
		private String uri;
		private String shortName;

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getShortName() {
			return shortName;
		}

		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
	}
}
