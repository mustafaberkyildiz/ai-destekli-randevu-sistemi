package com.mustafaberk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAIRecommendation extends DtoBase{

	private String symptomText;
	
	private DtoPatient patient;
	
	private DtoDepartment suggested_department;
	
}
