package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.model.*;
import au.com.imed.portal.referrer.referrerportal.utils.ValidationUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Primary
@Service
public class ReferrerAccountService extends ABasicAccountService {
    private Logger logger = LoggerFactory.getLogger(ReferrerAccountService.class);

    public boolean isHospitalAccess(final String uid) {
        boolean is = false;
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("cn", "HospitalAccess"));
        filter.and(new EqualsFilter("objectclass", "groupOfUniqueNames"));

        if (uid != null && uid.length() >= 3) {
            try {
                List<List<String>> members = getApplicationsLdapTemplate().search("", filter.encode(),
                        new AttributesMapper<List<String>>() {
                            @Override
                            public List<String> mapFromAttributes(Attributes attributes) throws NamingException {
                                List<String> lst = new ArrayList<>();
                                NamingEnumeration<?> ne = attributes.get("uniquemember").getAll();
                                while (ne.hasMore()) {
                                    String m = ne.next().toString();
                                    lst.add(m);
                                }
                                return lst;
                            }
                        });
                if (!members.isEmpty() && members.get(0)
                        .contains("uid=" + uid + ",ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au")) {
                    is = true;
                }
            } catch (Exception ex) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return detail;
    }

    public List<AccountDetail> findAccountsGlobalByAttr(final String name, final String value) {
        List<AccountDetail> list;
        try {
            list = getAccountDetailList(getGlobalLdapTemplate(), name, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<AccountDetail> findAccountsGlobalByAttr(final String name, final String value, final String uid) {
        List<AccountDetail> list;
        try {
            list = getAccountDetailList(getGlobalLdapTemplate(), name, value, uid);
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<String> findGlobalByAtr(final String name, final String value) {
        try {
            List<String> list = getGlobalLdapTemplate()
                    .search(query().where(name).is(value), new AttributesMapper<String>() {
                        @Override
                        public String mapFromAttributes(Attributes attrs) throws NamingException {
                            return attrs.get("uid").get(0).toString();
                        }
                    });
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<String>(0);
        }
    }

    public List<AccountDetail> findAccountsPortalByAttr(final String name, final String value) {
        List<AccountDetail> list;
        try {
            list = getAccountDetailList(getApplicationsLdapTemplate(), name, value);
        } catch (Exception ex) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    private List<AccountDetail> getAccountDetailList(LdapTemplate template, final String name, final String value, final String uid) {
        List<AccountDetail> list;
        LdapQuery query = query().where(name).is(value).and("uid").not().is(uid);
        try {
            list = template.search(query, new AccountDetailAttributeMapper());
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    protected List<AccountDetail> getAccountDetailListLike(LdapTemplate template, final String name,
                                                           final String valueLike) {
        List<AccountDetail> list;
        LdapQuery query = query().where(name).like(valueLike);
        try {
            list = template.search(query, new AccountDetailAttributeMapper());
        } catch (Exception ex) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<Name> GetReferrerDnListByAttr(final String name, final String value) {
        List<Name> list;
        try {
            list = getAccountDnList(getReferrerLdapTemplate(), name, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<Name> GetPacsDnListByAttr(final String name, final String value) {
        List<Name> list;
        try {
            list = getAccountDnList(getPacsLdapTemplate(), name, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<Name> GetImedPacsDnListByAttr(final String name, final String value) {
        List<Name> list;
        try {
            list = getAccountDnList(getImedPacsLdapTemplate(), name, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public Map<String, String> updateReferrerAccountDetail(final String userName, final DetailModel detail)
            throws Exception {
        Map<String, String> resultMap = new HashMap<String, String>();
        if (!ValidationUtility.isValidEmail(detail.getEmail())) {
            resultMap.put(MODEL_KEY_ERROR_MSG, "Invalid email");
        } else if (StringUtils.isNotEmpty(detail.getMobile()) && !ValidationUtility.hasAtleastOneNumberWithOptionalSpace(detail.getMobile())) {
            resultMap.put(MODEL_KEY_ERROR_MSG, "Invalid phone number");
        } else {
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter("uid", userName));
            LdapTemplate ldapTemplate = getReferrerLdapTemplate();
            List<Name> list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
            if (list.size() > 0) {
                Attribute mobileAttr = new BasicAttribute("mobile", detail.getMobile());
                ModificationItem mobileItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mobileAttr);
                Attribute emailAttr = new BasicAttribute("mail", detail.getEmail());
                ModificationItem emailItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, emailAttr);
                ldapTemplate.modifyAttributes(list.get(0), new ModificationItem[]{mobileItem, emailItem}); // relative
                // dn in
                // Name
                resultMap.put(MODEL_KEY_SUCCESS_MSG, "Your details have been changed.");
            } else {
                throw new InvalidParameterException();
            }
        }
        return resultMap;
    }

    /**
     * Change referrer password without any password check
     *
     * @param uid
     * @param newPassword
     * @throws Exception
     */
    public void resetReferrerPassword(final String userName, final String password) throws Exception {
        if (userName.isEmpty() || password.isEmpty()) {
            logger.warn("resetReferrerPassword() invalid params");
            throw new InvalidParameterException();
        } else {
            updateReferrerPassword(userName, password, null);
        }
    }

    public void updateReferrerPassword(final String userName, final ChangeModel changeModel) throws Exception {
        if (userName.isEmpty() || changeModel.getCurrentPassword().isEmpty()
                || changeModel.getNewPassword().isEmpty()) {
            logger.warn("updateReferrerPassword() invalid params");
            throw new InvalidParameterException();
        } else {
            updateReferrerPassword(userName, changeModel.getNewPassword(), changeModel.getCurrentPassword());
        }
    }

    private void updateReferrerPassword(final String userName, final String newPassword, final String currentPassword)
            throws Exception {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("uid", userName));

        LdapTemplate ldapTemplate = getReferrerLdapTemplate();
        List<Name> list = ldapTemplate.search("", filter.encode(), new PersonContextMapper());
        logger.info("updateReferrerPassword() " + list);
        if (list.size() > 0) {
            // Check password
            boolean isauth = currentPassword == null
                    || ldapTemplate.authenticate("", filter.toString(), currentPassword);
            logger.info("updateReferrerPassword() password corrrect? " + isauth);
            if (isauth) {
                // Update password
                Attribute pswdAttr = new BasicAttribute("userPassword", newPassword);
                ModificationItem pswdItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, pswdAttr);
                ldapTemplate.modifyAttributes(list.get(0), new ModificationItem[]{pswdItem});
                logger.info("updateReferrerPassword() updated password");
            } else {
                throw new AuthenticationException();
            }
        } else {
            throw new InvalidParameterException();
        }
    }

    public void lockUnlockReferrerAccount(final String uid, final boolean lock) throws Exception {
        LdapTemplate ldapTemplate = getReferrerLdapTemplate();
        Name dn = getAccountDnList(ldapTemplate, "uid", uid).get(0);

        Attribute unlockAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_ACC_LOCKED, lock ? "true" : "false");
        ModificationItem unlockItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, unlockAttr);

        logger.info("lockUnlockReferrerAccount() {} ", dn);
        ldapTemplate.modifyAttributes(dn, new ModificationItem[]{unlockItem});
    }

    public List<LdapUserDetails> findFuzzyReferrerAccounts(final String word) throws Exception {
        LdapQuery query = query()
                .attributes("ibm-pwdAccountLocked", "cn", "uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName")
                .where("uid").like("*" + word + "*")
                .or("mail").is(word)
                .or("givenName").is(word)
                .or("ahpra").is(word)
                .or("sn").is(word);
        return getReferrerLdapTemplate().search(query, new LdapUserDetailsUserAttributeMapper());
    }

    public List<LdapUserDetails> findReferrerAccountsByUid(final String uid) throws Exception {
        LdapQuery query = query()
                .attributes("ibm-pwdAccountLocked", "cn", "uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName")
                .where("uid").is(uid);
        return getReferrerLdapTemplate().search(query, new LdapUserDetailsUserAttributeMapper());
    }

    public List<LdapUserDetails> findReferrerAccountsByEmailAndAhpra(final String email, final String ahpra) throws Exception {
        LdapQuery query = query()
                .attributes(PortalConstant.PARAM_ATTR_FINALIZING_PAGER, "ibm-pwdAccountLocked", "cn", "uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName")
                .where("mail").is(email)
                .and("ahpra").is(ahpra)
                .and(PortalConstant.PARAM_ATTR_FINALIZING_PAGER).not().is(PortalConstant.PARAM_ATTR_VALUE_FINALIZING_PAGER)
                .and(PortalConstant.PARAM_ATTR_FINALIZING_PAGER).not().is(PortalConstant.PARAM_ATTR_VALUE_VALIDATING_PAGER);
        return getReferrerLdapTemplate().search(query, new LdapUserDetailsUserAttributeMapper());
    }

    public List<Name> findImedPacsUsersDnListByFirstAndLastNames(final String firstName, final String lastName) throws Exception {
        return findDnListByFirstAndLastNames(getImedPacsLdapTemplate(), firstName, lastName);
    }

    public List<Name> findPacsUsersDnListByFirstAndLastNames(final String firstName, final String lastName) throws Exception {
        return findDnListByFirstAndLastNames(getPacsLdapTemplate(), firstName, lastName);
    }

    protected List<Name> findDnListByFirstAndLastNames(LdapTemplate template, final String firstName, final String lastName) {
        LdapQuery query = query().where("givenName").is(firstName).and("sn").is(lastName);
        return template.search(query, new PersonContextMapper());
    }

    //
    // Approver
    //
    public List<StageUser> getStageUserList() {
        List<StageUser> list;
        LdapQuery query = query().where("uid").like("*");
        try {
            list = getReferrerStagingLdapTemplate().search(query, new StageUserAttributeMapper());
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    /**
     * Except for CRMCreate and Validating
     */
    public List<StageUser> getStageNewUserList() {
        List<StageUser> list;
        LdapQuery query = query()
                .attributes(PortalConstant.PARAM_ATTR_CRM_ACTION, "pager", "ibm-pwdAccountLocked", "cn", "uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName")
                .where("uid").like("*")
                .and(PortalConstant.PARAM_ATTR_CRM_ACTION).not().is(PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE)
                .and(PortalConstant.PARAM_ATTR_FINALIZING_PAGER).not().is(PortalConstant.PARAM_ATTR_VALUE_VALIDATING_PAGER);
        try {
            list = getReferrerStagingLdapTemplate().search(query, new StageUserAttributeMapper());
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<StageUser> getStageValidatingUserList() {
        List<StageUser> list;
        LdapQuery query = query()
                .attributes("pager", "ibm-pwdAccountLocked", "cn", "uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName")
                .where("uid").like("*").and(PortalConstant.PARAM_ATTR_FINALIZING_PAGER).is(PortalConstant.PARAM_ATTR_VALUE_VALIDATING_PAGER);
        try {
            list = getReferrerStagingLdapTemplate().search(query, new StageUserAttributeMapper());
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public List<StageUser> getFinalisingUserList() {
        List<StageUser> list;
        LdapQuery query = query().where(PortalConstant.PARAM_ATTR_FINALIZING_PAGER)
                .is(PortalConstant.PARAM_ATTR_VALUE_FINALIZING_PAGER);
        try {
            list = getReferrerLdapTemplate().search(query, new StageUserAttributeMapper());
        } catch (Exception ex) {
            ex.printStackTrace();
            list = new ArrayList<>(0);
        }
        return list;
    }

    public StageUser findReferrerAsStageUser(final String uid) {
        StageUser usr = null;
        LdapQuery query = query().where("uid").is(uid);
        try {
            usr = getReferrerLdapTemplate().search(query, new StageUserAttributeMapper()).get(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return usr;
    }

    public boolean checkPassword(final String username, final String password) {
        boolean isAuth = false;
        try {
            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                AndFilter filter = new AndFilter();
                filter.and(new EqualsFilter("uid", username));
                isAuth = getReferrerLdapTemplate().authenticate("", filter.toString(), password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.info("Check referrer credential is valid ? " + isAuth);
        return isAuth;
    }

    public void approveUser(final String uid, final String newuid, final String bu) throws Exception {
        LdapTemplate stageTemplate = getReferrerStagingLdapTemplate();
        Name currentDn = getAccountDnList(stageTemplate, "uid", uid).get(0);

        List<ModificationItem> moditemList = new ArrayList<>(2);

        if (bu != null && !bu.isBlank()) {
            Attribute buAttr = new BasicAttribute("BusinessUnit", bu);
            ModificationItem buItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, buAttr);
            moditemList.add(buItem);
        }

        Attribute finAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_FINALIZING_PAGER,
                PortalConstant.PARAM_ATTR_VALUE_FINALIZING_PAGER);
        ModificationItem finItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, finAttr);
        moditemList.add(finItem);

        stageTemplate.modifyAttributes(currentDn, moditemList.toArray(new ModificationItem[moditemList.size()]));

        String newDnStr = currentDn.toString();
        if (newuid != null && !newuid.isBlank()) {
            newDnStr = newDnStr.replace("uid=" + uid, "uid=" + newuid);
        }
        logger.info("approveUser() moving " + currentDn.toString() + " to " + newDnStr);
        getPortalLdapTemplate().rename(currentDn.toString() + ",ou=Staging", newDnStr + ",ou=Referrers");
    }

    public StageUser finaliseUser(final String uid) throws Exception {
        LdapTemplate ldapTemplate = getReferrerLdapTemplate();
        Name dn = getAccountDnList(ldapTemplate, "uid", uid).get(0);

        Attribute finAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_FINALIZING_PAGER, "");
        ModificationItem finItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, finAttr);

        Attribute unlockAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_ACC_LOCKED, "false");
        ModificationItem unlockItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, unlockAttr);

        logger.info("finaliseUser() {} ", dn);
        ldapTemplate.modifyAttributes(dn, new ModificationItem[]{finItem, unlockItem});

        return findReferrerAsStageUser(uid);
    }

    public void declineUser(final String uid, final String step) throws Exception {
        boolean isFinal = "finalising".equalsIgnoreCase(step);
        LdapTemplate ldapTemplate = isFinal ? getReferrerLdapTemplate()
                : getReferrerStagingLdapTemplate();
        Name dn = getAccountDnList(ldapTemplate, "uid", uid).get(0);
        logger.info("declineUser() {} ", dn);
        ldapTemplate.unbind(dn);

        if (isFinal) {
            cleanupPacsUsers(uid);
        }
    }

    protected void cleanupPacsUsers(final String uid) {
        try {
            LdapTemplate ldapTemplate = getPacsLdapTemplate();
            Name dn = getAccountDnList(ldapTemplate, "uid", uid).get(0);
            logger.info("cleanupPacsUsers() deleting pacs {} ", dn);
            if (dn != null) {
                ldapTemplate.unbind(dn);
            }
        } catch (Exception ex) {
            logger.info("PACS user not yet synched, skipping" + uid);
        }

        try {
            LdapTemplate ldapTemplate = getImedPacsLdapTemplate();
            Name dn = getAccountDnList(ldapTemplate, "uid", uid).get(0);
            logger.info("cleanupPacsUsers() deleting imed pacs {} ", dn);
            if (dn != null) {
                ldapTemplate.unbind(dn);
            }
        } catch (Exception ex) {
            logger.info("I-MED PACS user not yet synched, skipping" + uid);
        }
    }

    //
    // CRM Admin
    //
    public void updateReferrerCrmAction(final String uid, final String value) throws Exception {
        LdapTemplate ldapTemplate = getReferrerLdapTemplate();
        Name dn = getAccountDnList(ldapTemplate, "uid", uid).get(0);

        Attribute newAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_CRM_ACTION, value);
        ModificationItem modifyItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, newAttr);

        logger.info("updateCrmAction() updating {} {}", dn, value);
        ldapTemplate.modifyAttributes(dn, new ModificationItem[]{modifyItem});
    }

    public boolean updateReferrerCrmActionIfStating(final String uid, final String value) {
        boolean isSet = false;

        try {
            LdapTemplate ldapTemplate = getReferrerStagingLdapTemplate();
            List<Name> lst = getAccountDnList(ldapTemplate, "uid", uid);

            if (lst.size() > 0) {
                Name dn = lst.get(0);
                Attribute newAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_CRM_ACTION, value);
                ModificationItem modifyItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, newAttr);

                logger.info("updateCrmAction() updating {} {}", dn, value);
                ldapTemplate.modifyAttributes(dn, new ModificationItem[]{modifyItem});
                isSet = true;
                logger.info("User {} is marked as CRMCreate in LDAP", uid);
            } else {
                logger.info("User {} is not stating, but in auto validation DB", uid);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isSet;
    }

    public String getReferrerCrmAction(final String uid) {
        String action = "";
        try {
            LdapQuery query = query()
                    .attributes(PortalConstant.PARAM_ATTR_CRM_ACTION, "uid")
                    .where("uid").is(uid);
            List<String> list = getReferrerLdapTemplate().search(query, new AttributesMapper<String>() {
                @Override
                public String mapFromAttributes(Attributes attributes) throws NamingException {
                    String val = attributes.get(PortalConstant.PARAM_ATTR_CRM_ACTION) != null ?
                            attributes.get(PortalConstant.PARAM_ATTR_CRM_ACTION).get(0).toString() : null;
                    return val;
                }
            });
            action = list.size() > 0 ? list.get(0) : "";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.info("CRM Action " + action);
        return action;
    }

    public void updateCrmValidating(final String uid, final String bu, boolean onOrOff) throws Exception {
        LdapTemplate stageTemplate = getReferrerStagingLdapTemplate();
        Name currentDn = getAccountDnList(stageTemplate, "uid", uid).get(0);

        List<ModificationItem> moditemList = new ArrayList<>(2);

        if (bu != null && !bu.isBlank()) {
            Attribute buAttr = new BasicAttribute("BusinessUnit", bu);
            ModificationItem buItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, buAttr);
            moditemList.add(buItem);
        }

        Attribute finAttr = new BasicAttribute(PortalConstant.PARAM_ATTR_FINALIZING_PAGER, onOrOff ? PortalConstant.PARAM_ATTR_VALUE_VALIDATING_PAGER : "");
        ModificationItem finItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, finAttr);
        moditemList.add(finItem);

        stageTemplate.modifyAttributes(currentDn, moditemList.toArray(new ModificationItem[moditemList.size()]));
    }
}
