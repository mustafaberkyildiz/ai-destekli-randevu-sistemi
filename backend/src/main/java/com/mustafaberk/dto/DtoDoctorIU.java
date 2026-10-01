package com.mustafaberk.dto;

import com.mustafaberk.enums.Title;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoDoctorIU {

	@Valid
	@NotNull
	private AuthRequestUser authRequestUser;
	
	@NotNull
	private Title title;
	
	@NotNull
	private Long departmentId;
	
}
