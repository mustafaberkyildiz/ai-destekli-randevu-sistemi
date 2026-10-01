package com.mustafaberk.controller;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorReview;
import com.mustafaberk.dto.DtoDoctorReviewIU;
import com.mustafaberk.dto.DtoShowDoctor;

public interface IRestDoctorController {

	public RootEntity<DtoDoctor> getDoctorById(Long id);
	
	public RootEntity<List<DtoShowDoctor>> getAllDoctor();
	
	public RootEntity<Double> getDoctorAverageScore(Long doctorId);
	
	public RootEntity<DtoDoctorReview> saveDoctorReview(DtoDoctorReviewIU doctorReview);
	
	public RootEntity<DtoDoctorReview> updateDoctorReview(DtoDoctorReviewIU doctorReview);
	
	public RootEntity<List<DtoDoctorReview>> getAllDoctorReview(Long doctorId,Integer minScore,LocalDate startDate,LocalDate endDate);
	
	public RootEntity<List<DtoDoctorReview>> getAllDoctorReviewForPatient(Long patientId,Integer minScore,LocalDate startDate,LocalDate endDate);
	
	public RootEntity<Void> disableDoctor(Long doctorId);
	
	public RootEntity<Void> enableDoctor(Long doctorId);
}
