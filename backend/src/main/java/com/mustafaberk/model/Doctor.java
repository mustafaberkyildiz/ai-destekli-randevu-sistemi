package com.mustafaberk.model;

import com.mustafaberk.enums.Title;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor extends BaseEntity{

	@Column(name = "title")
	@Enumerated(EnumType.STRING)
	private Title title;
	
	@Column(name = "annual_leave_limit")
	private Integer annualLeaveLimit = 30;

	@Column(name = "remaining_annual_leave")
	private Integer remainingAnnualLeave = 30;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
	
	@ManyToOne
	private Department department;
	
}
