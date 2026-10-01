package com.mustafaberk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.DoctorReview;
import java.util.List;


@Repository
public interface DoctorReviewRepository extends JpaRepository<DoctorReview, Long>{


	List<DoctorReview> findByAppointment_AppointmentSlot_Doctor_Id(Long doctorId);
	


    @Query("SELECT AVG(r.score) FROM DoctorReview r WHERE r.appointment.appointmentSlot.doctor.id = :doctorId")
    Double getAverageScoreByDoctorId(@Param("doctorId") Long doctorId);


		
    DoctorReview findByAppointment(Appointment appointment);
    
    boolean existsByAppointment(Appointment appointment);

}
