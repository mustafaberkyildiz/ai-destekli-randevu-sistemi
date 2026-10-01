package com.mustafaberk.service;

import java.util.List;

import com.mustafaberk.dto.DtoMedicalRecords;
import com.mustafaberk.dto.DtoMedicalRecordsIU;

public interface IMedicalRecordsService {

	public DtoMedicalRecords saveMedicalRecord(DtoMedicalRecordsIU dto);
	
	public List<DtoMedicalRecords> getMedicalRecordsByPatient(Long patientId);
	
	public DtoMedicalRecords updateMedicalRecord(Long id, DtoMedicalRecordsIU dto);
}
