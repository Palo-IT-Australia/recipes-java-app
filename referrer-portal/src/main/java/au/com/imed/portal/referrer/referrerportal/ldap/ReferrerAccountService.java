package au.com.imed.portal.referrer.referrerportal.ldap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.ChangeModel;
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.utils.ValidationUtility;

@Primary
@Service
public class ReferrerAccountService extends ABasicAccountService {
	private Logger logger = LoggerFactory.getLogger(ReferrerAccountService.class);

	public boolean isHospitalAccess(final String uid) {
		boolean is = false;
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("cn", "HospitalAccess"));
		filter.and(new EqualsFilter("objectclass", "groupOfUniqueNames"));

		if(uid != null && uid.length() >= 3) {
			try {
				List<List<String>> members = getApplicationsLdapTemplate()
						.search("", filter.encode(), new AttributesMapper<List<String>>() {
							@Override
							public List<String> mapFromAttributes(Attributes attributes) throws NamingException {
								List<String> lst = new ArrayList<>();
								NamingEnumeration<?> ne = attributes.get("uniquemember").getAll();
								while(ne.hasMore()) {
									String m = ne.next().toString();
									lst.add(m);
								}
								return lst;
							}
						});
				if(members.get(0).contains("uid=" + uid + ",ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au")) {
					is = true;
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		return is;
	}

	public AccountDetail getReferrerAccountDetail(final String userName) {
		return getReferrerAccountDetail("uid", userName);
	}

	public AccountDetail getReferrerAccountDetailByEmail(final String email) {
		return getReferrerAccountDetail("mail", email);
	}

	private AccountDetail getReferrerAccountDetail(final String name, final String value) {
		AccountDetail detail = null;
		try {
			List<AccountDetail> list = getAccountDetailList(getReferrerLdapTemplate(), name, value);
			detail = list.size() > 0 ? list.get(0) : null;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return detail;
	}

	public List<AccountDetail> findAccountsGlobalByAttr(final String name, final String value) {
		List<AccountDetail> list;
		try {
			list = getAccountDetailList(getGlobalLdapTemplate(), name, value);
		}catch(Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}

	public List<AccountDetail> findAccountsPortalByAttr(final String name, final String value) {
		List<AccountDetail> list;
		try {
			list = getAccountDetailList(getApplicationsLdapTemplate(), name, value);
		}catch(Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}

	private List<AccountDetail> getAccountDetailList(LdapTemplate template, final String name, final String value) {
		List<AccountDetail> list;
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter(name, value));
		try {
			list = template.search("", filter.encode(), new AccountDetailAttributeMapper());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}
	
	protected List<AccountDetail> getAccountDetailListLike(LdapTemplate template, final String name, final String valueLike) {
		List<AccountDetail> list;
		LdapQuery query = query().where(name).like(valueLike);
		try {
			list = template.search(query, new AccountDetailAttributeMapper());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}
	
	protected List<Name> getAccountDnList(LdapTemplate template, final String name, final String value) {
		List<Name> list;
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter(name, value));
		try {
			list = template.search("", filter.encode(), new PersonContextMapper());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}
	
	public List<Name> GetReferrerDnListByAttr(final String name, final String value) {
		List<Name> list;
		try {
			list = getAccountDnList(getApplicationsLdapTemplate(), name, value);
		}catch(Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}

	public Map<String, String> updateReferrerAccountDetail(final String userName, final DetailModel detail) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (!ValidationUtility.isValidEmail(detail.getEmail())) {
			resultMap.put(MODEL_KEY_ERROR_MSG, "Invalid email");
		}else if (!ValidationUtility.hasAtleastOneNumberWithOptionalSpace(detail.getMobile())) {
			resultMap.put(MODEL_KEY_ERROR_MSG, "Invalid phone number");
		}else {
			AndFilter filter = new AndFilter();
			filter.and(new EqualsFilter("uid", userName));
			LdapTemplate ldapTemplate = getReferrerLdapTemplate();
			List<Name> list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
			if(list.size() > 0) {
				Attribute mobileAttr = new BasicAttribute("mobile", detail.getMobile());
				ModificationItem mobileItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mobileAttr);
				Attribute emailAttr = new BasicAttribute("mail", detail.getEmail());
				ModificationItem emailItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, emailAttr);
				ldapTemplate.modifyAttributes(list.get(0), new ModificationItem [] {mobileItem, emailItem});  // relative dn in Name
				resultMap.put(MODEL_KEY_SUCCESS_MSG, "Your details have been changed.");
			}
			else {
				throw new InvalidParameterException();
			}
		}  	
		return resultMap;
	}

	public void resetReferrerPassword(final String userName, final String password) throws Exception 
	{
		if(userName.isEmpty() || password.isEmpty()) {
			throw new InvalidParameterException();
		} else {
			logger.warn("resetReferrerPassword() invalid params");
			updateReferrerPassword(userName, password, null);
		}
	}

	public void updateReferrerPassword(final String userName, final ChangeModel changeModel) throws Exception 
	{
		if(userName.isEmpty() || changeModel.getCurrentPassword().isEmpty() || changeModel.getNewPassword().isEmpty()) {
			logger.warn("updateReferrerPassword() invalid params");
			throw new InvalidParameterException();
		} else {
			updateReferrerPassword(userName, changeModel.getNewPassword(), changeModel.getCurrentPassword());
		}
	}

	private void updateReferrerPassword(final String userName, final String newPassword, final String currentPassword) throws Exception {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("uid", userName));

		LdapTemplate ldapTemplate = getReferrerLdapTemplate();
		List<Name> list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
		logger.info("updateReferrerPassword() " + list);
		if(list.size() > 0) {
			// Check password 
			boolean isauth = currentPassword == null || ldapTemplate.authenticate("", filter.toString(), currentPassword);  
			logger.info("updateReferrerPassword() password corrrect? " + isauth);
			if(isauth)
			{
				// Update password
				Attribute pswdAttr = new BasicAttribute("userPassword", newPassword);
				ModificationItem pswdItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, pswdAttr);
				ldapTemplate.modifyAttributes(list.get(0), new ModificationItem [] {pswdItem}); 
				logger.info("updateReferrerPassword() updated password");
			}
			else {
				throw new AuthenticationException();
			}
		}
		else {
			throw new InvalidParameterException();
		}
	}

	//
	//	Approver
	//
	public List<StageUser> getStageUserList() {
		List<StageUser> list;
		LdapQuery query = query().where("uid").like("*");
		try {
			list = getReferrerStagingLdapTemplate().search(query, new StageUserAttributeMapper());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}
	
	public List<StageUser> getFinalisingUserList() {
		List<StageUser> list;
		LdapQuery query = query().where(PortalConstant.PARAM_ATTR_FINALIZING_PAGER).is(PortalConstant.PARAM_ATTR_VALUE_FINALIZING_PAGER);
		try {
			list = getReferrerLdapTemplate().search(query, new StageUserAttributeMapper());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			list = new ArrayList<>(0);
		}
		return list;
	}
	
	
}