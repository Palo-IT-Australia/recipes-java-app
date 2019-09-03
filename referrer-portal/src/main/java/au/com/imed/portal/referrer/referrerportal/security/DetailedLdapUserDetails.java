package au.com.imed.portal.referrer.referrerportal.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

public class DetailedLdapUserDetails implements LdapUserDetails {
	private static final long serialVersionUID = 248693664341248529L;

	private LdapUserDetails details;
	private String sn;
	private String givenName;
	private String mobile;
	private String email;

	public DetailedLdapUserDetails(LdapUserDetails details, String sn, String givenName, String mobile, String email) {
		super();
		this.details = details;
		this.sn = sn;
		this.givenName = givenName;
		this.mobile = mobile;
		this.email = email;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public boolean isEnabled() {
	    return details.isEnabled();
	}

	public String getDn() {
	    return details.getDn();
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return details.getAuthorities();
	}

	public String getPassword() {
	    return details.getPassword();
	}

	public String getUsername() {
	    return details.getUsername();
	}

	public boolean isAccountNonExpired() {
	    return details.isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
	    return details.isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
	    return details.isCredentialsNonExpired();
	}

	@Override
	public void eraseCredentials() {
		details.eraseCredentials();		
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "DetailedLdapUserDetails [details=" + details + ", givenName=" + givenName + ", mobile=" + mobile
				+ ", email=" + email + "]";
	}

}
