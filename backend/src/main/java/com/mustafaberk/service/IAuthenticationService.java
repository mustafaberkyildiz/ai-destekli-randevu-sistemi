package com.mustafaberk.service;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.AuthRequest;
import com.mustafaberk.dto.AuthRequestUser;
import com.mustafaberk.dto.AuthResponse;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientIU;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.dto.RefreshTokenRequest;

public interface IAuthenticationService {

	public DtoDoctor registerDoctor(DtoDoctorIU dtoDoctorIU);
	
	public DtoPatient registerPatient(DtoPatientIU dtoPatientIU);
	
	public AuthResponse authenticate(AuthRequest request);
	
	public AuthResponse refreshToken(String refreshToken);
		
}
