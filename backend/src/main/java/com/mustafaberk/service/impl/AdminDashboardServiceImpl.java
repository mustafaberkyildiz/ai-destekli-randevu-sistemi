package com.mustafaberk.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mustafaberk.dto.DtoAdminDashboard;
import com.mustafaberk.dto.DtoAppointment;
import com.mustafaberk.dto.DtoDoctorLeave;
import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.enums.LeaveStatus;
import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.DoctorLeave;
import com.mustafaberk.repository.AppointmentRepository;
import com.mustafaberk.repository.DoctorLeaveRepository;
import com.mustafaberk.repository.DoctorRepository;
import com.mustafaberk.repository.PatientRepository;
import com.mustafaberk.service.IAdminDashboardService;

@Service
public class AdminDashboardServiceImpl implements IAdminDashboardService{

	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private DoctorLeaveRepository doctorLeaveRepository;
	
	@Autowired
	private AppointmentServiceImpl appointmentServiceImpl;
	
	@Autowired
	private DoctorLeaveServiceImpl doctorLeaveServiceImpl;
	
	@Override
	public DtoAdminDashboard adminDashboard() {

		DtoAdminDashboard dto = new DtoAdminDashboard();
		
		List<Appointment> appointmentList = appointmentRepository.
				findTop5ByAppointmentSlot_DateGreaterThanEqualAndStatusOrderByAppointmentSlot_DateAscAppointmentSlot_StartTimeAsc
				(LocalDate.now(),AppointmentStatus.SCHEDULED);
		
		List<DtoAppointment> dtoAppointmentList = appointmentList.stream()
				.map(x->appointmentServiceImpl.generateDtoAppointment(x))
				.toList();
		
		List<DoctorLeave> doctorLeaveList = doctorLeaveRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatus(LocalDate.now(), LocalDate.now(),LeaveStatus.ACTIVE);
		
		List<DtoDoctorLeave> dtoDoctorLeaveList = doctorLeaveList.stream()
				.map(x->doctorLeaveServiceImpl.toDtoDoctorLeave(x))
				.toList();
		
		dto.setRecentAppointments(dtoAppointmentList);
		
		dto.setTodayLeaves(dtoDoctorLeaveList);
		
		dto.setTodayAppointmentCount(appointmentRepository.countByAppointmentSlot_Date(LocalDate.now()));
		
		dto.setTotalDoctor(doctorRepository.count());
		dto.setTotalPatient(patientRepository.count());
		dto.setTodayCancelledAppointmentCount(appointmentRepository.countByStatusAndAppointmentSlot_Date(AppointmentStatus.CANCELLED,LocalDate.now()));
		dto.setTodayCompletedAppointmentCount(appointmentRepository.countByStatusAndAppointmentSlot_Date(AppointmentStatus.COMPLETED,LocalDate.now()));
		dto.setTodayScheduledAppointmentCount(appointmentRepository.countByStatusAndAppointmentSlot_Date(AppointmentStatus.SCHEDULED,LocalDate.now()));
		dto.setTodayDoctorCancelledAppointmentCount(appointmentRepository.countByStatusAndAppointmentSlot_Date(AppointmentStatus.DOCTOR_CANCELLED,LocalDate.now()));
		dto.setTodayNoShowAppointmentCount(appointmentRepository.countByStatusAndAppointmentSlot_Date(AppointmentStatus.NO_SHOW,LocalDate.now()));
		
		return dto;
	}

}
