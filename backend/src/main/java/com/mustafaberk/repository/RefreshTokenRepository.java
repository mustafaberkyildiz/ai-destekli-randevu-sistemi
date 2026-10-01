package com.mustafaberk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	
	Optional<RefreshToken> findTopByUser_IdOrderByCreateTimeDesc(Long userId);


	
}
