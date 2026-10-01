package com.mustafaberk.dto;

import java.util.List;

import com.mustafaberk.model.Appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAdminDashboard {

	private Long totalDoctor;
	
	private Long totalPatient;
	
	private Long todayAppointmentCount;
	
	private Long todayScheduledAppointmentCount;
	
	private Long todayCancelledAppointmentCount;
	
	private Long todayCompletedAppointmentCount;
	
	private Long todayNoShowAppointmentCount;
	
	private Long todayDoctorCancelledAppointmentCount;
	
	private List<DtoDoctorLeave> todayLeaves;
	
	private List<DtoAppointment> recentAppointments;
}
