package com.mustafaberk.dto;

import com.mustafaberk.enums.Role;
import com.mustafaberk.validation.ValidTcKimlikNo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

	@NotEmpty
	private String tc;
	
	@NotEmpty
	private String password;
	
	private boolean rememberMe;
	
}
