package com.mustafaberk.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DtoBase {

	private Long id;
	
	private LocalDateTime createTime;
	
}
