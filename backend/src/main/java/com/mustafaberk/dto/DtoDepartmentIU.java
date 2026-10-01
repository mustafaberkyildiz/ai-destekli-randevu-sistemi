package com.mustafaberk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoDepartmentIU {

	@NotNull
	private String name;
	
}
