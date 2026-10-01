package com.mustafaberk.service;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorReview;
import com.mustafaberk.dto.DtoDoctorReviewIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientUpdate;
import com.mustafaberk.dto.DtoShowDoctor;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;

public interface IDoctorService {

	public DtoDoctor getDoctorById(Long id);
	
	public List<DtoShowDoctor> getAllDoctor();
	
	public List<Doctor> getDoctorByDepartment(Department department);
	
	public Double getDoctorAverageScore(Long doctorId);
	
	public DtoDoctorReview saveDoctorReview(DtoDoctorReviewIU doctorReview);
	
	public DtoDoctorReview updateDoctorReview(DtoDoctorReviewIU doctorReview);
	
	public List<DtoDoctorReview> getAllDoctorReview(Long doctorId,Integer minScore,LocalDate startDate,LocalDate endDate);
	
	public List<DtoDoctorReview> getAllDoctorReviewForPatient(Long patientId,Integer minScore,LocalDate startDate,LocalDate endDate);
	
	public void disableDoctor(Long doctorId);
	
	public void enableDoctor(Long doctorId);
	
}
