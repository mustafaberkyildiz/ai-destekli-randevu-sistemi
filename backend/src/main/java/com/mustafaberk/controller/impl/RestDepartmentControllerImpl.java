package com.mustafaberk.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestDepartmentController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.service.IDepartmentService;
import com.mustafaberk.service.IDoctorService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/department")
@RestController
public class RestDepartmentControllerImpl extends RestBaseController implements IRestDepartmentController{

	@Autowired
	private IDepartmentService departmentService;
	
	@Autowired
	private IDoctorService doctorService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/save")
	@Override
	public RootEntity<DtoDepartment> saveDepartment(@Valid @RequestBody DtoDepartmentIU dtoDepartmentIU) {
	    return ok(departmentService.saveDepartment(dtoDepartmentIU));
	}

	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	@GetMapping("/getAll")
	@Override
	public RootEntity<List<DtoDepartment>> getAllDepartment() {
		return ok(departmentService.getAllDepartment());
	}

	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	@GetMapping("/getDoctorsByDepartment/{departmentId}")
	@Override
	public RootEntity<List<Doctor>> getDoctorByDepartment(@PathVariable(name = "departmentId") Department department) {
		return ok(doctorService.getDoctorByDepartment(department));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/disable/{id}")
	@Override
	public RootEntity<Void> disableDepartment(@PathVariable(name = "id") Long departmentId) { 
		departmentService.disableDepartment(departmentId);
		return ok(null);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/enable/{departmentId}")
	@Override
	public RootEntity<Void> enableDepartment(@PathVariable Long departmentId) {
		departmentService.enableDepartment(departmentId);
		return ok(null);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAllForAdmin")
	@Override
	public RootEntity<List<DtoDepartment>> getAllDepartmentForAdmin() {
		return ok(departmentService.getAllDepartmentForAdmin());
	}
	
}
