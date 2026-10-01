package com.mustafaberk.controller.impl;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.mustafaberk.controller.IRestAuthenticationController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.AuthRequest;
import com.mustafaberk.dto.AuthResponse;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.dto.DtoDoctor;
import com.mustafaberk.dto.DtoDoctorIU;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoPatientIU;
import com.mustafaberk.dto.RefreshTokenRequest;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.RefreshToken;
import com.mustafaberk.repository.RefreshTokenRepository;
import com.mustafaberk.service.IAuthenticationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class RestAuthenticationControllerImpl extends RestBaseController implements IRestAuthenticationController{

	@Autowired
	private IAuthenticationService authenticationService;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register/doctor")
	@Override
	public RootEntity<DtoDoctor> registerDoctor(@Valid @RequestBody DtoDoctorIU dtoDoctorIU) {
		return ok(authenticationService.registerDoctor(dtoDoctorIU));
	}

	@PostMapping("/register/patient")
	@Override
	public RootEntity<DtoPatient> registerPatient(@Valid @RequestBody DtoPatientIU dtoPatientIU) {
		return ok(authenticationService.registerPatient(dtoPatientIU));
	}

	@PostMapping("/authenticate")
	@Override
	public RootEntity<AuthResponse> authentication(@Valid @RequestBody AuthRequest request,HttpServletResponse response) {
		
		AuthResponse authResponse = authenticationService.authenticate(request);
		
		long seconds;
		if (request.isRememberMe()) {
			seconds = 7 * 24 * 60 * 60;
		}
		else {
			seconds = 4 * 60 * 60;
		}
		
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
	            .httpOnly(true)
	            .secure(false)
	            .path("/")
	            .maxAge(seconds)
	            .sameSite("Lax")
	            .build();

		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		
		authResponse.setRefreshToken(null);
		
		return ok(authResponse);
	}

	@PostMapping("/refreshToken")
	@Override
	public RootEntity<AuthResponse> refreshToken(@CookieValue(name = "refreshToken",required = false) String refreshToken,
												 HttpServletResponse response) {
		
		if (refreshToken == null) {
			throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, null));
	    }
		
		AuthResponse authResponse = authenticationService.refreshToken(refreshToken);
		
		RefreshToken newRefreshToken = refreshTokenRepository.findByRefreshToken(authResponse.getRefreshToken()).orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, null)) );
		long seconds = Duration.between(LocalDateTime.now(),newRefreshToken.getExpiredDate()).getSeconds();
		
		if (seconds <= 0) {
		    throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_IS_EXPIRED, null));
		}
		
		ResponseCookie refreshCookie =
	            ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
	                    .httpOnly(true)
	                    .secure(false)
	                    .path("/")
	                    .maxAge(seconds)
	                    .sameSite("Lax")
	                    .build();
		
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		
		authResponse.setRefreshToken(null);// react tarafina gondermedik refreh tokeni HttpOnly Cookie'de tutucaz diye,cunku reactta kullanici erisebilir diye, HttpOnly Cookie'de daha guvenli tutuluyor
		
		return ok(authResponse);
	}


	@PostMapping("/logout")
	@Override
	public RootEntity<String> logout(HttpServletResponse response) {

		 ResponseCookie cookie =
		            ResponseCookie.from("refreshToken", "")
		                    .httpOnly(true)
		                    .secure(false)
		                    .path("/")
		                    .maxAge(0)
		                    .sameSite("Lax")
		                    .build();
		
		 response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		
		return ok("Çıkış başarılı.");
	}
	
}
