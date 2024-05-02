package com.bubbleboard.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.bubbleboard.web.entity.Attach;
import com.bubbleboard.web.entity.Board;

@EnableJpaRepositories
public interface AttachRepository extends JpaRepository<Attach, Integer>{

	List<Attach> findByBoard(Board b);
	Attach findByFname(String fname);

}
