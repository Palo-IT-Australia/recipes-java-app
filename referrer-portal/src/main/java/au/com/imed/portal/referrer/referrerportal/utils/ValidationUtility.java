package au.com.imed.portal.referrer.referrerportal.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Class to validate input.
 *
 */
public class ValidationUtility {

	/**
	 * Validates whether the given state value is <br>
	 * 1. Empty <br>
	 * 2. Contains white space or tab <br>
	 * 
	 * @param state
	 * @return
	 */
	public static boolean isValidState(String state) {

		boolean valid = true;

		if (StringUtils.isBlank(state)) {
			valid = false;
		}

		if (StringUtils.containsAny(state, new char[] { ' ', '\t' })) {
			valid = false;
		}

		return valid;
	}
	
	/**
	 * Checks whether the string has at least one alpha and optional space
	 * 
	 * @param stringToValidate
	 * @return
	 */
	public static boolean hasAtleastOneAlphaWithOptionalSpace(String stringToValidate) {

		boolean valid = true;
		
		if(StringUtils.isBlank(stringToValidate)) {
			valid = false;
		}
		
		if (!StringUtils.isAlphaSpace(stringToValidate)) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Checks whether the string has at least one number and optional space
	 * 
	 * @param stringToValidate
	 * @return
	 */
	public static boolean hasAtleastOneNumberWithOptionalSpace(String stringToValidate) {

		boolean valid = true;
		
		if(StringUtils.isBlank(stringToValidate)) {
			valid = false;
		}
		
		if (!StringUtils.isNumericSpace(stringToValidate)) {
			valid = false;
		}
		
		return valid;
	}

	public static boolean isValidEmail(String emailToValidate) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		return emailValidator.isValid(emailToValidate);
	}
	
	public static boolean isValidPassword(String pwdToVaidate) {
		return (!StringUtils.isEmpty(pwdToVaidate) && pwdToVaidate.length() >= 8);		
	}

}
