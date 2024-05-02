package com.bubbleboard.web.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reply")
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "ID_SEQUENCE_GENERATOR", sequenceName = "SEQ_REPLY", initialValue = 1, allocationSize = 1)
	private int rnum;
	
	@ManyToOne
	@JoinColumn(name = "unum")
	private Users user;
	
	@ManyToOne
	@JoinColumn(name = "bnum")
	private Board board;
	
	@Column(name = "r_date")
	private java.sql.Date rDate;

	@Column(name = "r_text")
	private String rText;
	
	private char secret = 'N';
	
	@Column(name = "p_reply")
	private int pReply;
}
