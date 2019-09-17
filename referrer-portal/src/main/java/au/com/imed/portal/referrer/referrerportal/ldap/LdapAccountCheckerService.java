package au.com.imed.portal.referrer.referrerportal.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;

@Service
public class LdapAccountCheckerService {
	@Autowired
	private ReferrerAccountService referrerAccountService;
	
	/**
	 * AHPRA is set only on portal(stage/approved) referrers
	 */
  public boolean isAhpraAvailable(final String ahpra) {
  	// TODO visage ahpra search == 0
  	return referrerAccountService.findAccountsPortalByAttr("AHPRA", ahpra).size() == 0;
  }
  
  public boolean isEmailAvailable(final String email) {
  	return referrerAccountService.findAccountsGlobalByAttr("mail", email).size() == 0;
  }
  
  /**
   * ex. ibm portal federated area
   */
  public boolean isEmailAvailablePortal(final String email) {
  	return referrerAccountService.findAccountsPortalByAttr("mail", email).size() == 0 &&
  			new ImedActiveDirectoryLdapManager().findByMail(email).size() == 0;
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
  			new ImedActiveDirectoryLdapManager().findByUid(uid).size() == 0;
  }
}
