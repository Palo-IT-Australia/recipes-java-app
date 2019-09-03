package au.com.imed.portal.referrer.referrerportal.sms;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoFaxSmsService {
  private static final String SMS_ROOT_URL = "https://restful-api.gofax.com.au/v1/SMS";
  private static final String SMS_TOKEN = "07c51937-50cb-4185-86e8-783fcdf1f178";
  private static final Map<String, String> SMS_PARAM_MAP = new HashMap<>();
  static {
    SMS_PARAM_MAP.put("token", SMS_TOKEN);
  }

//  public static void main(String args[]) throws Exception {
//    new PatientSmsService().send(new String [] {"0437118213"}, "passcode=test0123456");
//  }
  
  public ResponseEntity<SmsSendResponse> send(final String [] numbers, final String msg) throws Exception 
  {
    SmsSendRequest body = new SmsSendRequest();
    body.setTo(numbers);
    body.setBody(msg);
    body.setClientReference("imed");
    body.setReplyTo("");
    body.setFrom("");
    RequestEntity<SmsSendRequest> requestEntity = new RequestEntity<>(body, HttpMethod.PUT, getSmsUrl(SMS_PARAM_MAP, ""));
    RestTemplate restTemplate = new RestTemplate(getProxyFactory());
    return restTemplate.exchange(requestEntity, SmsSendResponse.class);
  }
  
  private HttpComponentsClientHttpRequestFactory getProxyFactory() throws Exception {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial(null, new TrustStrategy() {
          @Override
          public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
              throws java.security.cert.CertificateException {
            return true;
          }
        })
        .build();
    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
        .setProxy(new HttpHost("10.100.41.21", 3128, "http"))
        .build();
    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }

  private URI getSmsUrl(Map<String, String> requestParams, final String command) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SMS_ROOT_URL + command);
    for(String p : requestParams.keySet()) {
      builder.queryParam(p, requestParams.get(p));
    }
    URI rUri = builder.build().encode().toUri();
    //System.out.println("getSmsUrl() URI = " + rUri);
    return rUri;
  }
}
