package com.mustafaberk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.DoctorLeave;
import java.time.LocalDate;
import com.mustafaberk.enums.LeaveStatus;



@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long>{

	List<DoctorLeave> findByDoctor(Doctor doctor);
	
	boolean existsByStartDateAndDoctor(LocalDate startDate, Doctor doctor);
	
	List<DoctorLeave> findByDoctorId(Long doctorId);
	
	List<DoctorLeave> findByDoctor_IdAndStatus(Long doctor_Id, LeaveStatus status);
	
	List<DoctorLeave> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatus(LocalDate today1,LocalDate today2,LeaveStatus status);

		
	List<DoctorLeave> findByDoctorAndStatusAndEndDateGreaterThanEqual(Doctor doctor,LeaveStatus status,LocalDate date);
}
