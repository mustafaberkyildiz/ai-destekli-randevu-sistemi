package com.mustafaberk.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfigurationSource;
import com.mustafaberk.dto.DtoAppointment;
import com.mustafaberk.dto.DtoAppointmentIU;
import com.mustafaberk.dto.DtoAppointmentPatient;
import com.mustafaberk.dto.DtoAppointmentSlot;
import com.mustafaberk.dto.DtoAppointmentUpdate;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.enums.SlotStatus;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.Patient;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.*;
import com.mustafaberk.service.IAppointmentService;
import com.mustafaberk.service.MailService;


@Service
public class AppointmentServiceImpl implements IAppointmentService{

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecurityFilterChain filterChain;

    private final CorsConfigurationSource corsConfigurationSource;

    private final AIRecommendationRepository AIRecommendationRepository;

	@Autowired
	private AppointmentSlotRepository appointmentSlotRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private DoctorReviewRepository doctorReviewRepository;
	
    AppointmentServiceImpl(AIRecommendationRepository AIRecommendationRepository, CorsConfigurationSource corsConfigurationSource, SecurityFilterChain filterChain, RefreshTokenRepository refreshTokenRepository) {
        this.AIRecommendationRepository = AIRecommendationRepository;
        this.corsConfigurationSource = corsConfigurationSource;
        this.filterChain = filterChain;
        this.refreshTokenRepository = refreshTokenRepository;
    }
	
	public DtoAppointment generateDtoAppointment(Appointment appointment) {
		
		DtoAppointment dtoAppointment = new DtoAppointment();
		DtoPatient dtoPatient = new DtoPatient();
		DtoUser dtoUserPatient = new DtoUser();
		DtoUser dtoUserDoctor = new DtoUser();
		DtoAppointmentSlot dtoAppointmentSlot = new DtoAppointmentSlot();
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(appointment, dtoAppointment);
		BeanUtils.copyProperties(appointment.getAppointmentSlot(), dtoAppointmentSlot);
		BeanUtils.copyProperties(appointment.getAppointmentSlot().getDoctor(), dtoDoctor);
		BeanUtils.copyProperties(appointment.getAppointmentSlot().getDoctor().getUser(), dtoUserDoctor);
	    BeanUtils.copyProperties(appointment.getAppointmentSlot().getDoctor().getDepartment(), dtoDepartment);
		BeanUtils.copyProperties(appointment.getPatient(), dtoPatient);
		BeanUtils.copyProperties(appointment.getPatient().getUser(), dtoUserPatient);
		
		
		dtoDoctor.setUser(dtoUserDoctor);
		dtoDoctor.setDepartment(dtoDepartment);
		dtoAppointmentSlot.setDoctor(dtoDoctor);
		dtoPatient.setUser(dtoUserPatient);
		dtoAppointment.setAppointmentSlot(dtoAppointmentSlot);
		dtoAppointment.setPatient(dtoPatient);
		
		return dtoAppointment;
	}
	
	private AppointmentSlot existAppointmentSlot(Long appointmentSlotId) {
		
		Optional<AppointmentSlot> optionalAppointmentSlot = appointmentSlotRepository.findById(appointmentSlotId);
		if (optionalAppointmentSlot.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_SLOT_NOT_FOUND, appointmentSlotId.toString()));
		}
		
		AppointmentSlot appointmentSlot = optionalAppointmentSlot.get();
		

		if(appointmentSlot.getStatus()!=SlotStatus.AVAILABLE) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_SLOT_NOT_SUITABLE, appointmentSlotId.toString()));
		}
		

		boolean bookedSchedule = appointmentRepository.existsByAppointmentSlotAndStatus(appointmentSlot, AppointmentStatus.SCHEDULED);

		
		boolean bookedCompleted = appointmentRepository.existsByAppointmentSlotAndStatus(appointmentSlot, AppointmentStatus.COMPLETED);
		boolean bookedNoShow = appointmentRepository.existsByAppointmentSlotAndStatus(appointmentSlot, AppointmentStatus.NO_SHOW);

		if(bookedSchedule==true) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_SLOT_ALREADY_EXIST, null));
		}
		if (bookedNoShow==true || bookedCompleted==true) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_SLOT_EXPIRE, null));
		}
		
		return appointmentSlot;
	}
	
	@Override
	public DtoAppointment saveAppointment(DtoAppointmentIU dto) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		Patient loggedPatient = patientRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) ); 
		if (!loggedPatient.getId().equals(dto.getPatientId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		AppointmentSlot appointmentSlot = existAppointmentSlot(dto.getAppointmentSlotId());
		

		DoctorServiceImpl.validateDoctorActive(appointmentSlot.getDoctor());
		

		LocalDateTime appointmenDateTime = LocalDateTime.of(appointmentSlot.getDate(), appointmentSlot.getStartTime());
		if (LocalDateTime.now().isAfter(appointmenDateTime)) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_SLOT_EXPIRE, null));
		}
		
		Appointment appointment = new Appointment();
		if (dto.getNote() != null && !dto.getNote().isBlank()) {
		    appointment.setNote(dto.getNote());
		}
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		appointment.setPatient(loggedPatient);
		appointment.setAppointmentSlot(appointmentSlot);
		
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		DtoAppointment dtoAppointment = generateDtoAppointment(savedAppointment);
		
		AppointmentSlot appointmentSlot2 = savedAppointment.getAppointmentSlot();
		User userPatient = savedAppointment.getPatient().getUser();
		User userDoctor = savedAppointment.getAppointmentSlot().getDoctor().getUser();
		Doctor doctor = savedAppointment.getAppointmentSlot().getDoctor();
		
		mailService.sendAppointmentConfirmedMailToPatient(userPatient.getEmail(), userPatient.getName(), userPatient.getSurname(), 
														  userDoctor.getName(), userDoctor.getSurname(), doctor.getDepartment().getName() , 
														  appointmentSlot2.getDate(), appointmentSlot2.getStartTime());
		
		return dtoAppointment;
	}

	private Appointment existAppointment(Long appointmentId) {
		Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
		if(optionalAppointment.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, appointmentId.toString()));
		}
		
		Appointment appointment = optionalAppointment.get();
		if(appointment.getStatus()!=AppointmentStatus.SCHEDULED) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_CANNOT_CANCELLED, appointmentId.toString()));
		}
		

		LocalDateTime localDateTime = LocalDateTime.of(appointment.getAppointmentSlot().getDate(), appointment.getAppointmentSlot().getStartTime());
		if (localDateTime.isBefore(LocalDateTime.now())) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_TIME_EXPIRE_NOT_CANCEL, null));
		}
		
		return appointment;
	}
	
	@Transactional
	@Override
	public DtoAppointment cancelledByDoctor(Long appointmentId) {
		
		Appointment appointment = existAppointment(appointmentId);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User loggedUser = (User) authentication.getDetails();
		
		Doctor loggedDoctor = doctorRepository.findByUser(loggedUser).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
		if (!loggedDoctor.getId().equals(appointment.getAppointmentSlot().getDoctor().getId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		appointment.setStatus(AppointmentStatus.DOCTOR_CANCELLED);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		DtoAppointment dtoAppointment = generateDtoAppointment(savedAppointment);
		
		AppointmentSlot appointmentSlot = savedAppointment.getAppointmentSlot();
		User user = savedAppointment.getPatient().getUser();
		
		mailService.sendAppointmentCancelledMailByDoctor(user.getEmail(), user.getName(), user.getSurname(), 
														 appointmentSlot.getDate(), appointmentSlot.getStartTime());
		
		return dtoAppointment;
	}

	@Transactional
	@Override
	public DtoAppointment cancelledByPatient(Long appointmentId) {
		
		Appointment appointment = existAppointment(appointmentId);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User loggedUser = (User) authentication.getDetails();
		
		Patient loggedPatient = patientRepository.findByUser(loggedUser).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) );
		if (!loggedPatient.getId().equals(appointment.getPatient().getId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		appointment.setStatus(AppointmentStatus.CANCELLED);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		DtoAppointment dtoAppointment = generateDtoAppointment(savedAppointment);
		
		AppointmentSlot appointmentSlot = savedAppointment.getAppointmentSlot();
		User user = appointmentSlot.getDoctor().getUser();
		
		mailService.sendAppointmentCancelledMailByPatient(user.getEmail(), user.getName(), user.getSurname(), 
				 appointmentSlot.getDate(), appointmentSlot.getStartTime());
		
		return dtoAppointment;
	}

	@Transactional
	@Override
	public DtoAppointment updateAppointment(Long id, DtoAppointmentUpdate dto) {
		
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, id.toString())) );
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User loggedUser = (User) authentication.getDetails();
		
		Patient loggedPatient = patientRepository.findByUser(loggedUser).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) );
		if (!loggedPatient.getId().equals(appointment.getPatient().getId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		

		if (!(appointment.getStatus()==AppointmentStatus.SCHEDULED || appointment.getStatus()==AppointmentStatus.COMPLETED)) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_STATUS_NOT_SUITABLE_FOR_UPDATE, null));
		}
		
		if (dto.getNote()!=null) {
			if (dto.getNote().trim().isEmpty()) {
				throw new BaseException(new ErrorMessage(MessageType.INVALID_NOTE, null));
			}
			appointment.setNote(dto.getNote());
		}
		Appointment updatedAppointment = appointmentRepository.save(appointment);
		
		return generateDtoAppointment(updatedAppointment);
	}

	@Transactional
	@Override
	public DtoAppointment completeAppointment(Long appointmentId) {
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, appointmentId.toString())) )  ;
		
		validCompleteAppointment(appointment);
		appointment.setStatus(AppointmentStatus.COMPLETED);
		
		Appointment updatedAppointment = appointmentRepository.save(appointment);
		
		return generateDtoAppointment(updatedAppointment);
	}

	private void validCompleteAppointment(Appointment appointment) {
		
		if(appointment.getStatus()!=AppointmentStatus.SCHEDULED) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_STATUS_NOT_VALID, null));
		}
		
		LocalDateTime appointmentStartDateTime  = LocalDateTime.of(appointment.getAppointmentSlot().getDate(), appointment.getAppointmentSlot().getStartTime());

		if (appointmentStartDateTime.isAfter(LocalDateTime.now())) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_TIME_NOT_YET, null));
		}
		LocalDateTime appointmentExpireDateTime = LocalDateTime.of(appointment.getAppointmentSlot().getDate(), appointment.getAppointmentSlot().getEndTime().plusMinutes(10));


		if (appointmentExpireDateTime.isBefore(LocalDateTime.now())) {
			throw new BaseException(new ErrorMessage(MessageType.APPOINTMENT_TIME_EXPIRE, null));
		}
				
	}
	

	@Transactional
	@Override
	public DtoAppointment noshowAppointment(Long appointmentId) {
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, appointmentId.toString())) )  ;
		
		validCompleteAppointment(appointment);
		appointment.setStatus(AppointmentStatus.NO_SHOW);
		
		Appointment updatedAppointment = appointmentRepository.save(appointment);
		
		return generateDtoAppointment(updatedAppointment);
	}

	private DtoAppointmentPatient generateDtoAppointmentPatient(Appointment appointment) {
		
		DtoAppointmentPatient dtoAppointmentPatient = new DtoAppointmentPatient();
		DtoPatient dtoPatient = new DtoPatient();
		DtoUser dtoUserPatient = new DtoUser();
		DtoUser dtoUserDoctor = new DtoUser();
		DtoAppointmentSlot dtoAppointmentSlot = new DtoAppointmentSlot();
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(appointment, dtoAppointmentPatient);
		BeanUtils.copyProperties(appointment.getAppointmentSlot(), dtoAppointmentSlot);
		BeanUtils.copyProperties(appointment.getAppointmentSlot().getDoctor(), dtoDoctor);
		BeanUtils.copyProperties(appointment.getAppointmentSlot().getDoctor().getUser(), dtoUserDoctor);
	    BeanUtils.copyProperties(appointment.getAppointmentSlot().getDoctor().getDepartment(), dtoDepartment);
		BeanUtils.copyProperties(appointment.getPatient(), dtoPatient);
		BeanUtils.copyProperties(appointment.getPatient().getUser(), dtoUserPatient);
		
		
		dtoDoctor.setUser(dtoUserDoctor);
		dtoDoctor.setDepartment(dtoDepartment);
		dtoAppointmentSlot.setDoctor(dtoDoctor);
		dtoPatient.setUser(dtoUserPatient);
		dtoAppointmentPatient.setAppointmentSlot(dtoAppointmentSlot);
		dtoAppointmentPatient.setPatient(dtoPatient);
		

		if (doctorReviewRepository.existsByAppointment(appointment)) {
			dtoAppointmentPatient.setCanUpdateReview(true);// eger bu randevunun onceden bir yorumu varsa guncelle butonu ciksin
		}
		else {
			dtoAppointmentPatient.setCanUpdateReview(false);// eger bu randevunun onceden bir yorumu yoksa degerlendir butonu ciksin
		}
		
		return dtoAppointmentPatient;
	}
	
	@Override
	public List<DtoAppointmentPatient> getPatientAppointments(Long id) {	
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		Patient loggedPatient = patientRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) ); 
		if (!loggedPatient.getId().equals(id)) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		


	    return appointmentRepository.findByPatient_Id(id).stream()
	            .map(this::generateDtoAppointmentPatient)
	            .collect(Collectors.toList());

	}
	
	@Override
	public List<DtoAppointment> getDoctorAppointments(Long id) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		Doctor loggedDoctor = doctorRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) ); 
		if (!loggedDoctor.getId().equals(id)) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		 return appointmentRepository.findByAppointmentSlot_Doctor_Id(id).stream()
		            .map(this::generateDtoAppointment)
		            .collect(Collectors.toList());
	}

	@Override
	public void markPastScheduledAppointmentsAsNoShow() {
		
		List<Appointment> appointments = appointmentRepository.findAll();
		
		for (Appointment appointment : appointments) {


			if (appointment.getStatus() == AppointmentStatus.SCHEDULED) {
				LocalDateTime localDateTime = LocalDateTime.of(appointment.getAppointmentSlot().getDate(), appointment.getAppointmentSlot().getEndTime().plusMinutes(10));
				if (LocalDateTime.now().isAfter(localDateTime)) {
					appointment.setStatus(AppointmentStatus.NO_SHOW);
					appointmentRepository.save(appointment);
				}
			}
		}
		
	}

	@Override
	public void isCompletableAppointment(Long appointmentId) {
		
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, null)) );
		
		validCompleteAppointment(appointment);
		
	}

	@Override
	public List<DtoAppointment> getAllAppointment() {

		List<Appointment> appointmentList = appointmentRepository.findAll();
		
		return appointmentList.stream()
				.map(this::generateDtoAppointment)
				.toList();
		
	}

	@Override
	public List<DtoAppointment> getDoctorAppointmentsForAdmin(Long id) {
		
		doctorRepository.findById(id).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null))  );
		
		return appointmentRepository.findByAppointmentSlot_Doctor_Id(id).stream()
				.map(this::generateDtoAppointment)
				.toList();
	}

	@Override
	public DtoAppointment cancelledByAdmin(Long appointmentId) {
		
		Appointment appointment = existAppointment(appointmentId);
		
		appointment.setStatus(AppointmentStatus.CANCELLED);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		
		DtoAppointment dtoAppointment = generateDtoAppointment(savedAppointment);
		
		AppointmentSlot appointmentSlot = savedAppointment.getAppointmentSlot();
		User user = savedAppointment.getPatient().getUser();
		
		mailService.sendAppointmentCancelledMailByAdmin(user.getEmail(), user.getName(), user.getSurname(),
				appointmentSlot.getDate(), appointmentSlot.getStartTime());
		
		return dtoAppointment;
	}

	@Override
	public List<DtoAppointment> getAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate) {

		List<Appointment> appointmentList = appointmentRepository.findByAppointmentSlot_DateBetween(startDate, endDate);
	
		return appointmentList.stream()
				.map(this::generateDtoAppointment)
				.toList();
	}
	
}
