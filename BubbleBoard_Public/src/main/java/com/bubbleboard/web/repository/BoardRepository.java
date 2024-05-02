package com.bubbleboard.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.bubbleboard.web.entity.Board;

@EnableJpaRepositories
public interface BoardRepository extends JpaRepository<Board, Integer>{

	@Query(nativeQuery = true, value = "SELECT * FROM board  START WITH p_board=0 CONNECT BY PRIOR bnum=p_board")
	List<Board> findAll();
	
	Board findByBnum(int bnum);

}
