package com.bubbleboard.web.service;


import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.bubbleboard.web.entity.Users;
import com.bubbleboard.web.oauth.NaverOauth;
import com.bubbleboard.web.oauth.PrincipalDetails;
import com.bubbleboard.web.repository.UsersRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService implements UserDetailsService{

	@Autowired
	UsersRepository userRepo; // mapper.xml과 비슷하나 간단한 crud만 가능
	
	@Autowired
	NaverOauth naverOauth;
	
	@Autowired
    private BCryptPasswordEncoder enc;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Users u = userRepo.findByEmail(email);
		if(u==null) throw new UsernameNotFoundException("no");
		return new PrincipalDetails(u); 
	}
	

	public boolean userSignUp(Users user) {
		if(emailDuplTest(user.getEmail())) return false;
		user.setAuthority("ROLE_USER");
		user.setSubDate(new java.sql.Date(System.currentTimeMillis()));
		user.setUserPwd(enc.encode(user.getUserPwd()));
		return userRepo.save(user) != null; // null이면 false
	}
	
	
	// 이메일 중복 검사
	public boolean emailDuplTest(String email) {
		return userRepo.findByEmail(email) != null; // true : 이메일 있음
	}
	
	// 이메일로 user 찾기
	public Users findUser(String email) throws Exception {
		Users user = userRepo.findByEmail(email);
		return user;
	}
	
	// 아이디 찾기 -> 변환
	public String findId(String user_id) {
		int length = user_id.length();
		
		StringBuilder sb = new StringBuilder(user_id);
		for(int i=4; i<length; i++) {
			sb.setCharAt(i, '*');
		}
		user_id=sb.toString();
		return user_id;
	}


	public Users getUser(int unum) {
		return userRepo.findById(unum).get();
	}


	public boolean imgUpdate(MultipartFile file, int unum) {
		// 절대경로 구하기
		Path uploadPath = Paths.get("src/main/resources/static/images");
		String absolutePath = uploadPath.toAbsolutePath().toString();
		
		UUID uuid = null;
		try {
			// 파일 이름 암호화
			Users u = userRepo.findById(unum).get();
			String[] f = file.getOriginalFilename().trim().split("\\.");
			String userImg = f[0]+uuid.randomUUID()+"."+f[1];
			
			// 파일 저장
			file.transferTo(new File(absolutePath+"/"+userImg));
			
			// 유저 정보 업데이트
			u.setUserImg(userImg);
			return userRepo.save(u) != null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public boolean nicknameUpdate(String nickname, int unum) {
		Users u = userRepo.findById(unum).get();
		u.setNickName(nickname);
		
		return userRepo.save(u) != null;
	}


	public boolean deletedUser(Users user) {
		userRepo.delete(user);
		return userRepo.findByEmail(user.getEmail()) == null;
	}

	@Transactional
	public boolean kakaoUnlink(String access_Token) {
		String requestURL = "https://kapi.kakao.com/v1/user/unlink";
		
		
		try {
			URI uri = URI.create(requestURL);
			URL url = uri.toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer "+access_Token);
			conn.getResponseCode(); // 실제 요청을 보내야함!
			
			conn.getResponseMessage();
			log.info("응답: " + conn.getResponseMessage());
			
			return conn.getResponseMessage().equals("OK") ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		return false;
	}

	@Transactional
	public boolean naverUnlink(String access_Token) {
		
		try {
			// 네이버는 requestProperty로 전달되지 않는 것 같다..
			URI uri = new URI("https://nid.naver.com/oauth2.0/token?"+
								"grant_type=delete&client_id="+naverOauth.getNaverClientId()+"&client_secret="+naverOauth.getNaverClientSecert()+
								"&access_token="+access_Token);
			URL url = uri.toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.getResponseCode(); 
			log.info("REST API : "+ conn.getURL());
			
			log.info("응답 내용: "+ conn.getResponseMessage());
			return conn.getResponseMessage().equals("OK") ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	@Transactional
	public boolean googleUnlink(String access_Token) {
			
		try {
			// 네이버는 그냥 전달해주어도 됐는데 구글은 이런 방식으로 전달해주어야 인식이 되는 것 같다.
			URI uri = new URI("https://oauth2.googleapis.com/revoke?token");
			URL url = uri.toURL();
			
	        // 연결 설정
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST"); // POST 요청 설정
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Content-Type 설정
	        conn.setRequestProperty("Content-Length", String.valueOf(access_Token.length())); // Content-Length 설정
	        
	        // 요청 본문 작성
	        String postParams = "token=" + URLEncoder.encode(access_Token, "UTF-8");
	        
	        // 요청 본문 전송
	        conn.setDoOutput(true);
	        try (OutputStream os = conn.getOutputStream()) {
	            byte[] input = postParams.getBytes("utf-8");
	            os.write(input, 0, input.length);           
	        }
	        
			conn.getResponseCode(); 
			log.info("REST API : "+ conn.getURL());
			
			log.info("응답 내용: "+ conn.getResponseMessage());
			return conn.getResponseMessage().equals("OK") ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
}
