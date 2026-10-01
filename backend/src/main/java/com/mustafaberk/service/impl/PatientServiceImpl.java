package com.mustafaberk.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mustafaberk.config.AiConfig;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientIU;
import com.mustafaberk.dto.DtoPatientUpdate;
import com.mustafaberk.dto.DtoShowPatient;
import com.mustafaberk.dto.DtoSymptomRule;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Patient;
import com.mustafaberk.model.SymptomRule;
import com.mustafaberk.repository.PatientRepository;
import com.mustafaberk.repository.RefreshTokenRepository;
import com.mustafaberk.repository.SymptomRuleRepository;
import com.mustafaberk.repository.UserRepository;
import com.mustafaberk.service.IPatientService;
import com.mustafaberk.service.IUserService;

@Service
public class PatientServiceImpl implements IPatientService{

	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private SymptomRuleRepository symptomRuleRepository;

	@Override
	public DtoPatient getPatientById(Long id) {
		Patient patient = patientRepository.findById(id).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null)) );
		
		DtoPatient dtoPatient = new DtoPatient();
		DtoUser dtoUser = new DtoUser();
		
		BeanUtils.copyProperties(patient, dtoPatient);
		BeanUtils.copyProperties(patient.getUser(), dtoUser);
		dtoPatient.setUser(dtoUser);
		
		return dtoPatient;
	}

	@Override
	public List<DtoShowPatient> getAllPatient() {
		
		List<Patient> allPatientList = patientRepository.findAll();
		List<DtoShowPatient> dtoShowPatientList = new ArrayList<>();
		
		for (Patient patient : allPatientList) {
			DtoShowPatient dtoShowPatient = new DtoShowPatient();
			DtoPatient dtoPatient = new DtoPatient();
			DtoUser dtoUser = new DtoUser();
			
			BeanUtils.copyProperties(patient, dtoPatient);
			BeanUtils.copyProperties(patient.getUser(), dtoUser);
			
			dtoPatient.setUser(dtoUser);
			
			dtoShowPatient.setPatient(dtoPatient);
			LocalDateTime lastDateTime = refreshTokenRepository.findTopByUser_IdOrderByCreateTimeDesc(patient.getUser().getId())
					.map(x->x.getCreateTime()).orElse(null);//   .map(RefreshToken::getCreateTime) ile ayni
			dtoShowPatient.setLastLoginDateTime(lastDateTime);
			
			dtoShowPatientList.add(dtoShowPatient);
		}
		
		return dtoShowPatientList;
	}

	@Override
	public DtoPatient updatePatient(Long patientId, DtoPatientUpdate dto) {
		
		Optional<Patient> optional = patientRepository.findById(patientId);
	    if (optional.isEmpty()) {
	        throw new BaseException(
	                new ErrorMessage(MessageType.PATIENT_NOT_FOUND, patientId.toString())
	        );
	    }
		
		Patient patient = optional.get();
		
		if (dto.getHeight() <= 0.0) { 

			throw new BaseException(new ErrorMessage(MessageType.HEIGHT_NOT_VALID, null));
		}
		if (dto.getWeight() <= 0.0) { 

			throw new BaseException(new ErrorMessage(MessageType.WEIGHT_NOT_VALID, null));
		}
		
		patient.setHeight(dto.getHeight());
		patient.setWeight(dto.getWeight());
		

		Patient updatedPatient = patientRepository.save(patient);
		
		DtoPatient dtoPatient = new DtoPatient();
		BeanUtils.copyProperties(updatedPatient, dtoPatient);
		DtoUser dtoUser = new DtoUser();
		BeanUtils.copyProperties(updatedPatient.getUser(), dtoUser);
		dtoPatient.setUser(dtoUser);
		
		return dtoPatient;
	}

	private DtoSymptomRule generateDtoSymptomRule(SymptomRule symptomRule) {
		
		DtoSymptomRule dtoSymptomRule = new DtoSymptomRule();
		BeanUtils.copyProperties(symptomRule, dtoSymptomRule);
		
		return dtoSymptomRule;
	}
	
	@Override
	public List<DtoSymptomRule> getRulesByDepartment(Long departmentId) {
		
		List<SymptomRule> symptomRuleList = symptomRuleRepository.findRulesByDepartmentId(departmentId);
		
		return symptomRuleList.stream()
				.map(this::generateDtoSymptomRule)
				.toList();
	}
	
}
