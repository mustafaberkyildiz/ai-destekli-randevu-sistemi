package com.mustafaberk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseEntity{

	@Column(name = "expired_date")
	private LocalDateTime expiredDate;
	
	@Column(name = "refresh_token")
	private String refreshToken;
	
	@Column(name = "remember_me")
	private Boolean rememberMe;
	
	@ManyToOne
	private User user;
	
}
