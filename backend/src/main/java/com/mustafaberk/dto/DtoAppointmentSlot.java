package com.mustafaberk.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mustafaberk.enums.SlotStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAppointmentSlot extends DtoBase{

	private LocalDate date; 
	
	private LocalTime startTime; 
	
	private LocalTime endTime;
	
	private SlotStatus status; 
	
	private DtoDoctor doctor;
	
}
