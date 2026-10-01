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
public class DtoUpdatePasswordLogin {
	
	 @NotBlank
	 private String lastPassword;

	 @NotBlank
	 @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
		@Pattern(
			    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
			    message = "Şifre en az bir rakam, bir küçük ve bir büyük harf içermelidir."
		)
	 private String inputPassword;

	 @NotBlank
	 private String inputPasswordRepeat;
	 
	 @AssertTrue(message = "Yeni Sifreler Eslesmiyor.")
	 public boolean isPasswordMatching() {
		 if (inputPassword == null || inputPasswordRepeat == null) {
		        return true;
		    }
		 return inputPassword.equals(inputPasswordRepeat);
	 }
}
