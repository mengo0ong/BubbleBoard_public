package com.bubbleboard.web.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import com.bubbleboard.web.entity.Board;
import com.bubbleboard.web.entity.Comment;
import com.bubbleboard.web.entity.Users;
import com.bubbleboard.web.oauth.PrincipalDetails;
import com.bubbleboard.web.service.BoardService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("bboard")
public class BoardController {
	
	@Autowired
	private BoardService svc;
	
	@GetMapping("main")
	public String mainPage(Model m) {
		m.addAttribute("board", svc.getBoardList());
		return "main";
	}
	
	@GetMapping("add")
	public String addPostForm() {
		return "addPostForm";
	}
	
	@PostMapping("add")
	@ResponseBody
	public Map<String,Object> addPost(@RequestParam(value = "files", required = false) MultipartFile[] mfiles, 
			HttpServletRequest req, @AuthenticationPrincipal PrincipalDetails userDetail, Board b) {
		Map<String, Object> map = new HashMap<String, Object>();
		String msg = null;
		try {
			// 유저 찾기
			Users user = userDetail.getUser();
			
			// board 정보에 유저 넣기
			b.setUser(user);
			
			// 절대경로
			// 폴더의 절대경로를 찾기 위해서는 Resourceloader를 사용하는게 아니고 Path로 경로를 가져와야한다.
			Path uploadPath = Paths.get("src/main/resources/static/upload/");
			String absolutePath = uploadPath.toAbsolutePath().toString();
			
			// 파일 저장
			Map<String, Object> resultMap = svc.addPost(mfiles, b, absolutePath);
			
			if((boolean)resultMap.get("result")) {
				// 결과 전송
				msg = String.format("게시글 저장성공 /파일(%d)개 (작성자: %s)", mfiles.length, b.getUser().getNickName());
				map.put("result", true);
				map.put("msg", msg);
				map.put("bnum", resultMap.get("bnum"));
			}else {
				msg = "파일 저장 실패";
				map.put("result", false);
				map.put("msg", msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@GetMapping("addreply")
	public String addReplyForm(Model m, @RequestParam int pbnum) {
		m.addAttribute("pbnum", pbnum);
		return "addPostForm";
	}
	
	@PostMapping("addreply")
	@ResponseBody
	public Map<String,Object> addReply(@RequestParam(value = "files", required = false) MultipartFile[] mfiles, 
			HttpServletRequest req, @AuthenticationPrincipal PrincipalDetails userDetail, Board b) {
		log.info(Integer.toString(b.getPBoard()));
		Map<String, Object> map = new HashMap<String, Object>();
		String msg = null;
		try {
			// 유저 찾기
			Users user = userDetail.getUser();
			
			// board 정보에 유저 넣기
			b.setUser(user);
			
			// 절대경로
			// 폴더의 절대경로를 찾기 위해서는 Resourceloader를 사용하는게 아니고 Path로 경로를 가져와야한다.
			Path uploadPath = Paths.get("src/main/resources/static/upload/");
			String absolutePath = uploadPath.toAbsolutePath().toString();
			
			// 파일 저장
			Map<String, Object> resultMap = svc.addPost(mfiles, b, absolutePath);
			
			if((boolean)resultMap.get("result")) {
				// 결과 전송
				map.put("result", true);
				map.put("bnum", resultMap.get("bnum"));
			}else {
				map.put("result", false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	@GetMapping("detail")
	public String detail(@RequestParam int bnum, Model m) {
		m.addAttribute("board", svc.getBoard(bnum));
		m.addAttribute("attachs", svc.getAttachs(bnum));
		m.addAttribute("comments", svc.getComments(bnum));
		return "postDetail";
	}
	
	// 댓글
	@PostMapping("comment")
    public String submitReply(Comment reply,
                                @RequestParam("bnum") int bnum, // 게시글 번호
                                @AuthenticationPrincipal PrincipalDetails userDetail) {
        Users user = userDetail.getUser(); // 현재 로그인한 사용자 정보 가져오기

        if (svc.addReply(reply, bnum, user)) { 
            return "redirect:/bboard/detail?bnum=" + bnum; // 댓글을 등록한 게시글 상세 페이지로 리다이렉트
        } else {
            return "redirect:/bboard/main";
        }
    }
	
	// 댓글의 답글
	@PostMapping("nestedreply")
    public String submitNestedReply(Comment reply,
                                @RequestParam("bnum") int bnum, // 게시글 번호
                                @AuthenticationPrincipal PrincipalDetails userDetail) {
        Users user = userDetail.getUser();

        if (svc.addReply(reply, bnum, user)) { 
            return "redirect:/bboard/detail?bnum=" + bnum;
        } else {
            return "redirect:/bboard/main";
        }
    }
	
	//파일 다운로드
	@GetMapping("download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fname") String fileName) {
        try {
            // 파일이 저장된 디렉토리 경로 설정
        	Path uploadPath = Paths.get("src/main/resources/static/upload/");
			String absolutePath = uploadPath.toAbsolutePath().toString();

            // 파일의 전체 경로 설정
            String filePath = absolutePath + "/"+ fileName;

            // 파일을 읽어서 바이트 배열로 변환
            Path path = Paths.get(filePath);
            byte[] fileContent = Files.readAllBytes(path);

            // 파일명이 한글일 경우 인코딩 처리
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 삭제
    @PostMapping("deleted")
    @ResponseBody
    public Map<String, Object> deletedPost(@RequestParam int bnum){
    	Path uploadPath = Paths.get("src/main/resources/static/upload/");
		String absolutePath = uploadPath.toAbsolutePath().toString();
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	boolean deletedOK;
		try {
			deletedOK = svc.deletedPost(bnum, absolutePath);
			map.put("result", deletedOK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
    }
    
    // 게시글 수정 폼
    @GetMapping("edit")
    public String editForm(@RequestParam int bnum, Model m) {
    	m.addAttribute("board", svc.getBoard(bnum));
    	m.addAttribute("attachs", svc.getAttachs(bnum));
        return "postEditForm";
    }
    
    
    @PostMapping("edit")
    @ResponseBody
    public Map<String, Object> editPost(@RequestParam(value = "files", required = false) MultipartFile[] mfiles, Board b){
    	Map<String, Object> map = new HashMap<String, Object>();
    	Path uploadPath = Paths.get("src/main/resources/static/upload/");
		String absolutePath = uploadPath.toAbsolutePath().toString();
    	
    	Board board = svc.getBoard(b.getBnum());
    	board.setTitle(b.getTitle());
    	board.setContent(b.getContent());
    	board.setPosted(new java.sql.Date(new Date().getTime()));
    	
    	try {
			map.put("result", svc.addPost(mfiles, board, absolutePath));
			map.put("bnum", board.getBnum());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
    }
    
    @PostMapping("filedeleted")
    @ResponseBody
    public Map<String, Object> fileDel(@RequestParam String fname){
    	Path uploadPath = Paths.get("src/main/resources/static/upload/");
		String absolutePath = uploadPath.toAbsolutePath().toString();
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	try {
			map.put("result", svc.deletedAttach(absolutePath, fname));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return map;
    }
    
}
