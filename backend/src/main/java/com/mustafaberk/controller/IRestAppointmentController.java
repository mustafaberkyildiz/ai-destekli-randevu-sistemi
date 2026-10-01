package com.mustafaberk.controller;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.dto.DtoAppointment;
import com.mustafaberk.dto.DtoAppointmentIU;
import com.mustafaberk.dto.DtoAppointmentPatient;
import com.mustafaberk.dto.DtoAppointmentUpdate;

public interface IRestAppointmentController {

	public RootEntity<DtoAppointment> saveAppointment(DtoAppointmentIU dto);
	
	public RootEntity<DtoAppointment> cancelledByDoctor(Long appointmentId);
	
	public RootEntity<DtoAppointment> cancelledByPatient(Long appointmentId);
	
	public RootEntity<DtoAppointment> updateAppointment(Long id,DtoAppointmentUpdate dto);
	
	public RootEntity<DtoAppointment> completeAppointment(Long appointmentId);
	
	public RootEntity<Void> isCompletableAppointment(Long appointmentId);
	
	public RootEntity<DtoAppointment> noshowAppointment(Long appointmentId);
	
	public RootEntity<List<DtoAppointmentPatient>> getPatientAppointments(Long id);
	
	public RootEntity<List<DtoAppointment>> getDoctorAppointments(Long id);
	
	public RootEntity<List<DtoAppointment>> getAllAppointment();
	
	public RootEntity<List<DtoAppointment>> getDoctorAppointmentsForAdmin(Long id);
	
	public RootEntity<DtoAppointment> cancelledByAdmin(Long appointmentId);
	
	public RootEntity<List<DtoAppointment>> getAppointmentsBetweenDates(LocalDate startDate,LocalDate endDate);
}
