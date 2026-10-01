package com.mustafaberk.controller.impl;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestAppointmentController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAppointment;
import com.mustafaberk.dto.DtoAppointmentIU;
import com.mustafaberk.dto.DtoAppointmentPatient;
import com.mustafaberk.dto.DtoAppointmentUpdate;
import com.mustafaberk.service.IAppointmentService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/appointment")
@RestController
public class RestAppointmentControllerImpl extends RestBaseController implements IRestAppointmentController{

	@Autowired
	private IAppointmentService appointmentService;
	
	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping("/save")
	@Override
	public RootEntity<DtoAppointment> saveAppointment(@Valid @RequestBody DtoAppointmentIU dto) {
		return ok(appointmentService.saveAppointment(dto));
	}
	
	@PreAuthorize("hasRole('DOCTOR')")
	@PutMapping("/cancel-by-doctor/{appointmentId}")
	@Override
	public RootEntity<DtoAppointment> cancelledByDoctor(@PathVariable Long appointmentId) {
		return ok(appointmentService.cancelledByDoctor(appointmentId));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@PutMapping("/cancel-by-patient/{appointmentId}")
	@Override
	public RootEntity<DtoAppointment> cancelledByPatient(@PathVariable Long appointmentId) {
		return ok(appointmentService.cancelledByPatient(appointmentId));
	}

	@PreAuthorize("hasRole('PATIENT')")
	@PutMapping("/update/{appointmentId}")
	@Override
	public RootEntity<DtoAppointment> updateAppointment(@PathVariable(name = "appointmentId") Long id,@RequestBody DtoAppointmentUpdate dto) {
		return ok(appointmentService.updateAppointment(id, dto));
	}

	@PutMapping("/completed/{appointmentId}")
	@Override
	public RootEntity<DtoAppointment> completeAppointment(@PathVariable Long appointmentId) {
		return ok(appointmentService.completeAppointment(appointmentId));
	}

	@PutMapping("/noShow/{appointmentId}")
	@Override
	public RootEntity<DtoAppointment> noshowAppointment(@PathVariable Long appointmentId) {
		return ok(appointmentService.noshowAppointment(appointmentId));
	}
	
	@PreAuthorize("hasRole('PATIENT')")
	@GetMapping("/get-patient/{patientId}")
	@Override
	public RootEntity<List<DtoAppointmentPatient>> getPatientAppointments(@PathVariable(name = "patientId") Long id) {
		return ok(appointmentService.getPatientAppointments(id));
	}

	@PreAuthorize("hasRole('DOCTOR')")
	@GetMapping("/get-doctor/{doctorId}")
	@Override
	public RootEntity<List<DtoAppointment>> getDoctorAppointments(@PathVariable(name = "doctorId") Long id) {
		return ok(appointmentService.getDoctorAppointments(id));
	}

	@PutMapping("/is-completable/{appointmentId}")
	@Override
	public RootEntity<Void> isCompletableAppointment(@PathVariable Long appointmentId) {
		appointmentService.isCompletableAppointment(appointmentId);
		return ok(null);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAll")
	@Override
	public RootEntity<List<DtoAppointment>> getAllAppointment() {
		return ok(appointmentService.getAllAppointment());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/get-doctor-forAdmin/{doctorId}")
	@Override
	public RootEntity<List<DtoAppointment>> getDoctorAppointmentsForAdmin(@PathVariable(name = "doctorId") Long id) {
		return ok(appointmentService.getDoctorAppointmentsForAdmin(id));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/cancel-by-admin/{appointmentId}")
	@Override
	public RootEntity<DtoAppointment> cancelledByAdmin(@PathVariable Long appointmentId) {
		return ok(appointmentService.cancelledByAdmin(appointmentId));
	}

	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	@GetMapping("/get-date-between")
	@Override
	public RootEntity<List<DtoAppointment>> getAppointmentsBetweenDates(@RequestParam LocalDate startDate,@RequestParam LocalDate endDate) {
		return ok(appointmentService.getAppointmentsBetweenDates(startDate, endDate));
	}

}
