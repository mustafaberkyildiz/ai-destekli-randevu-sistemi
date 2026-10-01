package com.mustafaberk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAIResponse {
	
	private String message;
	
    private boolean success;
    
    private DtoAIRecommendation dtoAIRecommendation;

}
