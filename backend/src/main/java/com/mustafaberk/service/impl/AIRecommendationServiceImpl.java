package com.mustafaberk.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAIApiRecommendation;
import com.mustafaberk.dto.DtoAIRecommendation;
import com.mustafaberk.dto.DtoAIRecommendationIU;
import com.mustafaberk.dto.DtoAIResponse;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPythonRequest;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.AIRecommendation;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Patient;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.AIRecommendationRepository;
import com.mustafaberk.repository.DepartmentRepository;
import com.mustafaberk.repository.PatientRepository;
import com.mustafaberk.service.AiService;
import com.mustafaberk.service.IAIRecommendationService;

import io.netty.handler.codec.quic.QuicPathEvent.New;

@Service
public class AIRecommendationServiceImpl implements IAIRecommendationService{

	@Autowired
	private AiService aiService;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private AIRecommendationRepository aiRecommendationRepository;
	
	private DtoAIRecommendation generateDtoAIRecommendation(AIRecommendation aiRecommendation) {
		
		DtoAIRecommendation dtoAIRecommendation = new DtoAIRecommendation();
		DtoDepartment dtoDepartment = new DtoDepartment();
		DtoPatient dtoPatient = new DtoPatient();
		DtoUser dtoUser = new DtoUser();
		
		BeanUtils.copyProperties(aiRecommendation, dtoAIRecommendation);
		BeanUtils.copyProperties(aiRecommendation.getSuggested_department(), dtoDepartment);
		BeanUtils.copyProperties(aiRecommendation.getPatient(), dtoPatient);
		BeanUtils.copyProperties(aiRecommendation.getPatient().getUser(), dtoUser);
		
		dtoPatient.setUser(dtoUser);
		dtoAIRecommendation.setPatient(dtoPatient);
		dtoAIRecommendation.setSuggested_department(dtoDepartment);
		
		return dtoAIRecommendation;
	}
	
	@Override
	public DtoAIResponse aiRecommendation(DtoAIRecommendationIU dto) { 


		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getDetails();
		

		Patient loggedPatient = patientRepository.findByUser(user).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, null) ) ) ;
		

		if (!loggedPatient.getId().equals(dto.getPatientId())) {
			throw new BaseException(new ErrorMessage(MessageType.CANNOT_ACCESS_THIS_DATA, null));
		}
		
		DtoAIResponse aiResponse = aiService.checkIntention(dto.getSymptomText());
				
		if(!aiResponse.isSuccess()) {
			return aiResponse;
		}
		
		Optional<Department> optionalDepartment = departmentRepository.findByName(aiResponse.getMessage());
		if(optionalDepartment.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.DEPARTMENT_NOT_FOUND, aiResponse.getMessage()));
		}
		
		AIRecommendation aiRecommendation = new AIRecommendation();
		aiRecommendation.setSymptomText(dto.getSymptomText());
		aiRecommendation.setPatient(loggedPatient);
		aiRecommendation.setSuggested_department(optionalDepartment.get());
		
		AIRecommendation savedAiRecommendation = aiRecommendationRepository.save(aiRecommendation);
		
		DtoAIRecommendation dtoAIRecommendation = generateDtoAIRecommendation(savedAiRecommendation);
		
		DtoAIResponse dtoAIResponse = new DtoAIResponse();
		
		dtoAIResponse.setDtoAIRecommendation(dtoAIRecommendation);
		dtoAIResponse.setMessage(aiResponse.getMessage());
		dtoAIResponse.setSuccess(aiResponse.isSuccess());
		
		return dtoAIResponse;
	}
	
}
