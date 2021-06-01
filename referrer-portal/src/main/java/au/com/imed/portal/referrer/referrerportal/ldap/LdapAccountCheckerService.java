package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerAutoValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_INVALID;

@Service
public class LdapAccountCheckerService {
    @Autowired
    private ReferrerAccountService referrerAccountService;

    @Autowired
    private ReferrerAutoValidationRepository referrerAutoValidationRepository;

    /**
     * AHPRA is set only on portal(stage/approved) referrers
     */
    public boolean isAhpraAvailable(final String ahpra) {
        return referrerAccountService.findAccountsPortalByAttr("AHPRA", ahpra).size() == 0 &&
                referrerAutoValidationRepository.findByAhpraAndValidationStatusNot(ahpra, VALIDATION_STATUS_INVALID).size() == 0;
    }

    public boolean isEmailAvailable(final String email) {
        return referrerAccountService.findAccountsGlobalByAttr("mail", email).size() == 0 &&
                referrerAutoValidationRepository.findByEmailAndValidationStatusNot(email, VALIDATION_STATUS_INVALID).size() == 0;
    }

    /**
     * ex. ibm portal federated area
     */
    public boolean isEmailAvailablePortal(final String email) {
        return referrerAccountService.findAccountsPortalByAttr("mail", email).size() == 0 &&
                new ImedActiveDirectoryLdapManager().findByMail(email).size() == 0 &&
                referrerAutoValidationRepository.findByEmailAndValidationStatusNot(email, VALIDATION_STATUS_INVALID).size() == 0;
    }

    public boolean isEmailAvailableForUser(final String email, final String uid) {
        return referrerAccountService.findAccountsGlobalByAttr("mail", email, uid).size() == 0 &&
                new ImedActiveDirectoryLdapManager().findByAttributeExceptUid("mail", email, uid).size() == 0;
    }

    /**
     * ex. ibm portal federated
     */
    public boolean isUserIdAvailable(final String uid) {
        return referrerAccountService.findAccountsPortalByAttr("uid", uid).size() == 0 &&
                new ImedActiveDirectoryLdapManager().findByUid(uid).size() == 0 &&
                referrerAutoValidationRepository.findByUidAndValidationStatusNot(uid, VALIDATION_STATUS_INVALID).size() == 0;
    }

    public boolean login(String username, String password) {
        return referrerAccountService.checkPassword(username, password);
    }
}
