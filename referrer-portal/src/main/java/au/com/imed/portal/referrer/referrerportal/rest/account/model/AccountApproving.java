package au.com.imed.portal.referrer.referrerportal.rest.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountApproving {
	private String uid;
	private String bu;
	private String newuid;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getBu() {
		return bu;
	}
	public void setBu(String bu) {
		this.bu = bu;
	}
	public String getNewuid() {
		return newuid;
	}
	public void setNewuid(String newuid) {
		this.newuid = newuid;
	}
}
