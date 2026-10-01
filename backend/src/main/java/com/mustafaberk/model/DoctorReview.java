package com.mustafaberk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor_review")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorReview extends BaseEntity{
	
	@Column(name = "score", nullable = false)
	private Integer score;
	
	@Column(name = "comment", length = 500)
	private String comment;
	
	@Column(name = "update_used", nullable = false)
	private boolean updateUsed = false;
	
	@OneToOne
	@JoinColumn(name = "appointment_id", nullable = false, unique = true)
	private Appointment appointment;
	
}
