package com.mustafaberk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.User;
import java.util.List;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByTc(String tc);
	
	
	boolean existsByTc(String tc);

	
	boolean existsByEmail(String email);
	
	
	Optional<User> findByEmail(String email);
	
	boolean existsByPhone(String phone);
}
