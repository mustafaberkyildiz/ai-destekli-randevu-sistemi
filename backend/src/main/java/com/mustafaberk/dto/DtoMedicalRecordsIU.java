package com.mustafaberk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoMedicalRecordsIU {

	@NotNull
	private String diagnosis;
	
	@NotNull
	private String prescription;
	
	@NotNull
	private String doctor_note;
	
	@NotNull
	private Long patientId;
	
	@NotNull
	private Long doctorId;
	
}
