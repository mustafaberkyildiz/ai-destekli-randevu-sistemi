package com.mustafaberk.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUpdatePassword {
	
	@NotBlank
	@Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
	@Pattern(
		    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
		    message = "Şifre en az bir rakam, bir küçük ve bir büyük harf içermelidir"
	)
	private String inputPassword;

	@NotBlank
	@Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
	@Pattern(
		    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
		    message = "Şifre en az bir rakam, bir küçük ve bir büyük harf içermelidir"
	)
	private String inputPasswordRepeat;
	 
	@AssertTrue(message = "Sifreler Eslesmiyor")
	public boolean isPasswordMatching() {
		if (inputPassword == null || inputPasswordRepeat == null) {
		       return true;
		   }
		return inputPassword.equals(inputPasswordRepeat);
	}
}
