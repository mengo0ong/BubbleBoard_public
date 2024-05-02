package com.bubbleboard.web.oauth;

import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2UserInfo {
	
	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
}
