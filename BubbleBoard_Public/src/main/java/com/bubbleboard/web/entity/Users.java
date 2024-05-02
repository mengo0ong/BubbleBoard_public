package com.bubbleboard.web.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Entity
@Table(name = "users")
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "ID_SEQUENCE_GENERATOR", sequenceName = "SEQ_USER", initialValue = 1, allocationSize = 1)
	private int unum; // user seq
	
	// 컬럼명에 하단바가 있는 경우에는 컬럼 어노테이션을 사용해 정확한 네임을 지정해주어야한다c c.
	@Column(name="user_pwd") 
	private String userPwd; // 비밀번호
	private String email; // 이메일
	@Column(name = "name")
	private String nickName; // 닉네임 = 따로설정
	private String authority; //Role
	private String provider; // 소셜 로그인 ( 구글, 네이버, 카카오 ), 소셜 비연동시 null
	@Column(name="provider_id")
	private String providerId; // 소셜 id, 소셜 비연동시 null
	@Column(name="user_img")
	private String userImg; // 프로필이미지 = 따로설정
	@Column(name="sub_date")
	private java.sql.Date subDate; // 가입일자
	

}
