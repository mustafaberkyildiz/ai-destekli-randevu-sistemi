package com.mustafaberk.controller;

import com.mustafaberk.dto.AuthRequest;
import com.mustafaberk.dto.AuthResponse;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientIU;
import com.mustafaberk.dto.RefreshTokenRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface IRestAuthenticationController {

	public RootEntity<DtoDoctor> registerDoctor(DtoDoctorIU dtoDoctorIU);
	
	public RootEntity<DtoPatient> registerPatient(DtoPatientIU dtoPatientIU);
	
	public RootEntity<AuthResponse> authentication(AuthRequest request,HttpServletResponse response);
	
	public RootEntity<AuthResponse> refreshToken(String refreshToken,HttpServletResponse response);
	
	public RootEntity<String> logout(HttpServletResponse response);
		
}
