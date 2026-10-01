package com.mustafaberk.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoShowDoctor {

	DtoDoctor doctor;
	
	LocalDateTime lastLoginDateTime;
	
}
