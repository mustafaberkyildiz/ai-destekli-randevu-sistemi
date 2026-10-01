package com.mustafaberk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUpdateEmail {

	@NotBlank(message = "Email Bos Olamaz")
	@Email(message = "Gecerli Bir Email Giriniz")
	private String inputEmail;
	
}
