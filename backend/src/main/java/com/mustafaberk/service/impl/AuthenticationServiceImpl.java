package com.mustafaberk.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.usertype.UserType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.AuthRequest;
import com.mustafaberk.dto.AuthRequestUser;
import com.mustafaberk.dto.AuthResponse;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientIU;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.dto.RefreshTokenRequest;
import com.mustafaberk.dto.UserIds;
import com.mustafaberk.enums.Role;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.jwt.JwtService;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.Patient;
import com.mustafaberk.model.RefreshToken;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.DepartmentRepository;
import com.mustafaberk.repository.DoctorRepository;
import com.mustafaberk.repository.PatientRepository;
import com.mustafaberk.repository.RefreshTokenRepository;
import com.mustafaberk.repository.UserRepository;
import com.mustafaberk.service.IAuthenticationService;
import com.mustafaberk.starter.ProjeApplicationStarter;
import jakarta.transaction.Transactional;


@Service
public class AuthenticationServiceImpl implements IAuthenticationService{

    private final SecurityFilterChain filterChain;

    private final ProjeApplicationStarter projeApplicationStarter;

	@Autowired
    private DepartmentRepository departmentRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Autowired 
	private JwtService jwtService;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private UserDetailsService userDetailsService;

    AuthenticationServiceImpl(DepartmentRepository departmentRepository, ProjeApplicationStarter projeApplicationStarter, SecurityFilterChain filterChain) {
        this.departmentRepository = departmentRepository;
        this.projeApplicationStarter = projeApplicationStarter;
        this.filterChain = filterChain;
    }
	
	private User createDoctorUser(AuthRequestUser authRequest) {
		User user = createUserBase(authRequest);
		user.setRole(Role.DOCTOR);
		
		return user;
	}

	private User createUserBase(AuthRequestUser authRequest) {
		User user = new User();//setCreateTime YOK ❌ Hibernate kendisi koyuyor ✔️ @CreationTimestamp sayesinde bunu biz koymustuk BaseEntity'de createTime ustune
		
		if(userRepository.existsByEmail(authRequest.getEmail())) {
			throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_REGISTERED, null));
		}
		
		if (userRepository.existsByPhone(authRequest.getPhone())) {
			throw new BaseException(new ErrorMessage(MessageType.PHONE_ALREADY_REGISTERED, null));
		}
		
		user.setTc(authRequest.getTc());
		user.setEmail(authRequest.getEmail());
		user.setName(authRequest.getName());
		user.setSurname(authRequest.getSurname());
		user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
		user.setPhone(authRequest.getPhone());
		
		return user;
	}
	
	private User createPatientUser(AuthRequestUser authRequest) {
		User user = createUserBase(authRequest);
		user.setRole(Role.PATIENT);
		
		return user;
	}
	
	private Doctor createDoctor(DtoDoctorIU dtoDoctorIU) {
		Doctor doctor = new Doctor();
		
		Optional<Department> optional = departmentRepository.findById(dtoDoctorIU.getDepartmentId());
		if (optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.DEPARTMENT_NOT_FOUND, null));
		}
		
		Department department = optional.get();
		doctor.setDepartment(department);
		

		
		doctor.setTitle(dtoDoctorIU.getTitle());


		
		return doctor;
	}
	
	private Patient createPatient(DtoPatientIU dtoPatientIU) {
		Patient patient = new Patient();
		
		patient.setBirthDate(dtoPatientIU.getBirthDate());
		patient.setBloodType(dtoPatientIU.getBloodType());
		patient.setGender(dtoPatientIU.getGender());
		patient.setHeight(dtoPatientIU.getHeight());
		patient.setWeight(dtoPatientIU.getWeight());		
		
		return patient;
	}
	
	@Transactional
	@Override
	public DtoDoctor registerDoctor(DtoDoctorIU dtoDoctorIU) {

		

		if(userRepository.existsByTc(dtoDoctorIU.getAuthRequestUser().getTc())) {
			throw new BaseException(new ErrorMessage(MessageType.TC_ALREADY_REGISTERED, null));
		}				
		
		User user = createDoctorUser(dtoDoctorIU.getAuthRequestUser());
		
		Doctor doctor = createDoctor(dtoDoctorIU);
		
		User savedUser = userRepository.save(user);
		
		doctor.setUser(savedUser);
		
		Doctor savedDoctor = doctorRepository.save(doctor);
		
		DtoDoctor dtoDoctor = new DtoDoctor();
		DtoUser dtoUser = new DtoUser();
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(savedDoctor, dtoDoctor);
		BeanUtils.copyProperties(savedDoctor.getDepartment(), dtoDepartment);
		BeanUtils.copyProperties(savedDoctor.getUser(), dtoUser);
		
		dtoDoctor.setDepartment(dtoDepartment);
		dtoDoctor.setUser(dtoUser);
		
		return dtoDoctor;
	}
	
	@Transactional
	@Override
	public DtoPatient registerPatient(DtoPatientIU dtoPatientIU) {

		

		if(userRepository.existsByTc(dtoPatientIU.getAuthRequestUser().getTc())) {
			throw new BaseException(new ErrorMessage(MessageType.TC_ALREADY_REGISTERED, null));
		}				
		
		User user = createPatientUser(dtoPatientIU.getAuthRequestUser());
		
		Patient patient = createPatient(dtoPatientIU);
		
		User savedUser = userRepository.save(user);
		
		patient.setUser(savedUser);
		
		patientRepository.save(patient);
		
		DtoPatient dtoPatient = new DtoPatient();
		DtoUser dtoUser = new DtoUser();
		
		BeanUtils.copyProperties(patient, dtoPatient);
		BeanUtils.copyProperties(patient.getUser(), dtoUser);
		
		dtoPatient.setUser(dtoUser);
		
		return dtoPatient;
	}

	private RefreshToken createRefreshToken(User user,boolean rememberMe) {
		RefreshToken refreshToken = new RefreshToken();
		
		if (rememberMe) {
	        refreshToken.setExpiredDate(LocalDateTime.now().plusDays(7));
	    } else {
	        refreshToken.setExpiredDate(LocalDateTime.now().plusHours(4));
	    }
		
		refreshToken.setRememberMe(rememberMe);
		refreshToken.setUser(user);
		refreshToken.setRefreshToken(UUID.randomUUID().toString());
		
		return refreshToken;
	}
	
	@Override
	public AuthResponse authenticate(AuthRequest request) {
		try {	
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getTc(),request.getPassword());
			authenticationProvider.authenticate(authenticationToken);
			
			Optional<User> optional = userRepository.findByTc(request.getTc());
			User user = optional.get();
			
			UserIds userIds = resolveUserIds(user);
			
			String accessToken = jwtService.generateToken(optional.get());
				
			RefreshToken refreshToken = createRefreshToken(optional.get(),request.isRememberMe());
			RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);	
						
			return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken(),user.getId(),userIds.getDoctorId(),userIds.getPatientId(),user.getRole());
			
		}catch (AuthenticationException ex) {
			throw new BaseException(new ErrorMessage(MessageType.USERNAME_OR_PASSWORD_INVALID, ex.getMessage()));
		}
		catch (Exception ex) {
			throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, ex.getMessage()));
		}
	}

	private boolean isValidRefreshToken(LocalDateTime expiredDate) {
		return LocalDateTime.now().isBefore(expiredDate);
	}
	
	private UserIds resolveUserIds(User user) {
		
		Long patientId = null;
	    Long doctorId = null;

	    if (user.getRole() == Role.PATIENT) {
	        Patient patient = patientRepository.findByUser(user)
	                .orElseThrow(() -> new BaseException(
	                        new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)
	                ));
	        patientId = patient.getId();
	    }

	    if (user.getRole() == Role.DOCTOR) {
	        Doctor doctor = doctorRepository.findByUser(user)
	                .orElseThrow(() -> new BaseException(
	                        new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)
	                ));
	        doctorId = doctor.getId();
	    }

	    return new UserIds(doctorId, patientId);
	}
	
	@Override
	public AuthResponse refreshToken(String refreshToken) {
		
		Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(refreshToken);
		if (optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, refreshToken));
		}
		
		if (!isValidRefreshToken(optional.get().getExpiredDate())) {
			throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_IS_EXPIRED, refreshToken));
		}
		
		User user = optional.get().getUser();
		UserIds userIds = resolveUserIds(user);
		String accessToken = jwtService.generateToken(user);
		
		RefreshToken newRefreshToken = createRefreshToken(user, Boolean.TRUE.equals(optional.get().getRememberMe()));
		newRefreshToken.setExpiredDate(optional.get().getExpiredDate());
		
		RefreshToken savedRefreshToken = refreshTokenRepository.save(newRefreshToken);

		
		Optional<Patient> patientOptional = patientRepository.findByUser(user);
		
		return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken(),user.getId(),userIds.getDoctorId(),userIds.getPatientId(),user.getRole());
	}


}
