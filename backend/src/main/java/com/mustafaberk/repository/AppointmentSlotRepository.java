package com.mustafaberk.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.enums.SlotStatus;


@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long>{
	
	List<AppointmentSlot> findByDoctorId(Long doctorId);
	
	List<AppointmentSlot> findByDateGreaterThanEqual(LocalDate date);

	
	List<AppointmentSlot> findByDoctorIdAndDateGreaterThanEqual(Long doctorId,LocalDate date);
	
	List<AppointmentSlot> findByDoctorIdAndDateBetween(Long doctorId,LocalDate startDate,LocalDate endDate);
	
	List<AppointmentSlot> findByDoctor_IdAndDateAndStatus(Long doctor_Id, LocalDate date, SlotStatus status);
	



	List<AppointmentSlot> findByDoctor_Department_IdAndStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
	    Long departmentId,
	    SlotStatus status,
	    LocalDate date
	);
	
	@Query("""
			SELECT a FROM AppointmentSlot a
			WHERE a.doctor = :doctor
			AND (
			    a.date > :date
			    OR (
			        a.date = :date
			        AND a.startTime >= :time
			    )
			)
			""")
			List<AppointmentSlot> findFutureAppointmentSlotsFromDateTimeForDoctor(
			        @Param("doctor") Doctor doctor,
			        @Param("date") LocalDate date,
			        @Param("time") LocalTime time
			);
	
}
