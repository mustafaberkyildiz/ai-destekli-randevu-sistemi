package com.mustafaberk.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestAIRecommendationController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAIRecommendationIU;
import com.mustafaberk.dto.DtoAIResponse;
import com.mustafaberk.service.IAIRecommendationService;

import jakarta.validation.Valid;


@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/ai")
@RestController
public class RestAIRecommendationControllerImpl extends RestBaseController implements IRestAIRecommendationController{

	@Autowired
	private IAIRecommendationService aiRecommendationService;
	
	@PreAuthorize("hasRole('PATIENT')")
	@PostMapping("/recommend")
	@Override
	public RootEntity<DtoAIResponse> aiRecommendation(@Valid @RequestBody DtoAIRecommendationIU dto) {
		return ok(aiRecommendationService.aiRecommendation(dto));
	}

}
