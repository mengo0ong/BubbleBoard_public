package com.bubbleboard.web.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attach")
public class Attach {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "ID_SEQUENCE_GENERATOR", sequenceName = "SEQ_ATTACH", initialValue = 1, allocationSize = 1)
	private int fnum;
	
	@ManyToOne
	@JoinColumn(name = "bnum")
	private Board board;
	
	private String fname;
}
