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

	public static String PACS_URL;
	public static String PACS_AUTH_TOKEN;
	public static String [] PACS_AUTH_SERVERS;
	public static String PACS_FALLBACK;
	public static String PACS_SIGNATURE_KEY;
	
	@Value("${imed.pacs.signaturekey}")
	public void setPacsSignatureKey(String key) {
		PACS_SIGNATURE_KEY = key;
	}
	
	@Value("${imed.pacs.fallback}")
	public void setPacsFallback(String user) {
		PACS_FALLBACK = user;
	}

	@Value("${imed.pacs.url}")
	public void setPacsUrl(String url) {
		PACS_URL = url;
	}
	
	@Value("${imed.pacs.token}")
	public void setPacsAuthToken(String key) {
		PACS_AUTH_TOKEN = key;
	}
	
	@Value("${imed.pacs.servers}")
	public void setPacsAuthToken(String [] servers) {
		PACS_AUTH_SERVERS = servers;
	}

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
