package com.mustafaberk.dto;

import com.mustafaberk.enums.Role;
import com.mustafaberk.validation.ValidTcKimlikNo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestUser {
	
	@Size(min = 11,max = 11,message = "TC KIMLIK NUMARASI 11 HANELI OLMALI")
	@NotEmpty

	private String tc;
	
	@NotEmpty
	@Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
	@Pattern(
		    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
		    message = "Şifre en az bir rakam, bir küçük ve bir büyük harf içermelidir"
	)
	private String password;
	
	@NotEmpty
	@Size(max = 50)
	private String name;
	
	@NotEmpty
	@Size(max = 50)
	private String surname;
	
	@NotBlank(message = "Email Bos Olamaz")
	@Email(message = "Gecerli Bir Email Giriniz")
	private String email;
	

	@NotBlank(message = "Telefon boş olamaz")
	@Pattern(regexp = "^(05)[0-9]{9}$", message = "Geçerli bir telefon numarası giriniz (05XXXXXXXXX)")
	private String phone;
	
	@NotNull
	private Role role;

	
}
