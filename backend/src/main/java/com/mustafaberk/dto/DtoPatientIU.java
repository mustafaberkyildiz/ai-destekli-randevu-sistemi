package com.mustafaberk.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mustafaberk.enums.BloodType;
import com.mustafaberk.enums.Gender;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DtoPatientIU {

	@Valid
	private AuthRequestUser authRequestUser;
	
	@NotNull
	@Positive
	private double height;
	
	@NotNull
	@Positive
	private double weight;
	
	@NotNull
	@Past
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;
	
	@NotNull
	private Gender gender;
	
	@NotNull
	private BloodType bloodType;
		
}
