package com.bubbleboard.web.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.bubbleboard.web.entity.Users;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

	private Users user;
	private Map<String, Object> attributes;
	
	public PrincipalDetails(Users user) {
		super();
		this.user = user;
	}	
	
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getAuthority();
			}
		});
		
		return collect;
	}

	@Override
	public String getName() {
		return user.getNickName();
	}

	@Override
	public String getPassword() {
		return user.getUserPwd();
	}

	@Override
	public String getUsername() {
		return user.getNickName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	
	//유저 가져오기
	public Users getUser() {
		return user;
	}
	
	//이미지 가져오기
	public String getUserImg() {
		return user.getUserImg();
	}

}
