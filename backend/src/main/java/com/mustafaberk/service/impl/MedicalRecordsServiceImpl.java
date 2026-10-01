package com.mustafaberk.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoMedicalRecords;
import com.mustafaberk.dto.DtoMedicalRecordsIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.MedicalRecords;
import com.mustafaberk.model.Patient;
import com.mustafaberk.repository.DoctorRepository;
import com.mustafaberk.repository.MedicalRecordsRepository;
import com.mustafaberk.repository.PatientRepository;
import com.mustafaberk.service.IMedicalRecordsService;

@Service
public class MedicalRecordsServiceImpl implements IMedicalRecordsService {

    @Autowired
    private MedicalRecordsRepository medicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private MedicalRecords createMedicalRecord(DtoMedicalRecordsIU dto) {

        Optional<Patient> optionalPatient = patientRepository.findById(dto.getPatientId());
        if (optionalPatient.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, dto.getPatientId().toString())
            );
        }

        Optional<Doctor> optionalDoctor = doctorRepository.findById(dto.getDoctorId());
        if (optionalDoctor.isEmpty()) {
            throw new BaseException(
                    new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, dto.getDoctorId().toString())
            );
        }

        MedicalRecords record = new MedicalRecords();

        record.setDiagnosis(dto.getDiagnosis());
        record.setPrescription(dto.getPrescription());
        record.setDoctor_note(dto.getDoctor_note());

        record.setPatient(optionalPatient.get());
        record.setDoctor(optionalDoctor.get());

        return record;
    }

    private DtoMedicalRecords changeType(MedicalRecords savedMedicalRecords) {
    	 DtoMedicalRecords dtoMedicalRecords = new DtoMedicalRecords();
         DtoDoctor dtoDoctor = new DtoDoctor();
         DtoPatient dtoPatient = new DtoPatient();
         DtoUser dtoUserDoctor = new DtoUser();
         DtoUser dtoUserPatient = new DtoUser();
         
         BeanUtils.copyProperties(savedMedicalRecords.getDoctor().getUser(), dtoUserDoctor);
         BeanUtils.copyProperties(savedMedicalRecords.getPatient().getUser(), dtoUserPatient);
 		
         BeanUtils.copyProperties(savedMedicalRecords.getDoctor() , dtoDoctor);
         dtoDoctor.setUser(dtoUserDoctor);
         
         BeanUtils.copyProperties(savedMedicalRecords.getPatient() , dtoPatient);
         dtoPatient.setUser(dtoUserPatient);
         
         BeanUtils.copyProperties(savedMedicalRecords, dtoMedicalRecords);
         dtoMedicalRecords.setPatient(dtoPatient);
    	
    	return dtoMedicalRecords;
    }

    @Override
    public DtoMedicalRecords saveMedicalRecord(DtoMedicalRecordsIU dto) {

        MedicalRecords record = createMedicalRecord(dto);

        MedicalRecords savedMedicalRecords = medicalRecordRepository.save(record);

        DtoMedicalRecords dtoMedicalRecords = changeType(savedMedicalRecords);

        return dtoMedicalRecords;
    }

    @Override
    public List<DtoMedicalRecords> getMedicalRecordsByPatient(Long patientId) {

        Optional<Patient> optional = patientRepository.findById(patientId);
        if (optional.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PATIENT_NOT_FOUND, patientId.toString()));
        }

        List<MedicalRecords> records = medicalRecordRepository.findByPatient(optional.get());

        if (records.isEmpty()) {
            throw new BaseException(
                    new ErrorMessage(MessageType.MEDICAL_RECORD_NOT_FOUND, patientId.toString())
            );
        }

        List<DtoMedicalRecords> responseList = new ArrayList<>();

        for (MedicalRecords record : records) {
            DtoMedicalRecords dto = changeType(record);
            responseList.add(dto);
        }

        return responseList;
    }


    @Override
    public DtoMedicalRecords updateMedicalRecord(Long id, DtoMedicalRecordsIU dto) {

        Optional<MedicalRecords> optional = medicalRecordRepository.findById(id);

        if (optional.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.MEDICAL_RECORD_NOT_FOUND, id.toString()));
        }

        MedicalRecords record = optional.get();


        record.setDiagnosis(dto.getDiagnosis());
        record.setPrescription(dto.getPrescription());
        record.setDoctor_note(dto.getDoctor_note());

        MedicalRecords updated = medicalRecordRepository.save(record);

        DtoMedicalRecords response = changeType(record);
        
        return response;
    }
}