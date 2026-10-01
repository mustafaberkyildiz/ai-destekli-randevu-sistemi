package com.mustafaberk.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.mustafaberk.dto.DtoResetCodeResponse;
import com.mustafaberk.dto.DtoUpdateEmail;
import com.mustafaberk.dto.DtoUpdatePassword;
import com.mustafaberk.dto.DtoUpdatePasswordLogin;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.dto.DtoVerifyResetCode;

public interface IRestUserController {
	
	public RootEntity<DtoUser> getUserById(Long id);

	public RootEntity<DtoResetCodeResponse> sendResetCode(DtoUpdateEmail dto);
	
	public RootEntity<String> verifyResetCode(Long userId, DtoVerifyResetCode dto);
	
	public RootEntity<String> resetPassword(Long userId,DtoUpdatePassword dto);
	
	public RootEntity<String> updatePassword(Long userId,DtoUpdatePasswordLogin dto);
	
	public RootEntity<String> sendMailToUpdateEmail(Long userId,DtoUpdateEmail dto);
	
	public RootEntity<String> verifyEmailCode(Long userId,DtoVerifyResetCode dto);
	
	public RootEntity<List<DtoUser>> getAllUser();
	
	public RootEntity<LocalDateTime> lastLoginDateTime(Long userId);
}
