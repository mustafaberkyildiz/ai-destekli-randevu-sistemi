package com.mustafaberk.dto;

import java.time.LocalDate;

import com.mustafaberk.enums.LeaveType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoDoctorLeaveUpdate {

	@NotBlank(message = "Reason boş olamaz")
    private String reason;
       
}
