package com.bubbleboard.web.oauth;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes;
	
	@Override
	public String getProviderId() {
		return (String)((Map)attributes.get("response")).get("id");
	}

	@Override
	public String getProvider() {
		return "naver";
	}

	@Override
	public String getEmail() {
		return (String)((Map)attributes.get("response")).get("email");
	}

	@Override
	public String getName() {
		return (String)((Map)attributes.get("response")).get("name");
	}


}
