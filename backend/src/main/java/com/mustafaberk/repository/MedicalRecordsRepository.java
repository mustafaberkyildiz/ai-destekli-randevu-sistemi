package com.mustafaberk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.MedicalRecords;
import com.mustafaberk.model.Patient;

@Repository
public interface MedicalRecordsRepository extends JpaRepository<MedicalRecords, Long>{
	
	public List<MedicalRecords> findByPatient(Patient patient);

}
