package com.mustafaberk.dto;

import com.mustafaberk.model.Appointment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDoctorReviewIU {

	@NotNull(message = "Puan boş bırakılamaz")
    @Min(value = 1, message = "Puan en az 1 olabilir")
    @Max(value = 5, message = "Puan en fazla 5 olabilir")
	private Integer score;
	
	@Size(max = 500, message = "Yorum en fazla 500 karakter olabilir")
	private String comment;
	
	@NotNull(message = "Randevu ID boş bırakılamaz")
	private long appointmentId;
	
}
