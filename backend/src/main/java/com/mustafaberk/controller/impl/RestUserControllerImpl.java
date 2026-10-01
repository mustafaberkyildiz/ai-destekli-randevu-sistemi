package com.mustafaberk.controller.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestUserController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoResetCodeResponse;
import com.mustafaberk.dto.DtoUpdateEmail;
import com.mustafaberk.dto.DtoUpdatePassword;
import com.mustafaberk.dto.DtoUpdatePasswordLogin;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.dto.DtoVerifyResetCode;
import com.mustafaberk.service.IUserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/user")
@RestController
public class RestUserControllerImpl extends RestBaseController implements IRestUserController{

	@Autowired
	private IUserService userService;
	
	@PostMapping("/send-reset-code")
	@Override
	public RootEntity<DtoResetCodeResponse> sendResetCode(@Valid @RequestBody DtoUpdateEmail dto) {
		return ok(userService.sendResetCode(dto));
	}

	@PostMapping("/verify-reset-code/{userId}")
	@Override
	public RootEntity<String> verifyResetCode(@PathVariable Long userId,@Valid @RequestBody DtoVerifyResetCode dto) {
		return ok(userService.verifyResetCode(userId, dto));
	}

	@PutMapping("/reset-password/{userId}")
	@Override
	public RootEntity<String> resetPassword(@PathVariable Long userId,@Valid @RequestBody DtoUpdatePassword dto) {
		return ok(userService.resetPassword(userId,dto));
	}

	@PutMapping("/update-password/{userId}")
	@Override
	public RootEntity<String> updatePassword(@PathVariable Long userId,@Valid @RequestBody DtoUpdatePasswordLogin dto) {
		return ok(userService.updatePassword(userId, dto));
	}

	@PostMapping("/send-email-update-code/{userId}")
	@Override
	public RootEntity<String> sendMailToUpdateEmail(@PathVariable Long userId,@Valid @RequestBody DtoUpdateEmail dto) {
		return ok(userService.sendMailToUpdateEmail(userId, dto));
	}

	@PostMapping("/verify-email-code/{userId}")
	@Override
	public RootEntity<String> verifyEmailCode(@PathVariable Long userId,@Valid @RequestBody DtoVerifyResetCode dto) {
		return ok(userService.verifyEmailCode(userId, dto));
	}

	@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
	@GetMapping("/get/{userId}")
	@Override
	public RootEntity<DtoUser> getUserById(@PathVariable(name = "userId") Long id) {
		return ok(userService.getUserById(id));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAll")
	@Override
	public RootEntity<List<DtoUser>> getAllUser() {
		return ok(userService.getAllUser());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/lastLogin/{userId}")
	@Override
	public RootEntity<LocalDateTime> lastLoginDateTime(@PathVariable Long userId) {
		return ok(userService.lastLoginDateTime(userId));
	}
	
}
