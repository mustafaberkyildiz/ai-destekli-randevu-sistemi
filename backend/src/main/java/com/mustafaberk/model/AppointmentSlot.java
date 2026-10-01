package com.mustafaberk.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mustafaberk.enums.SlotStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointment_slot",
		uniqueConstraints = {
		        @UniqueConstraint(columnNames = {"doctor_id", "date", "start_time"}) }
      )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentSlot extends BaseEntity{

	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "start_time")
	private LocalTime startTime;
	
	@Column(name = "end_time")
	private LocalTime endTime;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private SlotStatus status; 
	
	@ManyToOne
	private Doctor doctor;
	
}
