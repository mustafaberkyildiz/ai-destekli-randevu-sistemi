package com.mustafaberk.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mustafaberk.dto.DtoAppointmentSlot;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.enums.SlotStatus;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.repository.AppointmentRepository;
import com.mustafaberk.repository.AppointmentSlotRepository;
import com.mustafaberk.repository.DoctorRepository;
import com.mustafaberk.service.IAppointmentSlotService;

import io.netty.handler.codec.quic.QuicPathEvent.New;

@Service
public class AppointmentSlotServiceImpl implements IAppointmentSlotService{

	@Autowired
	private AppointmentSlotRepository appointmentSlotRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository; 
	
	@Override
	public void createSlots() {
		
		List<Doctor> doctorList =  doctorRepository.findAll();
		
		for (Doctor doctor : doctorList) {
			if (DoctorServiceImpl.isActive(doctor)) {
				generateSlotsForDoctor(doctor);
			}
		}
		
	}
	
	private boolean existsByDateAndStartTime(List<AppointmentSlot> slots , LocalDate date, LocalTime startTime) {

		for (AppointmentSlot appointmentSlot : slots) {
			if(date.equals(appointmentSlot.getDate()) && startTime.equals(appointmentSlot.getStartTime())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isLunchTime(LocalTime starTime) {
		return !starTime.isBefore(LocalTime.of(12, 0)) && starTime.isBefore(LocalTime.of(13, 0));

	}
	
	public void generateSlotsForDoctor(Doctor doctor) {

	    LocalDate startDate = LocalDate.now();
	    LocalDate endDate = startDate.plusDays(29);
	    	    
	    Long doctorId = doctor.getId();

	    List<AppointmentSlot> existingSlots = appointmentSlotRepository.findByDoctorIdAndDateGreaterThanEqual(doctorId,startDate); 

	    

	    	    



	    Set<String> existingControlSlots = new HashSet<>();

	    for (AppointmentSlot slot : existingSlots) {
	        String key = slot.getDate() + "_" + slot.getStartTime();
	        existingControlSlots.add(key);
	    }
	    
	    
	    while (!startDate.isAfter(endDate)) {
	    	
	        LocalDateTime startTime = startDate.atTime(9, 0);
	        LocalDateTime endTimeLimit = startDate.atTime(17, 0);

	        while (startTime.isBefore(endTimeLimit)) {

	            LocalDateTime endTime = startTime.plusMinutes(30);
	            	    

	            boolean exist = existsByDateAndStartTime(existingSlots,endTime.toLocalDate(),startTime.toLocalTime());     
	            
	            String key = startTime.toLocalDate() + "_" + startTime.toLocalTime();

/*
Slot zaten var mı? ❌ (exist)
Öğle arası mı? ❌
Aynı slot daha önce var mı? ❌ (Set.contains)
*/
	            if (!exist && !isLunchTime(startTime.toLocalTime()) && !existingControlSlots.contains(key)) { 
	                AppointmentSlot slot = new AppointmentSlot();
	                slot.setDoctor(doctor);
	                slot.setDate(startTime.toLocalDate());
	                slot.setStartTime(startTime.toLocalTime());
	                slot.setEndTime(endTime.toLocalTime());
	                slot.setStatus(SlotStatus.AVAILABLE);

	                appointmentSlotRepository.save(slot);
	            }

	            startTime = startTime.plusMinutes(30);
	        }

	        startDate = startDate.plusDays(1);
	    }
	}

	@Override
	public List<DtoAppointmentSlot> getAvailableSlots(Long doctorId, LocalDate date) {
		
		List<AppointmentSlot> slots = appointmentSlotRepository.findByDoctor_IdAndDateAndStatus(doctorId, date, SlotStatus.AVAILABLE);
		
		List<DtoAppointmentSlot> dtoList = new ArrayList<>();

		if (LocalDate.now().isAfter(date)) {
			throw new BaseException(new ErrorMessage(MessageType.INVALID_DAY, null));
		}
		
		for (AppointmentSlot slot : slots) {
			

	        boolean isTaken = appointmentRepository.existsByAppointmentSlotAndStatus(slot, AppointmentStatus.SCHEDULED);

	        if (isTaken) {
	            continue;
	        }
			
	        LocalDateTime localDateTime = LocalDateTime.of(slot.getDate(), slot.getStartTime());
	        if (LocalDateTime.now().isAfter(localDateTime)) {
	        	continue;
	        }
	        
	        DtoAppointmentSlot dto = new DtoAppointmentSlot();
	        DtoDoctor dtoDoctor = new DtoDoctor();
	        DtoUser dtoUser = new DtoUser();
	        DtoDepartment dtoDepartment = new DtoDepartment();

	        dto.setId(slot.getId());
	        dto.setDate(slot.getDate());
	        dto.setStartTime(slot.getStartTime());
	        dto.setEndTime(slot.getEndTime());
	        dto.setStatus(slot.getStatus());
	        
	        BeanUtils.copyProperties(slot.getDoctor().getUser(), dtoUser);
	        BeanUtils.copyProperties(slot.getDoctor(), dtoDoctor);
	        BeanUtils.copyProperties(slot.getDoctor().getDepartment(), dtoDepartment);
	        
	        dtoDoctor.setUser(dtoUser);
	        dtoDoctor.setDepartment(dtoDepartment);
	        dto.setDoctor(dtoDoctor);
	        
	        dtoList.add(dto);
	    }
		
		return dtoList;
	}

	private DtoAppointmentSlot generateDtoAppointmentSlot(AppointmentSlot appointmentSlot) {
		
		DtoAppointmentSlot dtoAppointmentSlot = new DtoAppointmentSlot();
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoUser dtoUser = new DtoUser();
		DtoDepartment dtoDepartment = new DtoDepartment(); 
		
		BeanUtils.copyProperties(appointmentSlot, dtoAppointmentSlot);
		BeanUtils.copyProperties(appointmentSlot.getDoctor(), dtoDoctor);
		BeanUtils.copyProperties(appointmentSlot.getDoctor().getUser(), dtoUser);
		BeanUtils.copyProperties(appointmentSlot.getDoctor().getDepartment(), dtoDepartment);
		
		dtoDoctor.setUser(dtoUser);
		dtoDoctor.setDepartment(dtoDepartment);
		dtoAppointmentSlot.setDoctor(dtoDoctor);
		
		return dtoAppointmentSlot;
	}
	
	@Override
	public DtoAppointmentSlot earliestSlot(Long departmentId) {
				
		List<AppointmentSlot> slots = appointmentSlotRepository
				.findByDoctor_Department_IdAndStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(departmentId, SlotStatus.AVAILABLE, LocalDate.now());
		
		for (AppointmentSlot appointmentSlot : slots) {
			
			boolean isTaken = appointmentRepository.existsByAppointmentSlotAndStatus(appointmentSlot, AppointmentStatus.SCHEDULED);
			
			LocalDateTime localDateTime = LocalDateTime.of(appointmentSlot.getDate(), appointmentSlot.getStartTime());
			
			if (!isTaken && LocalDateTime.now().isBefore(localDateTime)) {
				return generateDtoAppointmentSlot(appointmentSlot);
			}
		}
		throw new BaseException(new ErrorMessage(MessageType.AVAILABLE_SLOT_NOT_FOUND, null));

	}

}
