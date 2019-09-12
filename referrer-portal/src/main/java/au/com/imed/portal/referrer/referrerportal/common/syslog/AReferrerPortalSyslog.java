package au.com.imed.portal.referrer.referrerportal.common.syslog;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class AReferrerPortalSyslog {
	private final Logger logger = LoggerFactory.getLogger(getLoggerClass());

	/**
	 * Name of class appearing on syslog to identify application and for logback.xml
	 * 
	 * @return class name
	 */
	protected abstract Class<?> getLoggerClass();

	public void log(final ReferrerEvent ev, final String msg, final String userName) {
		log(ev, msg, userName, null, null);
	}

	public void log(final ReferrerEvent ev, final String msg, final String userName,
			final Map<String, String> paramMap) {
		log(ev, msg, userName, paramMap, null);
	}

	public synchronized void log(final ReferrerEvent ev, final String msg, final String userName,
			final Map<String, String> paramMap, final Object object) {
		try {
			MDC.put(ReferrereEventConsts.PARAM_EVENT, ev.event());
			MDC.put(ReferrereEventConsts.PARAM_USERNAME, userName);
			for (String p : ev.params()) {
				MDC.put(p, getParamValue(p, paramMap, object));
			}
			logger.info(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			MDC.remove(ReferrereEventConsts.PARAM_EVENT);
			MDC.remove(ReferrereEventConsts.PARAM_USERNAME);
			for (String p : ev.params()) {
				MDC.remove(p);
			}
		}
	}

	private String getParamValue(final String param, final Map<String, String> paramMap, final Object object) {
		String val = "";
		boolean found = false;
		if (paramMap != null && paramMap.size() > 0) {
			if (paramMap.containsKey(param)) {
				val = paramMap.get(param);
				found = true;
			}
			if (!found && ReferrereEventConsts.PARAM_REQPARAMS.equals(param)) {
				val = buildParamString(paramMap);
				found = true;
			}
		}
		// Default break glass is false
		if (!found && ReferrereEventConsts.PARAM_BREAK_GLASS.equals(param)) {
			val = "false";
			found = true;
		}
		// Get from object
		if (!found && object != null) {
			val = getValueFromObject(param, object);
		}
		return val;
	}

	protected abstract String getValueFromObject(final String param, final Object object);

	private String buildParamString(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			Set<String> keyset = params.keySet();
			for (String key : keyset) {
				sb.append(key);
				sb.append("=");
				sb.append(params.get(key));
				sb.append("&");
			}
		}
		String ps = sb.toString();
		if (ps.endsWith("&")) {
			ps = ps.substring(0, ps.length() - 1);
		}
		return ps;
	}
}
