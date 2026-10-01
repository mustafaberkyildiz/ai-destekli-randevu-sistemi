package com.mustafaberk.model;

import java.time.LocalDate;

import com.mustafaberk.enums.LeaveStatus;
import com.mustafaberk.enums.LeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor_leave")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorLeave extends BaseEntity{

	@Column(name = "start_date")
	private LocalDate startDate;
	
	@Column(name = "end_date")
	private LocalDate endDate;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "leave_type")
	@Enumerated(EnumType.STRING)
	private LeaveType leaveType;
	
	@Column(name = "leave_status")
	@Enumerated(EnumType.STRING)
	private LeaveStatus status;
	
	@ManyToOne
	private Doctor doctor;
	
}
