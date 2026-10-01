package com.mustafaberk.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "symptom_rule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomRule{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "rule_text")
	private String rule_text;
	




	@JoinTable(name = "department_symptom_rule",
			   joinColumns = @JoinColumn(name = "symptom_rule_id"),
			   inverseJoinColumns = @JoinColumn(name = "department_id") )
	@ManyToMany
	private List<Department> department;
	
}
  