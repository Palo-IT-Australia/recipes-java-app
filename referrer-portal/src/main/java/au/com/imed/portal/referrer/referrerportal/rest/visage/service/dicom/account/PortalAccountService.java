package au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.GreaterThanOrEqualsFilter;
import org.springframework.ldap.filter.LessThanOrEqualsFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import au.com.imed.portal.referrer.referrerportal.ldap.ABasicAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;

@Service
public class PortalAccountService extends ABasicAccountService {
	public ResponseEntity<String> getTermsAndConditions() {
		String temp = "";
		try {
			ClassPathResource cpr = new ClassPathResource("static/files/tandc.html");
			byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			temp = new String(bdata, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>(temp, HttpStatus.OK);
	}

	/**
	 * Check if the given user has PACS/VueMotion account and the email is unique in
	 * it
	 * 
	 * @param userName
	 * @return
	 */
	public boolean canUserViewImage(final String userName) {
		boolean canView = false;
		if (userName != null) {
			try {
				AccountDetail account = this.getAccountDetail(getPacsLdapTemplate(), userName);
				if (account != null) {
					// Email only one in pacs dn?
					canView = (this.findsAccountsByAttributes(getPacsLdapTemplate(), "mail", account.getEmail())
							.size() == 1);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("canUserViewImage() " + userName + " ? " + canView);
		return canView;
	}

	public boolean isEmailPortalUnique(final String userName) {
		boolean isUnique = false;
		if (userName != null) {
			AccountDetail accountDetail = getReferrerAccountDetail(userName);
			if (accountDetail == null) {
				// Falling back for IMED account image viewing
				accountDetail = getBusinessUnitAccountDetail(userName);
			}
			if (accountDetail != null) {
				final String email = accountDetail.getEmail();
				try {
					isUnique = isEmailUnique(getApplicationsLdapTemplate(), userName, email)
							&& isEmailUnique(getBusinessUnitLdapTemplate(), userName, email);
				} catch (Exception e) {
					e.printStackTrace();
					isUnique = false;
				}
			}
		}
		return isUnique;
	}

	public boolean isEmailGloballyUnique(final String userName, final String email) throws Exception {
		return isEmailUnique(getGlobalLdapTemplate(), userName, email);
	}

	private boolean isEmailUnique(LdapTemplate template, final String userName, final String email) throws Exception {
		if (userName == null || email == null || email.length() == 0) {
			throw new InvalidParameterException();
		}
		System.out.println("isEmailUnique() " + userName + " " + email);
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("mail", email));
		filter.and(new NotFilter(new EqualsFilter("uid", userName))); // don't count itself

		List<String> list = template.search("", filter.encode(), new AttributesMapper<String>() {
			@Override
			public String mapFromAttributes(Attributes attrs) throws NamingException {
				System.out.println("isEmailUnique() " + attrs);
				return attrs.get("uid").get(0).toString();
			}
		});

		System.out.println("isEmailUnique() # of emails " + list.size());
		return list.size() == 0;
	}

	public AccountDetail getBusinessUnitAccountDetail(final String userName) {
		System.out.println("getBusinessUnitAccountDetail() " + userName);
		LdapTemplate ldapTemplate;
		AccountDetail accountDetail = null;
		try {
			ldapTemplate = getBusinessUnitLdapTemplate();
			accountDetail = this.getAccountDetail(ldapTemplate, userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountDetail;
	}

	public AccountDetail getReferrerAccountDetail(final String userName) {
		System.out.println("getReferrerAccountDetail() " + userName);
		LdapTemplate ldapTemplate;
		AccountDetail accountDetail = null;
		try {
			ldapTemplate = getReferrerLdapTemplate();
			accountDetail = this.getAccountDetail(ldapTemplate, userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountDetail;
	}

	private AccountDetail getAccountDetail(LdapTemplate ldapTemplate, final String userName) {
		AccountDetail accountDetail = null;
		try {
			List<AccountDetail> list = ldapTemplate.search("", "(uid=" + userName + ")",
					new AttributesMapper<AccountDetail>() {
						@Override
						public AccountDetail mapFromAttributes(Attributes attrs) throws NamingException {
							AccountDetail detail = new AccountDetail();
							detail.setEmail(attrs.get("mail") != null ? attrs.get("mail").get(0).toString() : "");
							detail.setName(attrs.get("cn") != null ? attrs.get("cn").get(0).toString() : "");
							detail.setMobile(attrs.get("mobile") != null ? attrs.get("mobile").get(0).toString() : "");
							return detail;
						}
					});
			accountDetail = list.size() > 0 ? list.get(0) : null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return accountDetail;
	}

	private List<AccountDetail> findsAccountsByAttributes(LdapTemplate ldapTemplate, final String attributeName,
			final String attributeValue) {
		try {
			List<AccountDetail> list = ldapTemplate.search("", "(" + attributeName + "=" + attributeValue + ")",
					new AttributesMapper<AccountDetail>() {
						@Override
						public AccountDetail mapFromAttributes(Attributes attrs) throws NamingException {
							AccountDetail detail = new AccountDetail();
							detail.setEmail(attrs.get("mail") != null ? attrs.get("mail").get(0).toString() : "");
							detail.setName(attrs.get("cn") != null ? attrs.get("cn").get(0).toString() : "");
							detail.setMobile(attrs.get("mobile") != null ? attrs.get("mobile").get(0).toString() : "");
							return detail;
						}
					});
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ArrayList<>(0);
		}
	}

	public void updateReferrerAccountDetail(final String userName, final AccountDetail detail) throws Exception {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("uid", userName));

		LdapTemplate ldapTemplate = getReferrerLdapTemplate();
		List<Name> list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
		if (list.size() > 0) {
			System.out.println(list.get(0));

			Attribute mobileAttr = new BasicAttribute("mobile", detail.getMobile());
			ModificationItem mobileItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mobileAttr);
			Attribute emailAttr = new BasicAttribute("mail", detail.getEmail());
			ModificationItem emailItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, emailAttr);
			ldapTemplate.modifyAttributes(list.get(0), new ModificationItem[] { mobileItem, emailItem }); // relative dn
																											// in Name
		} else {
			throw new InvalidParameterException();
		}
	}

	private class PersonContextMapper extends AbstractContextMapper<Name> {
		public Name doMapFromContext(DirContextOperations context) {
			System.out.println("PersonContextMapper : " + context.getAttributes());
			return context.getDn();
		}
	}

	public boolean isPatientAccount(final String userName) {
		List<Name> list = new ArrayList<>(0);
		try {
			AndFilter filter = new AndFilter();
			filter.and(new EqualsFilter("uid", userName));

			LdapTemplate ldapTemplate = getPatientLdapTemplate();
			list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list.size() > 0;
	}

	public void updateReferrerPassword(final String userName, final AccountPassword accountPassword) throws Exception {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("uid", userName));

		LdapTemplate ldapTemplate = getReferrerLdapTemplate();
		List<Name> list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
		System.out.println("updateReferrerPassword() " + list);
		if (list.size() > 0) {
			// Check password
			filter = new AndFilter();
			filter.and(new EqualsFilter("uid", userName));
			boolean isauth = ldapTemplate.authenticate("", filter.toString(), accountPassword.getOld_password());
			System.out.println("updateReferrerPassword() password corrrect? " + isauth);
			if (isauth) {
				// Update password
				Attribute pswdAttr = new BasicAttribute("userPassword", accountPassword.getNew_password());
				ModificationItem pswdItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, pswdAttr);
				ldapTemplate.modifyAttributes(list.get(0), new ModificationItem[] { pswdItem });
				System.out.println("updateReferrerPassword() updated password");
			} else {
				throw new AuthenticationException();
			}
		} else {
			throw new InvalidParameterException();
		}
	}

	/**
	 * Search created referrer account btw given dates
	 * 
	 * @param after  20180521
	 * @param before 20180528
	 * @return
	 */
	public String auditCreatedUsers(String after, String before) {
		String retstr = "";
		if (after != null && after.length() > 0 && before != null && before.length() > 0) {
			try {
				AndFilter filter = new AndFilter();
				filter.and(new GreaterThanOrEqualsFilter("createTimeStamp", after.replaceAll("-", "") + "000000Z"));
				filter.and(new LessThanOrEqualsFilter("createTimeStamp", before.replaceAll("-", "") + "115959Z"));
				filter.and(new EqualsFilter("objectclass", "Person"));

				final String filterstr = filter.encode();
				System.out.println(filterstr);
				List<String> list = getReferrerLdapTemplate().search("", filterstr, new AttributesMapper<String>() {
					@Override
					public String mapFromAttributes(Attributes attrs) throws NamingException {
						return attrs.get("uid").get(0).toString();
					}
				});
				retstr = "Created Users : " + list.size() + " " + list;
			} catch (Exception ex) {
				ex.printStackTrace();
				retstr = "";
			}
		}
		return retstr;
	}

}
