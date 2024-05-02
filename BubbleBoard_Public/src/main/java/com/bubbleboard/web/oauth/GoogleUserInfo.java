package com.bubbleboard.web.oauth;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {
	private Map<String, Object> attributes;

	// 로그인 된 소셜에서 정보를 빼내오고 객체로 저장
	
	@Override
	public String getProviderId() {
		return (String)attributes.get("sub");
	}

	@Override
	public String getProvider() {
		return "google";
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getName() {
		return (String)attributes.get("name");
	}
	
}
