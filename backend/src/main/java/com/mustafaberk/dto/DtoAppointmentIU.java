package com.mustafaberk.dto;

import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAppointmentIU {
	
	private String note;
	
	@NotNull
	private Long patientId;
	
	@NotNull
	private Long appointmentSlotId;
}
