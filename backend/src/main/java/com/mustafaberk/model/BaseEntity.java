package com.mustafaberk.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	

	/*
	Evet, artık setCreateTime() yazmana gerek yok.
	Hibernate / JPA bunu otomatik oluşturur.
	
	Güvenli: updatable = false ile birleştiğinde, bu alanın yanlışlıkla güncellenmesini (update edilmesini) engeller; kayıt zamanı "mühürlenmiş" olur.
	*/
	
	@CreationTimestamp
	@Column(name = "create_time" , updatable = false)
	private LocalDateTime createTime;
	
}
