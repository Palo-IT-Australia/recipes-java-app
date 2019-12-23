package au.com.imed.portal.referrer.referrerportal;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AttachmentService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetOrderService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetPatientOrdersService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetPatientService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.ReportService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.SearchHospitalOrderSummaryService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.SearchOrdersService;

@Aspect
@Configuration
public class AspectReferrerAudit {
	private Logger logger = LoggerFactory.getLogger(AspectReferrerAudit.class);
	
  @Autowired
  private AuditService auditService;
  
  /**
   * Search, Report or all Break Glass requests
   */
  @AfterReturning("execution(* au.com.imed.portal.referrer.referrerportal.rest.visage.service.AVisageRestClientService+.doRestGet(..)) && args(userName,requestParams,..)")
  public void auditVisageResult(JoinPoint jp, String userName, Map<String, String> requestParams) {
    Object target = jp.getTarget();
    if( GetPatientOrdersService.class.isAssignableFrom(target.getClass()) || 
        SearchHospitalOrderSummaryService.class.isAssignableFrom(target.getClass()) ||
        SearchOrdersService.class.isAssignableFrom(target.getClass())) {
    	auditService.doAudit(target.getClass().getSimpleName().replace("Get", "").replace("Service", "").replace("Search", "").replace("Summary", "").replace("Pdf", ""), userName, requestParams);
    }    
  }

  /**
   * View Image VM and IV Audit
   */
  @AfterReturning("execution(public * au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService.generateUrl(..)) && args(userName,paramMap,order,..)")
  public void auditViewImageResult(JoinPoint jp, String userName, Map<String, String> paramMap, OrderDetails order) {
  	auditService.doAudit("Image", userName, paramMap, order);
  }
  
  @AfterReturning("execution(public * au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService.generateUrls(..)) && args(userName,paramMap,order,..)")
  public void auditViewImageIvResult(JoinPoint jp, String userName, Map<String, String> paramMap, OrderDetails order) {
  	auditService.doAudit("ImageIv", userName, paramMap, order);
  }
  

  @AfterReturning("execution(public * au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService.generateIvEvImageUrls(..)) && args(userName,paramMap,order,..)")
  public void auditViewImageIvEvResult(JoinPoint jp, String userName, Map<String, String> paramMap, OrderDetails order) {
  	auditService.doAudit("ImageIvEv", userName, paramMap, order);
  }
  
  
}
