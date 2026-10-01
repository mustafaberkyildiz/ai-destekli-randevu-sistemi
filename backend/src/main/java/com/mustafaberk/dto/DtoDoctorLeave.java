package com.mustafaberk.dto;

import java.time.LocalDate;

import com.mustafaberk.enums.LeaveStatus;
import com.mustafaberk.enums.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDoctorLeave extends DtoBase{

	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private String reason;
	
	private LeaveType leaveType;
	
	private LeaveStatus status;
	
	private DtoDoctor doctor;
	
}
