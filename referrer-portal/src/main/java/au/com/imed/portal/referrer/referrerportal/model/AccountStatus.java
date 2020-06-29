package au.com.imed.portal.referrer.referrerportal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountStatus {
	private String	uid;
	private List<ReferrerProviderEntity> providers;
  private boolean portal;
  private boolean pacs;
  private boolean imedpacs;
  private boolean visage;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<ReferrerProviderEntity> getProviders() {
		return providers;
	}
	public void setProviders(List<ReferrerProviderEntity> providers) {
		this.providers = providers;
	}
	public boolean isPortal() {
		return portal;
	}
	public void setPortal(boolean portal) {
		this.portal = portal;
	}
	public boolean isPacs() {
		return pacs;
	}
	public void setPacs(boolean pacs) {
		this.pacs = pacs;
	}
	public boolean isImedpacs() {
		return imedpacs;
	}
	public void setImedpacs(boolean imedpacs) {
		this.imedpacs = imedpacs;
	}
	public boolean isVisage() {
		return visage;
	}
	public void setVisage(boolean visage) {
		this.visage = visage;
	}
  
  
}
