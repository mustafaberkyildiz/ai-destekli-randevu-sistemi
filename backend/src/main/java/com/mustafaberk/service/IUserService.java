package com.mustafaberk.service;

import java.time.LocalDateTime;
import java.util.List;

import com.mustafaberk.dto.AuthRequestUser;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoResetCodeResponse;
import com.mustafaberk.dto.DtoUpdateEmail;
import com.mustafaberk.dto.DtoUpdatePassword;
import com.mustafaberk.dto.DtoUpdatePasswordLogin;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.dto.DtoUserUpdate;
import com.mustafaberk.dto.DtoVerifyResetCode;

public interface IUserService {

	public DtoUser getUserById(Long id);
	
	public List<DtoUser> getAllUser();
		
	public DtoResetCodeResponse sendResetCode(DtoUpdateEmail dto);
	
	public String verifyResetCode(Long userId,DtoVerifyResetCode dto);// maile gonderilen 6 haneli dogrulama kodu 
	
	public String resetPassword(Long userId,DtoUpdatePassword dto);
	
	public String updatePassword(Long userId,DtoUpdatePasswordLogin dto);
	
	public String sendMailToUpdateEmail(Long userId,DtoUpdateEmail dto);
	
	public String verifyEmailCode(Long userId,DtoVerifyResetCode dto);
	
	public LocalDateTime lastLoginDateTime(Long userId);

}
