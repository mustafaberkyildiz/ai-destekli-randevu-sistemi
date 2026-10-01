package com.mustafaberk.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mustafaberk.repository.AppointmentRepository;
import com.mustafaberk.service.IAppointmentService;

@Component
public class ScheduledAppointmentStatus {

	@Autowired
	private IAppointmentService appointmentService;
	
	@Scheduled(cron = "0 */5 * * * *", zone = "Europe/Istanbul")
	public void markPastAppointmentsAsNoShow() {
		appointmentService.markPastScheduledAppointmentsAsNoShow();
	}
	
}
