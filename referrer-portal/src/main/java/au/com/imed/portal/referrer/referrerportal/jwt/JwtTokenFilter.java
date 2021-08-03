package au.com.imed.portal.referrer.referrerportal.jwt;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        String authenticationHeader = request.getHeader("Authorization");

        if (!StringUtils.isEmpty(authenticationHeader)) {
            try {
                String userName = AuthenticationUtil.getAuthenticatedUserName(authenticationHeader);

                if (!StringUtils.isEmpty(userName)) {
                    if (AuthenticationUtil.checkAccessToken(authenticationHeader).equals(userName)) {
                        var authorities = AuthenticationUtil.getAuthorities(authenticationHeader);
                        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userName, null, authorities);

                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // After setting the Authentication in the context, we specify
                        // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chain.doFilter(request, response);
    }

}
