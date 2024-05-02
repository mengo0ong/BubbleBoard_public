package com.bubbleboard.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BCryptPasswordEncoderConfig {
	@Bean // AuthSvc에서 @Autowired 어노테이션을 사용하여 엔코더를 쓰기 위해서는 configuration내에서 Bean등록을 해야한다.
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
