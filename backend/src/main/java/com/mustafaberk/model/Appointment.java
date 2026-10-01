package com.mustafaberk.model;

import com.mustafaberk.enums.AppointmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment extends BaseEntity{

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private AppointmentStatus status;
	
	@Column(name = "note")
	private String note;
	
	@ManyToOne
	private Patient patient;
	
	@ManyToOne
	private AppointmentSlot appointmentSlot;
	
}
