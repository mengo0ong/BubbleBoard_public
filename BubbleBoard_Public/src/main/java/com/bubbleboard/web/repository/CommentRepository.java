package com.bubbleboard.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.bubbleboard.web.entity.Board;
import com.bubbleboard.web.entity.Comment;

@EnableJpaRepositories
public interface CommentRepository extends JpaRepository<Comment, Integer>{
	
	@Query(nativeQuery = true, value = "SELECT * FROM reply WHERE bnum = :#{#b.bnum} START WITH p_reply=0 CONNECT BY PRIOR rnum=p_reply")
	List<Comment> findByBoard(Board b);

}
