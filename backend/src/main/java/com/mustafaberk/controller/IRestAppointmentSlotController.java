package com.mustafaberk.controller;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.dto.DtoAppointmentSlot;

public interface IRestAppointmentSlotController {

	public RootEntity<String> createSlots();
	
	public RootEntity<List<DtoAppointmentSlot>> getAvailableSlots(Long doctorId , LocalDate date);
	
	public RootEntity<DtoAppointmentSlot> earliestSlot(Long departmentId);
}
