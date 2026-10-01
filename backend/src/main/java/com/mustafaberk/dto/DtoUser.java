package com.mustafaberk.dto;

import com.mustafaberk.enums.Role;

import lombok.Data;

@Data
public class DtoUser extends DtoBase{

	private String tc;
	
	private String password;
	
	private String name;
	
	private String surname;
	
	private String email;
	
	private String phone;
	
	private Role role;
	
}
