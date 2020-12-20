package au.com.imed.portal.referrer.referrerportal.model;

public class RetrieveModel {
	private String email;
	private String ahpra;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAhpra() {
		return ahpra;
	}
	public void setAhpra(String ahpra) {
		this.ahpra = ahpra;
	}
	@Override
	public String toString() {
		return "RetrieveModel [email=" + email + ", ahpra=" + ahpra + "]";
	}
	
}
