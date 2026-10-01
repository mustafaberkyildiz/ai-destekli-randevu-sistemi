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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.mustafaberk.config.AiConfig;
import com.mustafaberk.dto.DtoAppointment;
import com.mustafaberk.dto.DtoAppointmentSlot;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorReview;
import com.mustafaberk.dto.DtoDoctorReviewIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientUpdate;
import com.mustafaberk.dto.DtoShowDoctor;
import com.mustafaberk.dto.DtoShowPatient;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.enums.LeaveStatus;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.DoctorLeave;
import com.mustafaberk.model.DoctorReview;
import com.mustafaberk.model.Patient;
import com.mustafaberk.model.RefreshToken;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.*;
import com.mustafaberk.service.IAppointmentService;
import com.mustafaberk.service.IDoctorLeaveService;
import com.mustafaberk.service.IDoctorService;

import io.netty.handler.codec.quic.QuicPathEvent.New;
import jakarta.transaction.Transactional;

@Service
public class DoctorServiceImpl implements IDoctorService{

    private final AiConfig aiConfig;

    private final AppointmentRepository appointmentRepository;

    private final RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private DoctorReviewRepository doctorReviewRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private IAppointmentService appointmentService;
	
	@Autowired
	private AppointmentSlotRepository appointmentSlotRepository;
	
	@Autowired
	private IDoctorLeaveService doctorLeaveService;
	
	@Autowired
	private DoctorLeaveRepository doctorLeaveRepository;

    DoctorServiceImpl(RefreshTokenRepository refreshTokenRepository, AppointmentRepository appointmentRepository, AiConfig aiConfig) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.appointmentRepository = appointmentRepository;
        this.aiConfig = aiConfig;
    }
	
	@Override
	public DtoDoctor getDoctorById(Long id) {
		Optional<Doctor> optional = doctorRepository.findById(id);
		if (optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null));
		}
		
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoUser dtoUser = new DtoUser();
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(optional.get().getUser(), dtoUser);
		BeanUtils.copyProperties(optional.get(), dtoDoctor);
		BeanUtils.copyProperties(optional.get().getDepartment(), dtoDepartment);
		
		dtoDoctor.setUser(dtoUser);
		dtoDoctor.setDepartment(dtoDepartment);
		
		return dtoDoctor;
	}

	@Override
	public List<DtoShowDoctor> getAllDoctor() {

		List<Doctor> allDoctorList = doctorRepository.findAll();
		List<DtoShowDoctor> dtoShowDoctorList = new ArrayList<>();
		
		for (Doctor doctor : allDoctorList) {
			DtoShowDoctor dtoShowDoctor = new DtoShowDoctor();
			DtoDoctor dtoDoctor = new DtoDoctor();
			DtoUser dtoUser = new DtoUser();
			DtoDepartment dtoDepartment = new DtoDepartment();
			
			BeanUtils.copyProperties(doctor, dtoDoctor);
			BeanUtils.copyProperties(doctor.getUser(), dtoUser);
			BeanUtils.copyProperties(doctor.getDepartment(), dtoDepartment);
			
			dtoDoctor.setUser(dtoUser);
			dtoDoctor.setDepartment(dtoDepartment);
			dtoShowDoctor.setDoctor(dtoDoctor);
			
			LocalDateTime localDateTime = refreshTokenRepository.findTopByUser_IdOrderByCreateTimeDesc(doctor.getUser().getId())
					.map(RefreshToken::getCreateTime).orElse(null);
			dtoShowDoctor.setLastLoginDateTime(localDateTime);
			
			dtoShowDoctorList.add(dtoShowDoctor);
		}
		
		return dtoShowDoctorList;	
	}

	@Override
	public List<Doctor> getDoctorByDepartment(Department department) {
				
		List<Doctor> doctors = doctorRepository.findByDepartmentAndIsActive(department,true);
		
		return doctors;
	}

	@Override
	public Double getDoctorAverageScore(Long doctorId) {
		Double avgScore = doctorReviewRepository.getAverageScoreByDoctorId(doctorId);
		
		return ( avgScore!=null ) ? avgScore : 0.0;
	}

	private DtoDoctorReview generateDtoDoctorReview(DoctorReview doctorReview) {
		
		DtoDoctorReview dtoDoctorReview = new DtoDoctorReview();
		
		DtoAppointment dtoAppointment = new DtoAppointment();
		DtoPatient dtoPatient = new DtoPatient();
		DtoUser dtoUserPatient = new DtoUser();
		DtoUser dtoUserDoctor = new DtoUser();
		DtoAppointmentSlot dtoAppointmentSlot = new DtoAppointmentSlot();
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(doctorReview, dtoDoctorReview);
		BeanUtils.copyProperties(doctorReview.getAppointment(), dtoAppointment);
		BeanUtils.copyProperties(doctorReview.getAppointment().getAppointmentSlot(), dtoAppointmentSlot);
		BeanUtils.copyProperties(doctorReview.getAppointment().getAppointmentSlot().getDoctor(), dtoDoctor);
		BeanUtils.copyProperties(doctorReview.getAppointment().getAppointmentSlot().getDoctor().getUser(), dtoUserDoctor);
	    BeanUtils.copyProperties(doctorReview.getAppointment().getAppointmentSlot().getDoctor().getDepartment(), dtoDepartment);
		BeanUtils.copyProperties(doctorReview.getAppointment().getPatient(), dtoPatient);
		BeanUtils.copyProperties(doctorReview.getAppointment().getPatient().getUser(), dtoUserPatient);
		
		dtoDoctor.setUser(dtoUserDoctor);
		dtoDoctor.setDepartment(dtoDepartment);
		dtoAppointmentSlot.setDoctor(dtoDoctor);
		dtoPatient.setUser(dtoUserPatient);
		dtoAppointment.setAppointmentSlot(dtoAppointmentSlot);
		dtoAppointment.setPatient(dtoPatient);
		
		dtoDoctorReview.setDtoAppointment(dtoAppointment);

		return dtoDoctorReview;
	}
	
	@Override
	public DtoDoctorReview saveDoctorReview(DtoDoctorReviewIU doctorReview) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();	
		
		Patient loggerPatient = patientRepository.findByUser(user)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) );  
		
		Appointment appointment =	appointmentRepository.findById(doctorReview.getAppointmentId())
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, null)) );
		
		if ( !loggerPatient.getId().equals(appointment.getPatient().getId()) ){
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		

		if (appointment.getStatus()!=AppointmentStatus.COMPLETED) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_DOCTOR_REVIEW,null));
		}
		

		if (LocalDate.now().isAfter(appointment.getAppointmentSlot().getDate().plusDays(7))) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_REVIEW_CREATE_EXPIRE,null));
		}
		
		DoctorReview newDoctorReview = new DoctorReview();
		BeanUtils.copyProperties(doctorReview, newDoctorReview);
		newDoctorReview.setAppointment(appointment);
		
		DoctorReview savedDoctorReview = doctorReviewRepository.save(newDoctorReview);
				
		return generateDtoDoctorReview(savedDoctorReview);
	}

	@Override
	public DtoDoctorReview updateDoctorReview(DtoDoctorReviewIU doctorReview) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();	
		
		Patient loggerPatient = patientRepository.findByUser(user)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) );  
		
		Appointment appointment =	appointmentRepository.findById(doctorReview.getAppointmentId())
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.APPOINTMENT_NOT_FOUND, null)) );
		
		if ( !loggerPatient.getId().equals(appointment.getPatient().getId()) ){
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		DoctorReview loggerDoctorReview = doctorReviewRepository.findByAppointment(appointment);
		if (loggerDoctorReview.isUpdateUsed()) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_UPDATE_DOCTOR_REVIEW, null));
		}
		

		if (LocalDateTime.now().isAfter(loggerDoctorReview.getCreateTime().plusDays(7))) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_REVIEW_EXPIRE, null)); 
		}
		
		loggerDoctorReview.setScore(doctorReview.getScore());
		loggerDoctorReview.setComment(doctorReview.getComment());
		loggerDoctorReview.setUpdateUsed(true);

		DoctorReview savedDoctorReview = doctorReviewRepository.save(loggerDoctorReview);
		
		return generateDtoDoctorReview(savedDoctorReview);
	}

	@Override
	public List<DtoDoctorReview> getAllDoctorReview(Long doctorId,Integer minScore,LocalDate startDate,LocalDate endDate) {


		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		boolean isAdmin = user.getAuthorities()
				.stream()
				.anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
		

		if (!isAdmin) {
			Doctor loggedDoctor = doctorRepository.findByUser(user).orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
			if (!loggedDoctor.getId().equals(doctorId)) {
				throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
			}
		}
		
		List<DoctorReview> reviews = doctorReviewRepository.findAll();


	    if (doctorId != null) {

	        reviews = reviews.stream()
	                .filter(x -> x.getAppointment().getAppointmentSlot().getDoctor().getId().equals(doctorId))
	                .toList();
	    }


	    if (minScore != null) {

	        reviews = reviews.stream()
	                .filter(x -> x.getScore() >= minScore)
	                .toList();
	    }


	    if (startDate != null && endDate != null) {

	        reviews = reviews.stream()
	                .filter(x ->
	                        !x.getCreateTime().toLocalDate().isBefore(startDate)
	                        &&
	                        !x.getCreateTime().toLocalDate().isAfter(endDate)
	                )
	                .toList();
	    }

		return reviews.stream()
				.map(this::generateDtoDoctorReview)
				.toList();
	}

	@Override
	public List<DtoDoctorReview> getAllDoctorReviewForPatient(Long patientId, Integer minScore, LocalDate startDate,LocalDate endDate) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		
		Patient loggedPatient = patientRepository.findByUser(user).orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) );
		if (!loggedPatient.getId().equals(patientId)) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		List<DoctorReview> reviews = doctorReviewRepository.findAll();
		
		if (minScore!=null) {
			
			reviews = reviews.stream()
					.filter(x -> x.getScore() >= minScore)
					.toList();
		}
		
		if (startDate!=null && endDate!=null) {
			
			reviews = reviews.stream()
					.filter(x ->
							!x.getCreateTime().toLocalDate().isBefore(startDate) 
							&&
							!x.getCreateTime().toLocalDate().isAfter(endDate)
							)
					.toList();
		}

		
		return reviews.stream()
				.map(this::generateDtoDoctorReview)
				.toList();
	}
	
	public static void validateDoctorActive(Doctor doctor) {
	    if (!doctor.getIsActive()) {
	        throw new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_ENABLE, null) );
	    }
	}
	
	public static boolean isActive(Doctor doctor) {
		if (doctor.getIsActive()) {
			return true;
		}
		return false;
	}

	
	@Override
	public void disableDoctor(Long doctorId) {
		
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
		
		if (!doctor.getIsActive()) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_ALREADY_DISABLE, null));
		}
				
		List<DoctorLeave> doctorLeaveList = doctorLeaveRepository.
				findByDoctorAndStatusAndEndDateGreaterThanEqual(doctor, LeaveStatus.ACTIVE, LocalDate.now() );
		
		for (DoctorLeave leave : doctorLeaveList) {

		    boolean isLastDay = leave.getEndDate().equals(LocalDate.now());

		    boolean after9am = !LocalTime.now().isBefore(LocalTime.of(9, 0));// 9 veya sonra ise 

		    if (isLastDay && after9am) {
		        continue; 
		    }

		    doctorLeaveService.cancelDoctorLeave(leave.getId());
		}
		
		List<Appointment> appointmentList = appointmentRepository.findFutureAppointmentsFromDateTimeForDoctor(doctor, LocalDate.now(), LocalTime.now());




		
		for (Appointment appointment : appointmentList) {
			if (appointment.getStatus()==AppointmentStatus.SCHEDULED) {
				appointmentService.cancelledByAdmin(appointment.getId());
			}
		}
		
		doctor.setIsActive(false);
		doctorRepository.save(doctor);
	}

	@Override
	public void enableDoctor(Long doctorId) {
		
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );
		
		if (doctor.getIsActive()) {
			throw new BaseException(new ErrorMessage(MessageType.DOCTOR_ALREADY_ENABLE, null));
		}
		
		doctor.setIsActive(true);
		doctorRepository.save(doctor);
		
	}

	
}
