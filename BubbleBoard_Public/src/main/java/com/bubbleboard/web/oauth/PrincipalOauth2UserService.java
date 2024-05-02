package com.bubbleboard.web.oauth;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bubbleboard.web.entity.*;
import com.bubbleboard.web.repository.UsersRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	private final UsersRepository userRepo;
	
	@Autowired
	private AuthSvc svc;
	
	private final BCryptPasswordEncoder enc;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("getAttributes: {}", oAuth2User.getAttributes());
		
		OAuth2UserInfo oAuth2UserInfo = null;
		
		String provider = userRequest.getClientRegistration().getRegistrationId();
		if(provider.equals("google")) {
			log.info("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		}else if (provider.equals("kakao")) {
			log.info("카카오 로그인 요청");
			oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
		}else if (provider.equals("naver")) {
			log.info("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
		}
		
		String providerId = oAuth2UserInfo.getProviderId();
		String email = oAuth2UserInfo.getEmail();
		String name = oAuth2UserInfo.getName();
		
		// DB에 해당 user가 있는지 없는지 확인
		Users u = userRepo.findByEmail(email);
		
		Users user;
		if(u==null) { // DB에 유저가 없으면 새로운 객체를 만들어 DB에 저장하기
			user = new Users();
			user.setNickName(name);
			user.setEmail(email);
			user.setProvider(provider);
			user.setProviderId(providerId);
			user.setAuthority("ROLE_USER");
			user.setSubDate(new java.sql.Date(System.currentTimeMillis()));
			boolean signUp = svc.snsSignUp(user);
			if(!signUp) throw new OAuth2AuthenticationException("회원가입 실패");
		}else {
			user=u;
		}
		
		// 세션에 토큰 저장하기
	    HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
	    session.setAttribute("access_token", userRequest.getAccessToken().getTokenValue());
		
		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}
	
	
}
