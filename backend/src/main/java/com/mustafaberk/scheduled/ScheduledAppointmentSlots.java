package com.mustafaberk.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mustafaberk.service.IAppointmentSlotService;

@Component
public class ScheduledAppointmentSlots {


	
	@Autowired
	private IAppointmentSlotService appointmentSlotService;
	

	@Scheduled(cron = "0 0 1 * * *", zone = "Europe/Istanbul")
	public void generateDailySlots() {
		appointmentSlotService.createSlots();
	}
	
}
