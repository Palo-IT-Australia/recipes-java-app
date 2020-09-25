package au.com.imed.portal.referrer.referrerportal.rest.cleanup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GlobalLdapAccount {
	private String uid;
	private String dn;
	private String type;
	private String addn;

	private String cn;
  private String mail;
  
  private String givenName;
  private String sn;
  
  private boolean canRemove;
  private boolean selected;
  
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getAddn() {
		return addn;
	}

	public void setAddn(String addn) {
		this.addn = addn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isCanRemove() {
		return canRemove;
	}
	
	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
}

