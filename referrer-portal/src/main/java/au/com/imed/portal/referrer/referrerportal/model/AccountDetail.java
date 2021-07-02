package au.com.imed.portal.referrer.referrerportal.model;

import au.com.imed.portal.referrer.referrerportal.security.DetailedLdapUserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetail {
    private String uid;
    private String name;
    private String lastName;
    private String email;
    private String mobile;

    public AccountDetail() {
    }

    public AccountDetail(DetailedLdapUserDetails principal) {
        this.uid = principal.getUsername();
        this.name = principal.getGivenName();
        this.email = principal.getEmail();
        this.mobile = principal.getMobile();
        this.lastName = principal.getSn();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "AccountDetail [name=" + name + ", email=" + email + ", mobile=" + mobile + "]";
    }
}
