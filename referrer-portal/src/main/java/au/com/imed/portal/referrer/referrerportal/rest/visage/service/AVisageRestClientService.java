package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * 
 * @param <S> output result from rest
 * @param <U> request object including parameters
 */
public abstract class AVisageRestClientService<U, S> {
  //private static final String VISAGE_ROOT = "http://10.100.39.140:8181/jris/portal/v1/";
  private static final String VISAGE_HEADER_USER = "RISUser";
  
  @Value("${imed.visage.url}")
  private String visageBaseUrl;
  
  public ResponseEntity<S> doRestGet(String userName, U requestParams, Class<S> clz) {
    return doRest(userName, requestParams, clz, HttpMethod.GET);
  }
  
  private ResponseEntity<S> doRest(String userName, U requestParams, Class<S> clz, HttpMethod method) {
    ResponseEntity<S> respEntity = null;
    if(userName != null && userName.length() > 0) {
      RestTemplate restTemplate = new RestTemplate();
      final HttpHeaders headers = new HttpHeaders();
      headers.add(VISAGE_HEADER_USER, userName);
      addHeaderParams(headers);
      HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
      try {
        respEntity = restTemplate.exchange(generateUri(requestParams), method, httpEntity, clz);
      } 
      catch(HttpStatusCodeException ex) 
      {
        respEntity = new ResponseEntity<S>(ex.getStatusCode());
      }
      catch(RestClientException ex) 
      {
        respEntity = new ResponseEntity<S>(HttpStatus.BAD_REQUEST);
      }
    }
    else 
    {
      respEntity = new ResponseEntity<S>(HttpStatus.UNAUTHORIZED);
    }
    return respEntity;
  }
  
  protected void addHeaderParams(HttpHeaders headers) {
    // NOP as default
  }
  
  abstract protected String getCommandPath(U requestParams);
  
  abstract protected void setParameters(UriComponentsBuilder builder, U requestParams);
  
  private URI generateUri(U requestParams) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(visageBaseUrl + getCommandPath(requestParams));
    setParameters(builder, requestParams);
    URI rUri = builder.build().encode().toUri();
    System.out.println("generateUri() URI = " + rUri);
    return rUri;
  }
  
}
