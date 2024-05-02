package com.bubbleboard.web.entity;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "board")
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "ID_SEQUENCE_GENERATOR", sequenceName = "SEQ_BOARD", initialValue = 1, allocationSize = 1)
	private int bnum;
	
	@ManyToOne
	@JoinColumn(name="unum")
	private Users user;
	
	private java.sql.Date posted;
	private String title;
	private String content;
	
	@Column(name = "p_board")
	private int pBoard =0;
}
