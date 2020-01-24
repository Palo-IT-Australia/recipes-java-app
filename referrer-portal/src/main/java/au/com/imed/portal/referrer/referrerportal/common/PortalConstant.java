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

	// ldap references
	public final static String DOMAIN_REFERRER = "ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_STAGING = "ou=Staging,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_PORTAL = "ou=Portal,ou=Applications,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_APPLICATIONS = "ou=Applications,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_GLOBAL = "dc=mia,dc=net,dc=au";
	public final static String DOMAIN_BUSINESS_UNITS = "ou=Business Units,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_STAGING_PACS_USERS = "ou=Staging PACS Users,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_IMED_PACS_USERS = "ou=IMED PACS Users,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_PACS_USERS = "ou=PACS Users,dc=mia,dc=net,dc=au";
	public final static String DOMAIN_PATIENTS = "ou=Patients,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au";

	// Terms and Conditions
	public final static String TERMS_AND_CONDITIONS_SHOW = "show";
	public final static String TERMS_AND_CONDITIONS_HIDE = "hide";

	// Clinic finder related constants
	public static final double FINDER_RADIUS = 100.00;
	public static final int MAX_CLINICS = 16;
	
	// Auto account validation statuses
	public static final String VALIDATION_STATUS_INVALID = "invalid"; // any invalid found
	public static final String VALIDATION_STATUS_PASSED = "passed";  // 1st to check ahpra
	public static final String VALIDATION_STATUS_VALID = "valid"; // 2nd acnt created
	public static final String VALIDATION_STATUS_NOTIFIED = "notified"; // 3rd notified by emails

	public static final String REP_VISAGE_USER = "huehara";
}
