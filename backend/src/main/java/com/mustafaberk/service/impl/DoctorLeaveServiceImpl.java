package com.mustafaberk.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mustafaberk.config.AiConfig;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorLeave;
import com.mustafaberk.dto.DtoDoctorLeaveIU;
import com.mustafaberk.dto.DtoDoctorLeaveUpdate;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.enums.LeaveStatus;
import com.mustafaberk.enums.LeaveType;
import com.mustafaberk.enums.SlotStatus;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.DoctorLeave;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.AppointmentRepository;
import com.mustafaberk.repository.AppointmentSlotRepository;
import com.mustafaberk.repository.DoctorLeaveRepository;
import com.mustafaberk.repository.DoctorRepository;
import com.mustafaberk.service.IAppointmentService;
import com.mustafaberk.service.IDoctorLeaveService;

import io.netty.handler.codec.quic.QuicPathEvent.New;

@Service
public class DoctorLeaveServiceImpl implements IDoctorLeaveService{

    private final AppointmentSlotServiceImpl appointmentSlotServiceImpl;

	@Autowired
	private DoctorLeaveRepository doctorLeaveRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private IAppointmentService appointmentService;
	
	@Autowired
	private AppointmentSlotRepository appointmentSlotRepository;

    DoctorLeaveServiceImpl(AppointmentSlotServiceImpl appointmentSlotServiceImpl) {
        this.appointmentSlotServiceImpl = appointmentSlotServiceImpl;
    }
		
	private DoctorLeave createDoctorLeave(DtoDoctorLeaveIU doctorLeaveIU) {
		
		Optional<Doctor> optional = doctorRepository.findById(doctorLeaveIU.getDoctorId());
		if (optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, doctorLeaveIU.getDoctorId().toString()));
		}
		

		if (doctorLeaveIU.getStartDate().isAfter(doctorLeaveIU.getEndDate())) {
		    throw new BaseException(new ErrorMessage(MessageType.DATE_NOT_VALID, null));
		}				
		
		User user = optional.get().getUser();
		
		Doctor doctor = new Doctor();
		BeanUtils.copyProperties(optional.get(), doctor);
		doctor.setUser(user);
		
		DoctorLeave doctorLeave = new DoctorLeave();
		doctorLeave.setDoctor(doctor);
		doctorLeave.setStartDate(doctorLeaveIU.getStartDate());
		doctorLeave.setEndDate(doctorLeaveIU.getEndDate());
		doctorLeave.setReason(doctorLeaveIU.getReason());
		doctorLeave.setLeaveType(doctorLeaveIU.getLeaveType());
		doctorLeave.setStatus(LeaveStatus.ACTIVE);
		
		return doctorLeave;
	}
	
	private boolean isLeaveDateAvailable(Long doctorId, LocalDate startDate, LocalDate endDate) {

	    List<DoctorLeave> doctorLeaveList = doctorLeaveRepository.findByDoctor_IdAndStatus(doctorId,LeaveStatus.ACTIVE);

	    for (DoctorLeave doctorLeave : doctorLeaveList) {
	        if (!(endDate.isBefore(doctorLeave.getStartDate()) || startDate.isAfter(doctorLeave.getEndDate()))) {
	            throw new BaseException(new ErrorMessage(MessageType.DATE_NOT_ENABLE,startDate + " - " + endDate));
	        }
	    }
	    return true;
	}
	
	@Transactional
	@Override
	public DtoDoctorLeave saveDoctorLeave(DtoDoctorLeaveIU doctorLeaveIU) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		Doctor loggedDoctor = doctorRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
		if (!loggedDoctor.getId().equals(doctorLeaveIU.getDoctorId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null) );
		}
		

		if (!loggedDoctor.getIsActive()) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_ENABLE, null) );
		}
		

		isLeaveDateAvailable(doctorLeaveIU.getDoctorId(), doctorLeaveIU.getStartDate(), doctorLeaveIU.getEndDate());
		
		DoctorLeave doctorLeave = createDoctorLeave(doctorLeaveIU);
		
		Doctor doctor = doctorLeave.getDoctor();
		
		if(doctorLeave.getLeaveType()==LeaveType.ANNUAL_LEAVE) {
			

			if (doctorLeaveIU.getStartDate().isEqual(LocalDate.now()) && !LocalTime.now().isBefore(LocalTime.of(9, 0))) {
				throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_CAN_AFTER_ANNUAL, null) );
			}
			else{ 


				long leaveDays = ChronoUnit.DAYS.between(doctorLeaveIU.getStartDate(), doctorLeaveIU.getEndDate()) + 1;
				if(leaveDays > doctor.getRemainingAnnualLeave()) {
					throw new BaseException(new ErrorMessage(MessageType.ANNUAL_LEAVE_NOT_ENAUGH, "KULLANILABILIR YILLIK IZIN : "+doctor.getRemainingAnnualLeave().toString()));
				}
				doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave()-(int)leaveDays);
				Doctor savedDoctor = doctorRepository.save(doctor);
				doctor=savedDoctor;
			}

		}
		else if(doctorLeave.getLeaveType()==LeaveType.EMERGENCY || doctorLeave.getLeaveType()==LeaveType.SICK_LEAVE) {


			if(!LocalTime.now().isBefore(LocalTime.of(17, 0)) && !doctorLeaveIU.getStartDate().isEqual(LocalDate.now().plusDays(1))){


				throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_CAN_AFTER_OTHER, null));
			}
			else if(LocalTime.now().isBefore(LocalTime.of(17, 0)) && !doctorLeaveIU.getStartDate().isEqual(LocalDate.now())) {

				throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_CAN_BEFORE_OTHER, null));
			}
		}
		
		DoctorLeave savedDoctorLeave = doctorLeaveRepository.save(doctorLeave);// DOKTOR RANDEVUYU BURADA ALIYOR 
		



		

		List<AppointmentSlot> slotList = appointmentSlotRepository.findByDoctorIdAndDateBetween(doctorLeaveIU.getDoctorId(),doctorLeaveIU.getStartDate(),doctorLeaveIU.getEndDate());
		

		for (AppointmentSlot slot : slotList) {
		    slot.setStatus(SlotStatus.DOCTOR_LEAVE);
		}
		appointmentSlotRepository.saveAll(slotList);
		
		List<Appointment> appointmentList = appointmentRepository.
				findByAppointmentSlot_Doctor_IdAndAppointmentSlot_DateBetweenAndStatus(doctorLeaveIU.getDoctorId(),doctorLeaveIU.getStartDate(),doctorLeaveIU.getEndDate(),AppointmentStatus.SCHEDULED);
		for (Appointment appointment : appointmentList) {
			appointmentService.cancelledByDoctor(appointment.getId());
			appointment.setStatus(AppointmentStatus.DOCTOR_CANCELLED);
		}
		appointmentRepository.saveAll(appointmentList);
		
		DtoDoctorLeave dtoDoctorLeave = toDtoDoctorLeave(savedDoctorLeave);
				
		return dtoDoctorLeave;
	}
	
	public DtoDoctorLeave toDtoDoctorLeave(DoctorLeave doctorLeave) {
		
		DtoDoctorLeave dtoDoctorLeave = new DtoDoctorLeave();
		DtoUser dtoUser = new DtoUser();
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(doctorLeave.getDoctor().getUser(), dtoUser);
		BeanUtils.copyProperties(doctorLeave.getDoctor(), dtoDoctor);
		BeanUtils.copyProperties(doctorLeave.getDoctor().getDepartment(), dtoDepartment);
		dtoDoctor.setUser(dtoUser);
		dtoDoctor.setDepartment(dtoDepartment);
		BeanUtils.copyProperties(doctorLeave, dtoDoctorLeave);
		dtoDoctorLeave.setDoctor(dtoDoctor);
		
		return dtoDoctorLeave;
	}


	@Override
	public List<DtoDoctorLeave> getAllDoctorLeave(Long doctorId) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		

		Doctor loggedDoctor = doctorRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
		

		if (!loggedDoctor.getId().equals(doctorId)) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		return getDoctorLeaveByDoctor(loggedDoctor);
	}
	
	private List<DtoDoctorLeave> getDoctorLeaveByDoctor(Doctor doctor){
		
		List<DoctorLeave> doctorLeaveList = doctorLeaveRepository.findByDoctor(doctor);
		if(doctorLeaveList.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_NOT_FOUND, null));
		}
		
		return toDtoDoctorLeaveList(doctorLeaveList);
	}

	private List<DtoDoctorLeave> toDtoDoctorLeaveList(List<DoctorLeave> doctorLeaveList){
		List<DtoDoctorLeave> dtoDoctorLeaveList = new ArrayList<>();
		
		for (DoctorLeave doctorLeave : doctorLeaveList) {
			
			DtoDoctorLeave dtoDoctorLeave = new DtoDoctorLeave();
			DtoUser dtoUser = new DtoUser();
			DtoDoctor dtoDoctor = new DtoDoctor();
			DtoDepartment dtoDepartment = new DtoDepartment();
			
			BeanUtils.copyProperties(doctorLeave.getDoctor().getUser(), dtoUser);
			BeanUtils.copyProperties(doctorLeave.getDoctor(), dtoDoctor);
			BeanUtils.copyProperties(doctorLeave.getDoctor().getDepartment(), dtoDepartment);
			dtoDoctor.setDepartment(dtoDepartment);
			dtoDoctor.setUser(dtoUser);
			BeanUtils.copyProperties(doctorLeave, dtoDoctorLeave);
			dtoDoctorLeave.setDoctor(dtoDoctor);
			
			dtoDoctorLeaveList.add(dtoDoctorLeave);
		}
		
		return dtoDoctorLeaveList;
	}
	
	@Transactional
	@Override
	public DtoDoctorLeave updateDoctorLeave(Long id, DtoDoctorLeaveUpdate dto) {
		
		DoctorLeave doctorLeave = doctorLeaveRepository.findById(id)
	            .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_NOT_FOUND, id.toString())));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		Doctor loggedDoctor = doctorRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
		if (!loggedDoctor.getId().equals(doctorLeave.getDoctor().getId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null) );
		}
		
		Doctor doctor = doctorLeave.getDoctor();
		
		if (doctorLeave.getStatus() == LeaveStatus.CANCELLED) {
	        throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_CANNOT_NOTE_FOR_CANCELLED, null));
	    }
		
		if (doctorLeave.getEndDate().isBefore(LocalDate.now())) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_CANNOT_NOTE, null));
		}
		
		/*

		if (dto.getLeaveType() != null && dto.getLeaveType() != doctorLeave.getLeaveType()) {

	        long leaveDays = ChronoUnit.DAYS.between(
	                doctorLeave.getStartDate(),
	                doctorLeave.getEndDate()
	        ) + 1;


	        if (doctorLeave.getLeaveType() == LeaveType.ANNUAL_LEAVE) {
	            doctor.setRemainingAnnualLeave(
	                    doctor.getRemainingAnnualLeave() + (int) leaveDays
	            );
	        }


	        if (dto.getLeaveType() == LeaveType.ANNUAL_LEAVE) {
	            if (leaveDays > doctor.getRemainingAnnualLeave()) {
	                throw new BaseException(
	                        new ErrorMessage(MessageType.ANNUAL_LEAVE_NOT_ENAUGH, "KULLANILABILIR YILLIK IZIN : "+doctor.getRemainingAnnualLeave().toString())
	                );
	            }

	            doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() - (int) leaveDays);
	        }

	        doctorLeave.setLeaveType(dto.getLeaveType());
	        doctorRepository.save(doctor);
	    }
		*/
		doctorLeave.setReason(dto.getReason());
		DoctorLeave updated = doctorLeaveRepository.save(doctorLeave);
	    return toDtoDoctorLeave(updated);
	}

	@Transactional
	@Override
	public void cancelDoctorLeave(Long id) {
		
		DoctorLeave doctorLeave = doctorLeaveRepository.findById(id)
	            .orElseThrow( () -> new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_NOT_FOUND, id.toString())));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		boolean isAdmin = user.getAuthorities()
				.stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		

		if (!isAdmin) {
			Doctor loggedDoctor = doctorRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
			if (!loggedDoctor.getId().equals(doctorLeave.getDoctor().getId())) {
				throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null) );
			}
		}
		
		long leaveDays;
		LocalDate startDate=doctorLeave.getStartDate();
		LocalDate endDate=doctorLeave.getEndDate();
		
		if (doctorLeave.getStatus() == LeaveStatus.CANCELLED) {
		        throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_ALREADY_CANCELLED, null));
		 }

		if (doctorLeave.getEndDate().isBefore(LocalDate.now())) {
			 throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_EXPIRE, null));
		}
		 
		 Doctor doctor = doctorLeave.getDoctor();
		 
		 LocalDate controlDate = LocalDate.now();
		 
		 boolean isBetween = !controlDate.isBefore(doctorLeave.getStartDate()) && !controlDate.isAfter(doctorLeave.getEndDate());
			
		 if (doctorLeave.getLeaveType() == LeaveType.ANNUAL_LEAVE) {
			 if ( controlDate.isEqual(doctorLeave.getStartDate()) || controlDate.isEqual(doctorLeave.getEndDate()) ){// leaveDays eklenecek izin gunu sayisi
				
				if (controlDate.isEqual(doctorLeave.getStartDate())) {
					if (LocalTime.now().isBefore(LocalTime.of(9, 0))) {
						doctorLeave.setStatus(LeaveStatus.CANCELLED);
						leaveDays = ChronoUnit.DAYS.between(doctorLeave.getStartDate(),doctorLeave.getEndDate()) + 1;
						doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + (int) leaveDays);
					}
					else {
						endDate=doctorLeave.getStartDate();// bitiş günü ise saat 9 dan sonra ıptal et dedıgınden 1 gunluk izin kullanilmis olur
						leaveDays = ChronoUnit.DAYS.between(doctorLeave.getStartDate().plusDays(1),doctorLeave.getEndDate()) + 1;
						doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + (int) leaveDays);
						doctorLeave.setEndDate(endDate);// artik bitis gununu de guncellemis olduk
					}
				} 
				else if (controlDate.isEqual(doctorLeave.getEndDate())) {
					if (!LocalTime.now().isBefore(LocalTime.of(9, 0))) {
						throw new BaseException(new ErrorMessage(MessageType.DOCTOR_LEAVE_EXPIRE, null));
					}
					else {
						endDate=doctorLeave.getEndDate().minusDays(1);
						doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + 1);
						doctorLeave.setEndDate(endDate);
					}
				}

			}
			else if(isBetween) {

				 
				if (LocalTime.now().isBefore(LocalTime.of(9, 0))) {
					endDate=controlDate.minusDays(1);
					leaveDays = ChronoUnit.DAYS.between(controlDate,doctorLeave.getEndDate()) + 1;
					doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + (int) leaveDays);
					doctorLeave.setEndDate(endDate);
				}
				else {
					endDate=controlDate;
					leaveDays = ChronoUnit.DAYS.between(controlDate.plusDays(1),doctorLeave.getEndDate()) + 1;
					doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + (int) leaveDays);
					doctorLeave.setEndDate(endDate);
				}
				
			}
			else {
				doctorLeave.setStatus(LeaveStatus.CANCELLED);
				leaveDays = ChronoUnit.DAYS.between(doctorLeave.getStartDate(),doctorLeave.getEndDate()) + 1;
				doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + (int) leaveDays);

			}
			 
		 }
		 else if(doctorLeave.getLeaveType()==LeaveType.EMERGENCY || doctorLeave.getLeaveType()==LeaveType.SICK_LEAVE) {

			 
			if (LocalTime.now().isBefore(LocalTime.of(9, 0))) {
				if (doctorLeave.getStartDate().isEqual(controlDate)) {
					doctorLeave.setStatus(LeaveStatus.CANCELLED);
				}
				else {
					doctorLeave.setEndDate(controlDate.minusDays(1));
				}
			}
			else {
				doctorLeave.setEndDate(controlDate);
			}
			 
		 }
		 
		 availableSlotStatus(doctor,startDate,endDate);
		 
		 doctorRepository.save(doctor);
		 doctorLeaveRepository.save(doctorLeave);  
	}
	
	private void availableSlotStatus(Doctor doctor,LocalDate startDate,LocalDate endDate) {

	    List<AppointmentSlot> slots = appointmentSlotRepository.findByDoctorIdAndDateBetween(
	                    doctor.getId(),
	                    startDate,
	                    endDate
	            );
	    
	    for (AppointmentSlot slot : slots) {
	        slot.setStatus(SlotStatus.AVAILABLE);
	    }
	    appointmentSlotRepository.saveAll(slots);
	}

	@Override
	public List<DtoDoctorLeave> getAllDoctorLeaveForAdmin(Long doctorId,LocalDate startDate,LocalDate endDate) {
		
		List<Doctor> doctorList = doctorRepository.findAll();
		List<DoctorLeave> doctorLeaveList = doctorLeaveRepository.findByDoctorId(doctorId);
		List<DoctorLeave> allDoctorLeaveList = doctorLeaveRepository.findAll();

		if (doctorId!=null && startDate!=null && endDate!=null) {
			
			return doctorLeaveList.stream()
					.filter( leave -> !leave.getStartDate().isAfter(endDate) && !leave.getEndDate().isBefore(startDate) )
					.map(this::toDtoDoctorLeave)
					.toList();
		}
		else if (doctorId!=null) {
			
			return doctorLeaveList.stream()
					.map(this::toDtoDoctorLeave)
					.toList();
		}
		else if (startDate != null && endDate != null) {
			
			return allDoctorLeaveList.stream()
					.filter( leave -> !leave.getStartDate().isAfter(endDate) && !leave.getEndDate().isBefore(startDate) )
					.map(this::toDtoDoctorLeave)
					.toList();
					
		} 
		




		return doctorList.stream()
				.flatMap(doctor->getDoctorLeaveByDoctor(doctor).stream())
				.toList();
	}
	
	/*
	private void cancelAllLeave(DoctorLeave doctorLeave,Doctor doctor) {

		long leaveDays = ChronoUnit.DAYS.between(doctorLeave.getStartDate(),doctorLeave.getEndDate()) + 1;
		doctor.setRemainingAnnualLeave(doctor.getRemainingAnnualLeave() + (int) leaveDays);
		

	    List<AppointmentSlot> slots = appointmentSlotRepository.findByDoctorIdAndDateBetween(
	                    doctor.getId(),
	                    doctorLeave.getStartDate(),
	                    doctorLeave.getEndDate()
	            );
	    
	    for (AppointmentSlot slot : slots) {
	        slot.setStatus(SlotStatus.AVAILABLE);
	    }
		
	    doctorLeave.setStatus(LeaveStatus.CANCELLED);
	    doctorRepository.save(doctor);
	    appointmentSlotRepository.saveAll(slots);
	    doctorLeaveRepository.save(doctorLeave);  		
	}
	*/
	
}
