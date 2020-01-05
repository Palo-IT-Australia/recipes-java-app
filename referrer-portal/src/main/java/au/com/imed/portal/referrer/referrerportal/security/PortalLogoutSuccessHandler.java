package au.com.imed.portal.referrer.referrerportal.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;

@Component
public class PortalLogoutSuccessHandler implements LogoutSuccessHandler {
	private Logger logger = LoggerFactory.getLogger(PortalLogoutSuccessHandler.class);
	
	@Autowired
	private AuditService auditService;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String username = authentication.getName();
		logger.info("Logging out user : " + username);
		auditService.doAudit("Logout", username);;
		
		response.sendRedirect(request.getContextPath() + "/login?logout");
	}

}
