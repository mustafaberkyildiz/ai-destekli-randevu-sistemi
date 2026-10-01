package com.mustafaberk.service;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAIRecommendation;
import com.mustafaberk.dto.DtoAIRecommendationIU;
import com.mustafaberk.dto.DtoAIResponse;

public interface IAIRecommendationService {

	public DtoAIResponse aiRecommendation(DtoAIRecommendationIU dto);
	
}
