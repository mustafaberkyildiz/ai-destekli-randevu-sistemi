package com.mustafaberk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_records")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecords extends BaseEntity{

	@Column(name = "diagnosis")
	private String diagnosis;
	
	@Column(name = "prescription")
	private String prescription;
	
	@Column(name = "doctor_note")
	private String doctor_note;
	
	@ManyToOne
	private Patient patient;
	
	@ManyToOne
	private Doctor doctor;
	
}
