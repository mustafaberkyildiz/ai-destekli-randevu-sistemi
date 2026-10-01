package com.mustafaberk.dto;

import java.time.LocalDate;

import com.mustafaberk.enums.BloodType;
import com.mustafaberk.enums.Gender;

import lombok.Data;

@Data
public class DtoPatient extends DtoBase{

	private DtoUser user;
	
	private double height;
	
	private double weight;
	
	private LocalDate birthDate;
	
	private Gender gender;
	
	private BloodType bloodType;
	
}
