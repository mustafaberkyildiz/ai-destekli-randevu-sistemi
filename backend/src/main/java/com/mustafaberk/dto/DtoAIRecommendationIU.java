package com.mustafaberk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAIRecommendationIU {
	
	@NotBlank(message = "Semptom Aciklamasi Bos Birakilamaz !")//NotBlank:hem null kontrolü yapar hem de metnin uzunluğunun (boşluklar kırpıldıktan sonra) 0'dan büyük olduğunu doğrular.
	private String symptomText;
	
	@NotNull(message = "Hasta ID'si boş olamaz.")
	private Long patientId;

}
