package com.mustafaberk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_recommendation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIRecommendation extends BaseEntity{

	@Column(name = "symptom_text")
	private String symptomText;
	
	@ManyToOne
	private Patient patient;
	
	@ManyToOne
	private Department suggested_department;
	
}
