package au.com.imed.portal.referrer.referrerportal.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.RequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.RequestAuditJPARepository;

@Component
public class PortalLogoutSuccessHandler implements LogoutSuccessHandler {
	private Logger logger = LoggerFactory.getLogger(PortalLogoutSuccessHandler.class);
	
	@Autowired
	private RequestAuditJPARepository requestAuditRepository;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String username = authentication.getName();
		logger.info("Logging out " + username);
		RequestAuditEntity entity = new RequestAuditEntity();
    entity.setAuditAt(new Date());
    entity.setBreakGlass("false");
    entity.setCommand("Logout");
    entity.setUsername(username);
    entity.setParameters("");
		requestAuditRepository.save(entity);
		
		response.sendRedirect(request.getContextPath() + "/login?logout");
	}

}
