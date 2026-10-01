package com.mustafaberk.controller;

import java.util.List;

import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoShowPatient;
import com.mustafaberk.dto.DtoSymptomRule;
import com.mustafaberk.model.SymptomRule;

public interface IRestPatientController {

	public RootEntity<List<DtoShowPatient>> getAllPatient();
	
	public RootEntity<DtoPatient> getPatientById(Long id);
	
	public RootEntity<List<DtoSymptomRule>> getRulesByDepartment(Long departmentId);

}
