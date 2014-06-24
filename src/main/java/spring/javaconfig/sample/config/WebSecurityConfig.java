package spring.javaconfig.sample.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("ADMIN").and()
			.withUser("user").password("user").roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers(
					"/",
					"/css/**",
					"/login",
					"/logout").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.and()
			.logout()
				.logoutRequestMatcher(logoutRequestMatcher())
				.invalidateHttpSession(true)
				.and()
			.csrf()
				.and()
			.exceptionHandling()
				.defaultAuthenticationEntryPointFor(
					ajaxAuthenticationEntryPoint(),
					ajaxRequestMatcher()
				);
	}

	@Bean
	public AuthenticationEntryPoint ajaxAuthenticationEntryPoint() {
	return new AuthenticationEntryPoint() {
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	};
	}

	@Bean
	public RequestMatcher ajaxRequestMatcher() {
	return new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");
	}

	@Bean
	public RequestMatcher logoutRequestMatcher() {
	return new AntPathRequestMatcher("/logout");
	}

}
