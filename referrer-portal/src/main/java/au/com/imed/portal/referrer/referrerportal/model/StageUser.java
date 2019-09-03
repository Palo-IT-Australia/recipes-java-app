package au.com.imed.portal.referrer.referrerportal.model;

import java.util.List;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;

public class StageUser {
  private String cn;
  private String uid;
  private String email;
  
  private String givenName;
  private String surname;
  private String password;
  
  private String mobile;
  private String ahpra;
  private String accountType;
  private String businessUnit;
  
  private List<ReferrerProviderEntity> providers;
  
  public String getCn() {
    return cn;
  }
  public void setCn(String cn) {
    this.cn = cn;
  }
  public String getUid() {
    return uid;
  }
  public void setUid(String uid) {
    this.uid = uid;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getGivenName() {
    return givenName;
  }
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }
  public String getSurname() {
    return surname;
  }
  public void setSurname(String surname) {
    this.surname = surname;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getMobile() {
    return mobile;
  }
  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  public String getAhpra() {
    return ahpra;
  }
  public void setAhpra(String ahpra) {
    this.ahpra = ahpra;
  }
  public List<ReferrerProviderEntity> getProviders() {
    return providers;
  }
  public void setProviders(List<ReferrerProviderEntity> providers) {
    this.providers = providers;
  }
  public String getAccountType() {
    return accountType;
  }
  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }
  public String getBusinessUnit() {
    return businessUnit;
  }
  public void setBusinessUnit(String businessUnit) {
    this.businessUnit = businessUnit;
  }
  @Override
  public String toString() {
    return "StageUser [cn=" + cn + ", uid=" + uid + ", email=" + email + ", givenName=" + givenName
        + ", surname=" + surname + ", password=" + password + ", mobile=" + mobile + ", ahpra="
        + ahpra + ", accountType=" + accountType + ", businessUnit=" + businessUnit
        + ", providers=" + providers + "]";
  }
  
}
