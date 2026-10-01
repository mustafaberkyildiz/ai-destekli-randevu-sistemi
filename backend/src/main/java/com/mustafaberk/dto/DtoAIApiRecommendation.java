package com.mustafaberk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAIApiRecommendation {

	 private long department_id;
	    
	 private String department_name;
	   
	 private String matched_symptom;
	
}
