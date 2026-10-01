package com.mustafaberk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.Department;
import java.util.List;


@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>{

	Optional<Department> findByName(String name);
	
	boolean existsByName(String name);
	
	List<Department> findByIsActive(Boolean isActive);
}
