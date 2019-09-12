package au.com.imed.portal.referrer.referrerportal.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;

public class StringConversionUtil {

	public static String toAusDateQuick(final String ori) {
		String dt = "-- Invalid Date Format --";
		if (ori != null) {
			final String[] elms = ori.split("-");
			if (elms.length == 3) {
				dt = elms[2] + "/" + elms[1] + "/" + elms[0];
			}
		}
		return dt;
	}

	public static String toAusDate(final String ori) {
		String converted;
		SimpleDateFormat sdf = new SimpleDateFormat(PortalConstant.VISAGE_DATE_FORMAT);
		try {
			Date d = sdf.parse(ori);
			sdf.applyPattern(PortalConstant.DISPLAY_DATE_FORMAT);
			converted = sdf.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
			converted = ori;
		}
		return converted;
	}

	public static String toAusDate(final Date date) {
		return new SimpleDateFormat(PortalConstant.DISPLAY_DATE_FORMAT).format(date);
	}

	public static String toAusDateTime(final Date date) {
		return new SimpleDateFormat(PortalConstant.DISPLAY_DATE_TIME_FORMAT).format(date);
	}

	public static final String MINS_DEFAULT = String.valueOf(60 * 2); // 2 hours
	public static final String PUSH_MINS_DEFAULT = "5"; // push 5 minutes

	public static Date getDateByMinutesDiff(final String minutes, final boolean subtraction) {
		int amount = 0;
		if (minutes != null && minutes.length() > 0) {
			try {
				amount = Integer.parseInt(minutes);
			} catch (Exception ex) {
				amount = 0;
			}
		}
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, amount * (subtraction ? -1 : 1));
		return now.getTime();
	}

}
