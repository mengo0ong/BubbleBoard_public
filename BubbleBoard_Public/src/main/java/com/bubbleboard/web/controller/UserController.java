package com.bubbleboard.web.controller;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.bubbleboard.web.entity.Users;
import com.bubbleboard.web.oauth.PrincipalDetails;
import com.bubbleboard.web.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("buser")
public class UserController {
	
	@Autowired
	UserService svc;
	
	@Autowired
	ResourceLoader resourceloader;
	
	@GetMapping("login")
	public String loginForm(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "exception", required = false) String exception,
			Model model) {
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);
		return "loginForm";
	}
	
	
	
	@GetMapping("signup")
	public String signUpForm() {
		return "signUpForm";
	}
	
	@PostMapping("signup")
	public String signUpResult(Users user, Model m) {
		boolean result = svc.userSignUp(user);
		if(result) {
			m.addAttribute("user", user);
			return "regist";
		}
		return "redirect:/buser/signup";
	}
	
	@GetMapping("sign")
	public String sign() {
		return "regist";
	}
	
	@PostMapping("emailcheck")
	@ResponseBody
	public Map<String, Object> emailCheck(@RequestParam String email) {
		Map<String, Object> map = new HashMap<>();
		map.put("result", svc.emailDuplTest(email));		
		return map;
	}
	
	// 로그아웃
	@GetMapping("logout")
	public String getMethodName(SessionStatus status) {
		status.setComplete();
		return "redirect:/bboard/main";
	}
	
	//마이페이지
	@GetMapping("mypage")
	public String mypage(@RequestParam int unum, Model m) {
		m.addAttribute("user", svc.getUser(unum));
		return "mypage";
	}
	
	// 이미지 바이트 배열로 불러오기
	@GetMapping("images/{userImg}")
	@ResponseBody
	public byte[] img(@PathVariable("userImg") String userImg){

		try {
			Path uploadPath = Paths.get("src/main/resources/static/images");
			String absolutePath = uploadPath.toAbsolutePath().toString();
			
			Resource resource = resourceloader.getResource(absolutePath+"/"+userImg);
			InputStream in = resource.getInputStream();
			return IOUtils.toByteArray(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	// 이미지 업데이트
	@PostMapping("userimgupdate")
	@ResponseBody
	public Map<String, Object> imgUpdate(@AuthenticationPrincipal PrincipalDetails user, 
			@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "unum") int unum){
		Map<String, Object> map = new HashMap<String, Object>();
		
		boolean result = svc.imgUpdate(file, unum);
		map.put("result", result);
		map.put("unum", unum);
		
		sessionUpdate(user, unum);
		return map;
	}
	
	// 닉네임 업데이트
	@PostMapping("usernicknamechange")
	@ResponseBody
	public Map<String, Object> nicknameUpdate(@AuthenticationPrincipal PrincipalDetails user, 
			@RequestParam(value = "nickName") String nickname,
			@RequestParam(value = "unum") int unum){
		Map<String, Object> map = new HashMap<String, Object>();
		 
		boolean result = svc.nicknameUpdate(nickname, unum);
		map.put("result", result);
		map.put("unum", unum);
		
		sessionUpdate(user, unum);
		return map;
	} 
	
	private void sessionUpdate(@AuthenticationPrincipal PrincipalDetails user, int unum) {
		// 닉네임이 업데이트된 사용자 정보를 가져옵니다.
	    user.getUser().setNickName(svc.getUser(unum).getNickName());
	    
	    Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
	            user, // 변경된 사용자 객체
	            null, // 비밀번호 정보는 사용하지 않음
	            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")) // 변경된 사용자의 권한 정보
	        );
	    
	    SecurityContextHolder.getContext().setAuthentication(newAuthentication);

	   
	}
	
	// 계정 탈퇴
	@GetMapping("/unlink/{provider}")
	@Transactional
    public String unlink(HttpSession session, HttpServletResponse response, HttpServletRequest request, 
                         @AuthenticationPrincipal PrincipalDetails user, @PathVariable(value = "provider", required = false) String provider) {
        log.info("클라에서 전달한 provider: " + provider);
        String access_Token = (String) session.getAttribute("access_token");
        log.info("세션에서 가져온 엑세스토큰: " + access_Token);
        boolean userDel = false;
        boolean unlinked = false;
        if (provider == null) {
            userDel = svc.deletedUser(user.getUser());
            session.invalidate();
        } else {
            if (access_Token != null) {
                if (provider.equals("kakao")) {
                    unlinked = svc.kakaoUnlink(access_Token);
                } else if (provider.equals("naver")) {
                    unlinked = svc.naverUnlink(access_Token);
                } else if (provider.equals("google")) {
                    unlinked = svc.googleUnlink(access_Token);
                }
                
                if (unlinked) {
                    userDel = svc.deletedUser(user.getUser());
                    removeCookies(response, request);
                    
                }
            }
        }
        
        if(userDel) {
            // Spring Security 로그아웃
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        }
        
        return userDel ? "redirect:/bboard/main" : "redirect:/buser/mypage?unum=" + user.getUser().getUnum();
    }
	
	private void removeCookies(HttpServletResponse response, HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	        	cookie.setDomain("localhost");
	        	cookie.setHttpOnly(true);
	            cookie.setMaxAge(0);
	            cookie.setPath("/");

	            response.addCookie(cookie);
	        }
	    }
	}

	
}
