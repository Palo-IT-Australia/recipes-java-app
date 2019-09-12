package au.com.imed.portal.referrer.referrerportal.common.util;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;

public class CommonUtil {

	/**
	 * Converts given email to ldap username
	 * 
	 * @param email
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String emailToUid(final String email) throws IllegalArgumentException {
		String uid = "";
		if (email != null && email.length() > 0) {
			uid = email.replace(PortalConstant.ATMARK_IN_EMAIL, PortalConstant.ATMARK_IN_LDAP);
		} else {
			throw new IllegalArgumentException("Invalid email address for uid");
		}
		return uid;
	}

	/**
	 * Converts given ldap username to email id
	 * 
	 * @param uid
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String uidToEmail(final String uid) throws IllegalArgumentException {
		String email = "";
		if (uid != null && uid.length() > 0) {
			email = uid.replace(PortalConstant.ATMARK_IN_LDAP, PortalConstant.ATMARK_IN_EMAIL);
		} else {
			throw new IllegalArgumentException("Invalid uid");
		}
		return email;
	}

}
