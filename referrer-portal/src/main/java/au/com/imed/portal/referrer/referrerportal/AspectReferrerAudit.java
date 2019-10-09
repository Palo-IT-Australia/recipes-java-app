package au.com.imed.portal.referrer.referrerportal;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.RequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.RequestAuditJPARepository;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AttachmentService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetOrderService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetPatientOrdersService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.ReportService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.SearchOrdersService;

@Aspect
@Configuration
public class AspectReferrerAudit {
  private static final String BREAK_GLASS = "breakGlass";
  
  @Autowired
  private RequestAuditJPARepository repository;
  
  /**
   * Search, Report or all Break Glass requests
   */
  @AfterReturning("execution(* au.com.imed.portal.referrer.referrerportal.rest.visage.service.AVisageRestClientService+.doRestGet(..)) && args(userName,requestParams,..)")
  public void auditVisageResult(JoinPoint jp, String userName, Map<String, String> requestParams) {
    Object target = jp.getTarget();
    if("true".equalsIgnoreCase(requestParams.get(BREAK_GLASS)) || 
    		GetReferrerService.class.isAssignableFrom(target.getClass()) || 
        ReportService.class.isAssignableFrom(target.getClass()) || 
        GetOrderService.class.isAssignableFrom(target.getClass()) || 
        GetPatientOrdersService.class.isAssignableFrom(target.getClass()) || 
        AttachmentService.class.isAssignableFrom(target.getClass()) || 
        SearchOrdersService.class.isAssignableFrom(target.getClass())) {
      doAudit(target.getClass().getSimpleName().replace("Get", "").replace("Service", "").replace("Pdf", ""), userName, requestParams);
    }    
  }

  /**
   * View Image VM and IV Audit
   */
  @AfterReturning("execution(public * au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService.generateUrl(..)) && args(userName,paramMap,..)")
  public void auditViewImageResult(JoinPoint jp, String userName, Map<String, String> paramMap) {
    doAudit("Image", userName, paramMap);
  }
  
  @AfterReturning("execution(public * au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService.generateUrls(..)) && args(userName,paramMap,..)")
  public void auditViewImageIvResult(JoinPoint jp, String userName, Map<String, String> paramMap) {
    doAudit("ImageIv", userName, paramMap);
  }
  

  @AfterReturning("execution(public * au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService.generateIvEvImageUrls(..)) && args(userName,paramMap,..)")
  public void auditViewImageIvEvResult(JoinPoint jp, String userName, Map<String, String> paramMap) {
    doAudit("ImageIvEv", userName, paramMap);
  }
  
  private void doAudit(final String command, final String username, final Map<String, String> params) {
    RequestAuditEntity entity = new RequestAuditEntity();
    entity.setAuditAt(new Date());
    entity.setBreakGlass(params.containsKey(BREAK_GLASS) ? params.get(BREAK_GLASS) : "false");
    entity.setCommand(command);
    entity.setUsername(username);
    entity.setParameters(buildParamString(params));
    repository.save(entity);    
  }

  private String buildParamString(Map<String, String> params) {
    StringBuffer sb = new StringBuffer();
    if(params != null) {
      Set<String> keyset = params.keySet();
      for(String key : keyset) {
        sb.append(key);
        sb.append("=");
        sb.append(params.get(key));
        sb.append("&");
      }
    }
    String ps = sb.toString();
    if(ps.endsWith("&")) {
      ps = ps.substring(0, ps.length() - 1);
    }
    return ps;
  }
}
