package com.mustafaberk.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(value = Include.NON_NULL)
public class RootEntity<T> {

	private Integer status;
	
	private T payload;
	
	private String errorMessage;
	
	
	public static <T> RootEntity<T> ok(T payload){
		 return RootEntity.<T>builder()
		            .status(200)
		            .errorMessage(null)
		            .payload(payload)
		            .build();
	}
	
	public static <T> RootEntity<T> error(String errorMessage){
		return RootEntity.<T>builder()
	            .status(500)
	            .errorMessage(errorMessage)
	            .payload(null)
	            .build();
	}
	
}
