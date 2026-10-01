package com.mustafaberk.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mustafaberk.enums.SlotStatus;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAppointmentSlotIU {
	
	@NotNull
	@FutureOrPresent
	private LocalDate date; 
	
	@NotNull
	private LocalTime startTime; 
	
	@NotNull
	private LocalTime endTime;
	
	@NotNull
	private SlotStatus status; 
	
	@NotNull
	private Long doctorId;

	@AssertTrue(message = "End time start time'dan sonra olmalı")
	public boolean isValidTimeRange() {
	    if (startTime == null || endTime == null) return true;
	    return endTime.isAfter(startTime);
	}
	
}
