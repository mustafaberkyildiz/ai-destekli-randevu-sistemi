package com.mustafaberk.controller.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestAppointmentSlotController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAppointmentSlot;
import com.mustafaberk.service.IAppointmentSlotService;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/appointment_slot")
@RestController
public class IRestAppointmentSlotControllerImpl extends RestBaseController implements IRestAppointmentSlotController{

	@Autowired
	private IAppointmentSlotService appointmentSlotService;
	
	@PostMapping("/create")
	@Override
	public RootEntity<String> createSlots() {	
		appointmentSlotService.createSlots();
		return ok("Slotlar Olusturuldu !");
	}

	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	@GetMapping("/getAvailableSlots")
	@Override
	public RootEntity<List<DtoAppointmentSlot>> getAvailableSlots(@RequestParam Long doctorId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return ok(appointmentSlotService.getAvailableSlots(doctorId, date));
	}

	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	@GetMapping("/getEarliestSlot/{departmentId}")
	@Override
	public RootEntity<DtoAppointmentSlot> earliestSlot(@PathVariable Long departmentId) {
		return ok(appointmentSlotService.earliestSlot(departmentId));
	}
	
}
