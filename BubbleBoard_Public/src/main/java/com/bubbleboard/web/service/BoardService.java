package com.bubbleboard.web.service;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bubbleboard.web.entity.Attach;
import com.bubbleboard.web.entity.Board;
import com.bubbleboard.web.entity.Comment;
import com.bubbleboard.web.entity.Users;
import com.bubbleboard.web.repository.AttachRepository;
import com.bubbleboard.web.repository.BoardRepository;
import com.bubbleboard.web.repository.CommentRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardService {
	
	@Autowired
	private AttachRepository aRepo;
	
	@Autowired
	private BoardRepository bRepo;
	
	@Autowired
	private CommentRepository rRepo;
	
	public List<Board> getBoardList(){
		return bRepo.findAll();
	}

	@Transactional
	public Map<String, Object> addPost(MultipartFile[] mfiles, Board b, String absolutePath) throws Exception{
		Map<String, Object> map = new HashMap<>();
		UUID uuid = null;
		
		try {
			b.setPosted(new java.sql.Date(System.currentTimeMillis()));
			b = bRepo.findById(bRepo.save(b).getBnum()).get();
			if(bRepo.findByBnum(b.getBnum())==null) throw new Exception();
			
			log.info(b.toString());
			
			for(int i=0; i<mfiles.length; i++) {
				if(mfiles[0].getSize()==0) continue;
				String filename = mfiles[i].getOriginalFilename();
				String[] f = filename.trim().split("\\.");
				String fname = f[0]+uuid.randomUUID()+"."+f[1];
				
				mfiles[i].transferTo(new File(absolutePath+"/"+fname));
				Attach attach = new Attach();
				attach.setBoard(b);
				attach.setFname(fname);
				
				boolean saved = aRepo.save(attach) != null;
				if(!saved) throw new Exception(); 
			}
			map.put("bnum", b.getBnum());
			map.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", false);
		}
		return map;	
	}

	public Board getBoard(int bnum) {
		return bRepo.findByBnum(bnum);
	}

	public List<Attach> getAttachs(int bnum) {
		return aRepo.findByBoard(getBoard(bnum));
	}

	public List<Comment> getComments(int bnum) {
		return rRepo.findByBoard(getBoard(bnum));
	}

	public boolean addReply(Comment reply, int bnum, Users user) {
        reply.setUser(user);
        reply.setBoard(bRepo.findByBnum(bnum));
        reply.setRDate(new java.sql.Date(new Date().getTime())); // 현재 시간으로 설정
        if(reply.getPReply()!=0) return rRepo.save(reply)!=null; // 만약 preply에 값이 있으면 바로 저장      
        reply.setPReply(0); // 기본값으로 0 설정
		return rRepo.save(reply)!=null;
	}
	
	@Transactional
	public boolean deletedPost(int bnum, String absolutePath) throws Exception {
		Board b = bRepo.findByBnum(bnum);
		List<Attach> attachs = aRepo.findByBoard(b);
		for(Attach a : attachs) {
			File f = new File(absolutePath+"/"+a.getFname());
			f.delete();
			if(f.exists()) throw new Exception();
		}

		bRepo.deleteById(bnum);
		if(bRepo.findByBnum(bnum)!=null) throw new Exception();
		
		return true;
	}

	@Transactional
	public boolean deletedAttach(String absolutePath, String fname) throws Exception{
		Attach a = aRepo.findByFname(fname);
		aRepo.delete(a);
		if(aRepo.findByFname(fname)!=null) throw new Exception(); 
		
		File f = new File(absolutePath+"/"+fname);
		if(!f.delete()) throw new Exception();
		return true;
	}
}
