package com.mustafaberk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.User;

import java.util.List;
import java.util.Optional;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{

	List<Doctor> findByDepartment(Department department);

	Optional<Doctor> findByUser(User user);
	
	List<Doctor> findByDepartmentAndIsActive(Department department,Boolean isActive);
}
