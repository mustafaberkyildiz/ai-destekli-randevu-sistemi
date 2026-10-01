package com.mustafaberk.service;

import java.util.List;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.AuthRequestUser;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientIU;
import com.mustafaberk.dto.DtoPatientUpdate;
import com.mustafaberk.dto.DtoShowPatient;
import com.mustafaberk.dto.DtoSymptomRule;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.model.SymptomRule;

public interface IPatientService {

	public DtoPatient getPatientById(Long id);
	
	public List<DtoShowPatient> getAllPatient();

	public DtoPatient updatePatient(Long patientId, DtoPatientUpdate dto);
	
	public List<DtoSymptomRule> getRulesByDepartment(Long departmentId);
}
