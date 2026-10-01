package com.mustafaberk.controller.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestDoctorLeaveController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoDoctorLeave;
import com.mustafaberk.dto.DtoDoctorLeaveIU;
import com.mustafaberk.dto.DtoDoctorLeaveUpdate;
import com.mustafaberk.service.IDoctorLeaveService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/doctorLeave")
@RestController
public class RestDoctorLeaveControllerImpl extends RestBaseController implements IRestDoctorLeaveController{

	@Autowired
	private IDoctorLeaveService doctorLeaveService;
	
	@PreAuthorize("hasRole('DOCTOR')")
	@PostMapping("/save")
	@Override
	public RootEntity<DtoDoctorLeave> saveDoctorLeave(@Valid @RequestBody DtoDoctorLeaveIU doctorLeaveIU) {
		return ok(doctorLeaveService.saveDoctorLeave(doctorLeaveIU));
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@GetMapping("/get-doctor-Leave/{doctorId}")
	@Override
	public RootEntity<List<DtoDoctorLeave>> getAllDoctorLeave(@PathVariable Long doctorId) {
		return ok(doctorLeaveService.getAllDoctorLeave(doctorId));
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@PatchMapping("/update-doctor-leave/{id}")
	@Override
	public RootEntity<DtoDoctorLeave> updateDoctorLeave(@PathVariable Long id,@RequestBody @Valid DtoDoctorLeaveUpdate doctorLeaveUpdate) {
		return ok(doctorLeaveService.updateDoctorLeave(id, doctorLeaveUpdate));
	}

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@PutMapping("/cancel-doctor-leave/{id}")
	@Override
	public void cancelDoctorLeave(@PathVariable Long id) {
		doctorLeaveService.cancelDoctorLeave(id);
	}


	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAll")
	@Override
	public RootEntity<List<DtoDoctorLeave>> getAllDoctorLeaveForAdmin(@RequestParam(required = false) Long doctorId,
																	  @RequestParam(required = false) LocalDate startDate,
																	  @RequestParam(required = false) LocalDate endDate) {
		return ok(doctorLeaveService.getAllDoctorLeaveForAdmin(doctorId, startDate, endDate));
	}

}
