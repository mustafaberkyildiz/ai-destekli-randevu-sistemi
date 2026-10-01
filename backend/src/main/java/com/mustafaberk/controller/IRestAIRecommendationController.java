package com.mustafaberk.controller;

import com.mustafaberk.dto.DtoAIRecommendation;
import com.mustafaberk.dto.DtoAIRecommendationIU;
import com.mustafaberk.dto.DtoAIResponse;

public interface IRestAIRecommendationController {

	public RootEntity<DtoAIResponse> aiRecommendation(DtoAIRecommendationIU dto);
	
}
