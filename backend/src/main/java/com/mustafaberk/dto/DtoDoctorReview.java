package com.mustafaberk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDoctorReview {

	private Integer score;
	
	private String comment;
	
	private DtoAppointment dtoAppointment;
	
}
