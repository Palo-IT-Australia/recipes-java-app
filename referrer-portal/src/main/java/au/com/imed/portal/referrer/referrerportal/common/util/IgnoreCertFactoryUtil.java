package au.com.imed.portal.referrer.referrerportal.common.util;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * This should not be necessary after proper https servers provided
 *
 */
public class IgnoreCertFactoryUtil {
  public static HttpComponentsClientHttpRequestFactory createFactory() throws Exception {
    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
        .loadTrustMaterial(null, new TrustStrategy() {
          @Override
          public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
              throws java.security.cert.CertificateException {
            return true;
          }
        })
        .build();

    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(csf)
        .build();

    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();

    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }

}
