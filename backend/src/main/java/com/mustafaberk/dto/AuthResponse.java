package com.mustafaberk.dto;

import com.mustafaberk.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

	private String accessToken;
	
	private String refreshToken;
	
	private Long userId;
	
	private Long doctorId;
	
	private Long patientId;
	
	private Role role;
}
