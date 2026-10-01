package com.mustafaberk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.AIRecommendation;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Long>{

}
