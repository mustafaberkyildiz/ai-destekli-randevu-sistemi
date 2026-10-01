package com.mustafaberk.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestPatientController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoShowPatient;
import com.mustafaberk.dto.DtoSymptomRule;
import com.mustafaberk.model.SymptomRule;
import com.mustafaberk.service.IPatientService;

@RequestMapping("/patient")
@RestController
public class RestPatientControllerImpl extends RestBaseController implements IRestPatientController{

	@Autowired
	private IPatientService patientService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAll")
	@Override
	public RootEntity<List<DtoShowPatient>> getAllPatient() {
		return ok(patientService.getAllPatient());
	}

	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	@GetMapping("/get/{id}")
	@Override
	public RootEntity<DtoPatient> getPatientById(@PathVariable Long id) {
		return ok(patientService.getPatientById(id));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/getRules/{departmentId}")
	@Override
	public RootEntity<List<DtoSymptomRule>> getRulesByDepartment(@PathVariable Long departmentId) {
		return ok(patientService.getRulesByDepartment(departmentId));
	}
	
	
}
