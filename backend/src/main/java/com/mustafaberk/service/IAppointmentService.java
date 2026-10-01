package com.mustafaberk.service;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAppointment;
import com.mustafaberk.dto.DtoAppointmentIU;
import com.mustafaberk.dto.DtoAppointmentPatient;
import com.mustafaberk.dto.DtoAppointmentUpdate;

public interface IAppointmentService {

	public DtoAppointment saveAppointment(DtoAppointmentIU dto);
	
	public DtoAppointment cancelledByDoctor(Long appointmentId);
	
	public DtoAppointment cancelledByPatient(Long appointmentId);
	
	public DtoAppointment updateAppointment(Long id,DtoAppointmentUpdate dto);
	
	public DtoAppointment completeAppointment(Long appointmentId);
	
	public void isCompletableAppointment(Long appointmentId);
	
	public DtoAppointment noshowAppointment(Long appointmentId);
	
	public List<DtoAppointmentPatient> getPatientAppointments(Long id);
	
	public List<DtoAppointment> getDoctorAppointments(Long id);
	
	public void markPastScheduledAppointmentsAsNoShow();
	
	public List<DtoAppointment> getAllAppointment();
	
	public List<DtoAppointment> getDoctorAppointmentsForAdmin(Long id);
	
	public DtoAppointment cancelledByAdmin(Long appointmentId);
	
	public List<DtoAppointment> getAppointmentsBetweenDates(LocalDate startDate,LocalDate endDate);
	
}
