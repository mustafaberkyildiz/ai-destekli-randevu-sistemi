package com.mustafaberk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoMedicalRecords extends DtoBase{

	private String diagnosis;
	
	private String prescription;
	
	private String doctor_note;
	
	private DtoPatient patient;
	
	private DtoDoctor doctor;
	
}
