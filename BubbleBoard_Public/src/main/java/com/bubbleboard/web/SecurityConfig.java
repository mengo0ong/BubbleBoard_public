package com.bubbleboard.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bubbleboard.web.oauth.PrincipalOauth2UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private final PrincipalOauth2UserService poUserSvc;
	
	private final AuthenticationFailureHandler customFailureHandler;
	
	// static 관련 설정 무시
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (webSecurity) -> webSecurity.ignoring().requestMatchers("/css/**", "/js/**", "/upload/**");
	}
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
    	httpSecurity.headers().frameOptions().sameOrigin();
    	httpSecurity.authorizeHttpRequests(authz -> authz
    			// 권한 부여
    			.requestMatchers("/", "/buser/login", "/bboard/main", "/login").permitAll()
    			.requestMatchers("/bboard/add", "/bboard/addreply", "/bboard/comment", "/bboard/nestedreply").hasAnyRole("USER","ADMIN")
    			.requestMatchers("/bboard/edit").hasAnyRole("USER","ADMIN")
    			.requestMatchers("/buser/mypage").hasAnyRole("USER","ADMIN")
    			.anyRequest().permitAll())
    			// 쿠키검사
    			.csrf(CsrfConfigurer -> CsrfConfigurer.disable())
    			// 로그인 검사
    			.formLogin(LoginConfig -> LoginConfig.loginPage("/buser/login")
    					.loginProcessingUrl("/buser/doLogin")
    					.defaultSuccessUrl("/bboard/main", true)
    					.failureHandler(customFailureHandler)
    					// 기본적으로 name, password라는 이름으로 지정되어있으니 컬럼명이 다르면 Parameter이름을 지정해주어야한다.
    					.usernameParameter("email")
    					.passwordParameter("userPwd")
    					.permitAll())
    			.logout(LogoutConfigurer -> LogoutConfigurer.logoutRequestMatcher(new AntPathRequestMatcher("/buser/logout"))
    					.logoutSuccessUrl("/bboard/main")
    					.invalidateHttpSession(true)
    					.deleteCookies("JSESSIONID")
    					.addLogoutHandler((request, response, authentication) -> {
    						response.setHeader("Cache-Controll", "no-cache, no-store, must-revalidate");
    						response.setHeader("Pragma", "no-cache");
    						response.setHeader("Expires", "0");
    					})
    					.permitAll())
    			.exceptionHandling(exConfig -> exConfig.accessDeniedPage("/bboard/denied"))
    			.oauth2Login(t -> t.loginPage("/buser/login")
    					.defaultSuccessUrl("/bboard/main")
    					.failureHandler(customFailureHandler)
    					.userInfoEndpoint(r -> r.userService(poUserSvc)));
    	return httpSecurity.build();
    }
}