package com.bubbleboard.web.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.bubbleboard.web.entity.Users;

@EnableJpaRepositories
public interface UsersRepository extends JpaRepository<Users, Integer>{
//	JpaRepository<> 괄호 안에 entity가 될 클래스와, PK(@ID 어노테이션)자료형을 명시해주어야 한다.
	Users findByEmail(String email);
	
	
}
