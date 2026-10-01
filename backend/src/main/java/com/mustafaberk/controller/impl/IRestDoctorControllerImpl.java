package com.mustafaberk.controller.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestDoctorController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorReview;
import com.mustafaberk.dto.DtoDoctorReviewIU;
import com.mustafaberk.dto.DtoShowDoctor;
import com.mustafaberk.service.IDoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/doctor")
public class IRestDoctorControllerImpl extends RestBaseController implements IRestDoctorController {

	@Autowired
	private IDoctorService doctorService;
	
	@GetMapping("/get/{id}")
	@Override
	public RootEntity<DtoDoctor> getDoctorById(@PathVariable Long id) {
		return ok(doctorService.getDoctorById(id));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAll")
	@Override
	public RootEntity<List<DtoShowDoctor>> getAllDoctor() {
		return ok(doctorService.getAllDoctor());
	}

	@GetMapping("/average-score/{doctorId}")
	@Override
	public RootEntity<Double> getDoctorAverageScore(@PathVariable Long doctorId) {
		return ok(doctorService.getDoctorAverageScore(doctorId));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping("/save-doctor-review")
	@Override
	public RootEntity<DtoDoctorReview> saveDoctorReview(@RequestBody @Valid DtoDoctorReviewIU doctorReview) {
		return ok(doctorService.saveDoctorReview(doctorReview));
	}
	
	@PreAuthorize("hasRole('PATIENT')")
	@PutMapping("/update-doctor-review")
	@Override
	public RootEntity<DtoDoctorReview> updateDoctorReview(@RequestBody @Valid DtoDoctorReviewIU doctorReview) {
		return ok(doctorService.updateDoctorReview(doctorReview));
	}

	@PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
	@GetMapping("/getAllDoctorReview")
	@Override
	public RootEntity<List<DtoDoctorReview>> getAllDoctorReview(@RequestParam(required = false) Long doctorId,@RequestParam(required = false) Integer minScore, 
															    @RequestParam(required = false) LocalDate startDate,@RequestParam(required = false) LocalDate endDate) {
		return ok(doctorService.getAllDoctorReview(doctorId, minScore, startDate, endDate));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/get-patient-doctor-review")
	@Override
	public RootEntity<List<DtoDoctorReview>> getAllDoctorReviewForPatient(@RequestParam Long patientId,@RequestParam(required = false) Integer minScore,
																          @RequestParam(required = false) LocalDate startDate,@RequestParam(required = false) LocalDate endDate) {
		return ok(doctorService.getAllDoctorReviewForPatient(patientId, minScore, startDate, endDate));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/disable/{id}")
	@Override
	public RootEntity<Void> disableDoctor(@PathVariable(name = "id") Long doctorId) {
		doctorService.disableDoctor(doctorId);
		return ok(null);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/enable/{doctorId}")
	@Override
	public RootEntity<Void> enableDoctor(@PathVariable Long doctorId) {
		doctorService.enableDoctor(doctorId);
		return ok(null);
	}
	
}
