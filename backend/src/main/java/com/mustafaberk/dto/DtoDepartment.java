package com.mustafaberk.dto;

import com.mustafaberk.model.BaseEntity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoDepartment extends DtoBase{

	private String name;
	
	private Boolean active;
	
}
