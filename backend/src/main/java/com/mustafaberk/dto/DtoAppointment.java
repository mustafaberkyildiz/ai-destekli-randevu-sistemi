package com.mustafaberk.dto;

import com.mustafaberk.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAppointment extends DtoBase{

	private AppointmentStatus status;
	
	private String note;
	
	private DtoPatient patient;
	
	private DtoAppointmentSlot appointmentSlot;
	
}
