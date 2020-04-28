package au.com.imed.portal.referrer.referrerportal.electronicreferraldownload;

public class ElectronicReferralDownloadModel {
	private String passcode;
	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	@Override
	public String toString() {
		return "ElectronicReferralDownloadModel [passcode=" + passcode + ", secret=" + secret + "]";
	}
}
