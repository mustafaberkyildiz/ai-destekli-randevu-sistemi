package com.mustafaberk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.AppointmentSlot;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import com.mustafaberk.model.User;



@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{

	public Optional<Appointment> findByAppointmentSlot(AppointmentSlot appointmentSlot);
	
	public boolean existsByAppointmentSlotAndStatus(AppointmentSlot slot , AppointmentStatus status );
	
	public List<Appointment> findByAppointmentSlot_Doctor_IdAndAppointmentSlot_DateBetweenAndStatus(
	        Long doctorId,
	        LocalDate startDate,
	        LocalDate endDate,
	        AppointmentStatus status
	);
	
	public List<Appointment> findByAppointmentSlot_Doctor_Id(Long id);
	
	public List<Appointment> findByPatient_Id(Long id);
	
	public Optional<Appointment> findByAppointmentSlot_Doctor_User(User appointmentSlot_Doctor_User);
	
	public List<Appointment> findByAppointmentSlot_DateBetween(LocalDate startDate,LocalDate endDate);
	
	public List<Appointment> findTop5ByAppointmentSlot_DateGreaterThanEqualAndStatusOrderByAppointmentSlot_DateAscAppointmentSlot_StartTimeAsc
							 (LocalDate date,AppointmentStatus status);
	
	public long countByAppointmentSlot_Date(LocalDate date);
	
	public long countByStatusAndAppointmentSlot_Date(AppointmentStatus status, LocalDate date);
	
	@Query("""
			SELECT a FROM Appointment a
			WHERE a.appointmentSlot.doctor.department = :department
			AND (
			    a.appointmentSlot.date > :date
			    OR (
			        a.appointmentSlot.date = :date
			        AND a.appointmentSlot.startTime >= :time
			    )
			)
			""")
			List<Appointment> findFutureAppointmentsFromDateTime(
			        @Param("department") Department department,
			        @Param("date") LocalDate date,
			        @Param("time") LocalTime time
			);

	@Query("""
			SELECT a FROM Appointment a
			WHERE a.appointmentSlot.doctor = :doctor
			AND (
			    a.appointmentSlot.date > :date
			    OR (
			        a.appointmentSlot.date = :date
			        AND a.appointmentSlot.startTime >= :time
			    )
			)
			""")
			List<Appointment> findFutureAppointmentsFromDateTimeForDoctor(
			        @Param("doctor") Doctor doctor,
			        @Param("date") LocalDate date,
			        @Param("time") LocalTime time
			);
	
}
