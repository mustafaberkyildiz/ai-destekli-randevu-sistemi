package com.mustafaberk.dto;

import java.time.LocalDate;

import com.mustafaberk.enums.LeaveType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDoctorLeaveIU {

	@NotNull
	@FutureOrPresent
	private LocalDate startDate;
	
	@NotNull
	@FutureOrPresent
	private LocalDate endDate;
	
	@NotBlank
	private String reason;
	
	@NotNull
	private LeaveType leaveType;
	
	@NotNull
	private Long doctorId;
	
	@AssertTrue(message = "Başlangıç tarihi bitiş tarihinden sonra olamaz")
    public boolean isDateValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !startDate.isAfter(endDate);
    }
	
}
