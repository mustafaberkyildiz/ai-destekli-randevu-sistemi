package com.mustafaberk.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mustafaberk.dto.AuthRequestUser;
import com.mustafaberk.dto.DtoPatient;
import com.mustafaberk.dto.DtoResetCodeResponse;
import com.mustafaberk.dto.DtoUpdateEmail;
import com.mustafaberk.dto.DtoUpdatePassword;
import com.mustafaberk.dto.DtoUpdatePasswordLogin;
import com.mustafaberk.dto.DtoUser;
import com.mustafaberk.dto.DtoUserUpdate;
import com.mustafaberk.dto.DtoVerifyResetCode;
import com.mustafaberk.enums.VertificationType;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.ResetCode;
import com.mustafaberk.model.Patient;
import com.mustafaberk.model.RefreshToken;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.PasswordResetCodeRepository;
import com.mustafaberk.repository.RefreshTokenRepository;
import com.mustafaberk.repository.UserRepository;
import com.mustafaberk.service.IUserService;
import com.mustafaberk.service.MailService;

import io.netty.handler.codec.quic.QuicPathEvent.New;

@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private PasswordResetCodeRepository passwordResetCodeRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Override
	public DtoUser getUserById(Long id) {
		
		Optional<User> optional = userRepository.findById(id);
		if (optional.isEmpty()) {
			throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null));
		}
		
		DtoUser dtoUser = new DtoUser();
		BeanUtils.copyProperties(optional.get(), dtoUser);
		
		return dtoUser;
	}

	@Override
	public List<DtoUser> getAllUser() {
		
		List<User> allUserList = userRepository.findAll();
		List<DtoUser> dtoUserList = new ArrayList<>();
		
		for (User user : allUserList) {
			DtoUser dtoUser = new DtoUser();
			BeanUtils.copyProperties(user, dtoUser);
			dtoUserList.add(dtoUser);
		}
		
		return dtoUserList;
	}
	
	@Transactional
	@Override
	public DtoResetCodeResponse sendResetCode(DtoUpdateEmail dto) {
		
		User user = userRepository.findByEmail(dto.getInputEmail())
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.EMAIL_NOT_FOUND, null)) ) ;
		


	    List<ResetCode> activeCodes = passwordResetCodeRepository.findByUser_IdAndUsedFalseAndType(user.getId(),VertificationType.PASSWORD_RESET);

	    for (ResetCode item : activeCodes) {
	        item.setUsed(true);
	    }
		passwordResetCodeRepository.saveAll(activeCodes);
	    
		String code = generateCode();
		
		ResetCode passwordResetCode = new ResetCode();
	    passwordResetCode.setUser(user);
	    passwordResetCode.setCode(code);
	    passwordResetCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
	    passwordResetCode.setUsed(false);
	    passwordResetCode.setType(VertificationType.PASSWORD_RESET);
	    passwordResetCode.setNewEmail(null);
	    
	    passwordResetCodeRepository.save(passwordResetCode);
		mailService.sendMailtoCode(user.getEmail(),code);
	    
		DtoResetCodeResponse response = new DtoResetCodeResponse();
		response.setMessage("MAIL GONDERILDI");
		response.setUserId(user.getId());
		
	    return response;
	}

	private String generateCode() {

		String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));

		return code;
	}
	
	@Override
	public String verifyResetCode(Long userId,DtoVerifyResetCode dto) {
		
		ResetCode passwordResetCode = passwordResetCodeRepository.
				findByUser_IdAndCodeAndUsedFalseAndType(userId, dto.getInputCode(),VertificationType.PASSWORD_RESET)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.CODE_NOT_MATCH, null)) ) ;
		
		if (passwordResetCode.getExpireTime().isBefore(LocalDateTime.now())) {
			throw new BaseException(new ErrorMessage(MessageType.CODE_EXPIRED, null));
		}
		
		passwordResetCode.setUsed(true);// KOD KULLANILDI GIBISINDEN
		passwordResetCodeRepository.save(passwordResetCode);
		
		return "KOD DOGRULANDI";
	}

	@Override
	public String resetPassword(Long userId,DtoUpdatePassword dto) {
		
		User user = userRepository.findById(userId)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)) );
		
		String updatedPassword = isValidPassword(user, dto.getInputPassword(),dto.getInputPasswordRepeat());
		
		user.setPassword(updatedPassword);
		userRepository.save(user);
		
		return "SIFRENIZ GUNCELLENDI";
	}

	private String isValidPassword(User user,String inputPassword,String inputPasswordRepeat) {
		
		if (!inputPassword.equals(inputPasswordRepeat)) {
			throw new BaseException(new ErrorMessage(MessageType.PASSWORD_NOT_MATCH,null));
		}
		

		if (passwordEncoder.matches(inputPassword, user.getPassword())) {
			throw new BaseException(new ErrorMessage(MessageType.PASSWORD_NOT_VALID, null));
		}
		
		return passwordEncoder.encode(inputPassword);
	}
	
	@Override
	public String updatePassword(Long userId, DtoUpdatePasswordLogin dto) {

		User user = userRepository.findById(userId)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null))  ) ;
		
		if(!passwordEncoder.matches(dto.getLastPassword(),user.getPassword())) {
			throw new BaseException(new ErrorMessage(MessageType.PASSWORD_NOT_MATCH_BEFORE_PASSWORD, null));
		}
		
		String updatedPassword = isValidPassword(user, dto.getInputPassword(),dto.getInputPasswordRepeat());
		
		user.setPassword(updatedPassword);
		userRepository.save(user);
		
		return "SIFRENIZ GUNCELLENDI";
	}

	@Transactional
	@Override
	public String sendMailToUpdateEmail(Long userId, DtoUpdateEmail dto) {
		
		User user = userRepository.findById(userId)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, null)) ); 
		
		if (user.getEmail().equals(dto.getInputEmail())) {
			throw new BaseException(new ErrorMessage(MessageType.EMAIL_NOT_VALID, null));
		}
		
		if(userRepository.existsByEmail(dto.getInputEmail())) {
			throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_REGISTERED, null));
		}
		
		List<ResetCode> activeCodes = passwordResetCodeRepository.findByUser_IdAndUsedFalseAndType(userId,VertificationType.EMAIL_VERIFICATION);

	    for (ResetCode item : activeCodes) {
	        item.setUsed(true);
	    }
		passwordResetCodeRepository.saveAll(activeCodes);
		
		String code = generateCode();
		
		ResetCode resetCode = new ResetCode();
		resetCode.setUser(user);
		resetCode.setCode(code);
		resetCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
		resetCode.setUsed(false);
		resetCode.setType(VertificationType.EMAIL_VERIFICATION);
		resetCode.setNewEmail(dto.getInputEmail());
	    
	    passwordResetCodeRepository.save(resetCode);
		mailService.sendMailtoCode(dto.getInputEmail(), code);
		
		return "KOD GONDERILDI";
	}

	@Transactional
	@Override
	public String verifyEmailCode(Long userId, DtoVerifyResetCode dto) {

		ResetCode resetCode = passwordResetCodeRepository.
				findByUser_IdAndCodeAndUsedFalseAndType(userId, dto.getInputCode(),VertificationType.EMAIL_VERIFICATION)
				.orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.CODE_NOT_MATCH, null)) ) ;
		
		if (userRepository.existsByEmail(resetCode.getNewEmail())) {
		    throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_REGISTERED, null));
		}
		
		if (resetCode.getExpireTime().isBefore(LocalDateTime.now())) {
			throw new BaseException(new ErrorMessage(MessageType.CODE_EXPIRED, null));
		}
		
		resetCode.setUsed(true);// KOD KULLANILDI GIBISINDEN
		passwordResetCodeRepository.save(resetCode);
		
		User user = resetCode.getUser();
		user.setEmail(resetCode.getNewEmail());
		userRepository.save(user);
		
		return "EMAIL GUNCELLENDI";
	}


	@Override
	public LocalDateTime lastLoginDateTime(Long userId) {
		
		RefreshToken refreshToken = refreshTokenRepository.findTopByUser_IdOrderByCreateTimeDesc(userId)
											.orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, null)) );
		
		return refreshToken.getCreateTime();
	}
	
}
