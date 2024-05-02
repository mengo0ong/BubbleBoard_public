package com.bubbleboard.web.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NaverOauth {
	
	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String naverClientId;
	
	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String naverClientSecert;

	
	public String getNaverClientId() {
		return naverClientId;
	}


	public String getNaverClientSecert() {
		return naverClientSecert;
	}
	
}
