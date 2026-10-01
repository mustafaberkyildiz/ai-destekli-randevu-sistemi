package com.mustafaberk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.ResetCode;

import java.util.List;
import java.util.Optional;
import com.mustafaberk.enums.VertificationType;


@Repository
public interface PasswordResetCodeRepository extends JpaRepository<ResetCode, Long>{

	Optional<ResetCode> findByUser_IdAndCodeAndUsedFalse(Long userId, String code);
	
	Optional<ResetCode> findByUser_IdAndCodeAndUsedFalseAndType(Long userId, String code, VertificationType type);
	
	List<ResetCode> findByUser_IdAndUsedFalseAndType(Long userId, VertificationType type);// girilen userid nin tum kullanilmayan yani used false olan satirlarini getirir
	
}
