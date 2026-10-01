package com.mustafaberk.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoShowPatient {

	DtoPatient patient;
	
	LocalDateTime lastLoginDateTime;
}
