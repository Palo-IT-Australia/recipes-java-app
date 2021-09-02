package au.com.imed.portal.referrer.referrerportal;

import au.com.imed.portal.referrer.referrerportal.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(1)
@EnableWebSecurity
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${imed.api-v2.prefix}/**")
    private String apiV2Prefix;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher(apiV2Prefix)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(apiV2Prefix).permitAll()
                .antMatchers(apiV2Prefix).permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .addFilterAfter(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }
}
