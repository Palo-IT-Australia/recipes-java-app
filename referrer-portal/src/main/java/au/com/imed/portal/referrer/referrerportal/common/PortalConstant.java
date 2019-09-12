package au.com.imed.portal.referrer.referrerportal.common;

public final class PortalConstant {

	// Constants related to the model
	public static final String MODEL_KEY_FORM_MODEL = "fmodel";
	public static final String MODEL_KEY_ACTION_STATUS = "status";
	public static final String MODEL_KEY_SUCCESS_MSG = "successMsg";
	public static final String MODEL_KEY_ERROR_MSG = "errorMsg";
	public static final String KEY_RETRY_FLAG = "mayRetry";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";

	// Quick report related constatns
	public static final String PARAM_IMED_SECIRITY = "imsec";
	public static final String NO_REPORT_ALERT = "<div class=\"alert alert-warning\" role=\"alert\">Report is unavailable, not verified or not shared.</div>";

	// Temporal password for COMRAD
	public static String IMED_TEMPORAL_PASSWORD = "iMtmP2018pswd";


	public static final String HEADER_AUTHENTICATION = "Authentication";

	// Constants related to ldap and email symbols
	public static final String ATMARK_IN_LDAP = "-!-";
	public static final String ATMARK_IN_EMAIL = "@";

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static final String VISAGE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DISPLAY_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

	// Approver
	public static final String PARAM_ATTR_ACC_LOCKED = "ibm-pwdAccountLocked";
	public static final String PARAM_ATTR_FINALIZING_PAGER = "pager";
	public static final String PARAM_ATTR_VALUE_FINALIZING_PAGER = "Finalizing";

}
