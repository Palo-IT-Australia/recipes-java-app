package au.com.imed.portal.referrer.referrerportal.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalVals {
	public static final String HEADER_AUTHENTICATION = "Authorization";

	public static String CARESTREAM_PARAM_FORMAT;
	public static String CARESTREAM_PARAM_FORMAT_SUID;
	public static String CARESTREAM_URL;
	public static String CARESTREAM_KEY;

	@Value("${imed.vuemotion.enckey}")
	public void setCarestreamKey(String key) {
		CARESTREAM_KEY = key;
	}
	
	@Value("${imed.vuemotion.url}")
	public void setCarestreamUrl(String url)
	{
		CARESTREAM_URL = url;
	}

	@Value("${imed.vuemotion.paramformat}")
	public void setCarestreamParamFormat(String fmt)
	{
		CARESTREAM_PARAM_FORMAT = fmt;
	}
	
	@Value("${imed.vuemotion.paramformat.suid}")
	public void setCarestreamParamFormatSuid(String fmt)
	{
		CARESTREAM_PARAM_FORMAT_SUID = fmt;
	}
}
