package com.bubbleboard.web.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bubbleboard.web.entity.Users;
import com.bubbleboard.web.repository.UsersRepository;

@Service
public class AuthSvc {

	@Autowired
    private UsersRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder enc;
	
	public boolean snsSignUp(Users user) {
		String encPwd = enc.encode(user.getEmail());
		user.setUserPwd(encPwd);
		return userRepo.save(user) != null;
	}

}
