package com.mustafaberk.dto;

import com.mustafaberk.enums.Title;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class DtoDoctor extends DtoBase{

	private DtoUser user;
	
	private Title title;
	
	private Boolean isActive;
	
	private Integer annualLeaveLimit;

	private Integer remainingAnnualLeave;
	
	private DtoDepartment department;
	
}
