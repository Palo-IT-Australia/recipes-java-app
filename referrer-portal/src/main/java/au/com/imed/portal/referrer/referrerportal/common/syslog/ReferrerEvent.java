package au.com.imed.portal.referrer.referrerportal.common.syslog;

public enum ReferrerEvent {
	LOGIN_SUCCESS("Login successful", ReferrereEventConsts.NOPARAM),
	LOGIN_FAIL("Login failed", ReferrereEventConsts.NOPARAM),
	RESET_PSWD("Reset password", ReferrereEventConsts.NOPARAM),
	CHANGE_PSWD("Change password", ReferrereEventConsts.NOPARAM),
	UPDATE_DETAILS("Update account details", ReferrereEventConsts.NOPARAM),
	ACCEPT_TC("Accept T&C", ReferrereEventConsts.NOPARAM),
	TOKEN_SUCCESS("Generate access token successful (app login)", ReferrereEventConsts.NOPARAM),
	TOKEN_FAIL("Generate access token failed (app login)", ReferrereEventConsts.NOPARAM),
	TOKEN_REFRESH_SUCCESS("Generate access token successful (refresh token)", ReferrereEventConsts.NOPARAM),
	TOKEN_REFRESH_FAIL("Generate access token failed (refresh token)", ReferrereEventConsts.NOPARAM),

	SEARCH_ORDERS("Search for results", new String[] { ReferrereEventConsts.PARAM_REQPARAMS }),
	PATIENT("View patient",
			new String[] { ReferrereEventConsts.PARAM_BREAK_GLASS, ReferrereEventConsts.PARAM_PATIENT_URI,
					ReferrereEventConsts.PARAM_PATIENT_ID }),
	PATIENT_ORDERS("View patient orders",
			new String[] { ReferrereEventConsts.PARAM_BREAK_GLASS, ReferrereEventConsts.PARAM_PATIENT_URI,
					ReferrereEventConsts.PARAM_PATIENT_ID }),
	ORDER("View order",
			new String[] { ReferrereEventConsts.PARAM_ORDER_URI, ReferrereEventConsts.PARAM_BREAK_GLASS,
					ReferrereEventConsts.PARAM_PATIENT_ID }),
	REPORT_VIEW("View report",
			new String[] { ReferrereEventConsts.PARAM_REPORT_URI, ReferrereEventConsts.PARAM_BREAK_GLASS,
					ReferrereEventConsts.PARAM_PATIENT_ID }),
	REPORT_DOWNLOAD("Download report",
			new String[] { ReferrereEventConsts.PARAM_REPORT_URI, ReferrereEventConsts.PARAM_BREAK_GLASS,
					ReferrereEventConsts.PARAM_PATIENT_ID }),
	REFERRAL("View referral",
			new String[] { ReferrereEventConsts.PARAM_ATTACH_URI, ReferrereEventConsts.PARAM_BREAK_GLASS,
					ReferrereEventConsts.PARAM_PATIENT_ID }),
	IMAGE("View images", new String[] { ReferrereEventConsts.PARAM_ORDER_URI, ReferrereEventConsts.PARAM_BREAK_GLASS,
			ReferrereEventConsts.PARAM_ACC_NUM, ReferrereEventConsts.PARAM_PATIENT_ID });

	private String event;
	private String[] params;

	private ReferrerEvent(String event, String[] params) {
		this.event = event;
		this.params = params;
	}

	public String event() {
		return this.event;
	}

	public String[] params() {
		return this.params;
	}
}
