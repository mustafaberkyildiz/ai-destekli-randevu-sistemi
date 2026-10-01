package com.mustafaberk.service;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.dto.DtoAppointmentSlot;

public interface IAppointmentSlotService {
	
	public void createSlots();
	
	public List<DtoAppointmentSlot> getAvailableSlots(Long doctorId , LocalDate date);
	
	public DtoAppointmentSlot earliestSlot(Long departmentId);
}
