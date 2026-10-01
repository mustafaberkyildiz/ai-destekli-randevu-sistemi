package com.mustafaberk.handler;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.mustafaberk.exception.BaseException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {BaseException.class})
	public ResponseEntity<ApiError<?>> handleBaseException(BaseException ex , WebRequest request) {
		return ResponseEntity.badRequest().body(createApiError(ex.getMessage(), request));
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParseException(HttpMessageNotReadableException ex , WebRequest request) {

        String message = "JSON format hatalı. Doğru tarih formatı: yyyy-MM-dd";

        return ResponseEntity
                .badRequest()
                .body(createApiError(message, request));
    }
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ApiError<Map<String, List<String>>>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex , WebRequest request) {
		Map<String, List<String>> map = new HashMap<>();
		
		for (ObjectError objError : ex.getBindingResult().getAllErrors()) {

			String fieldName = ((FieldError)objError).getField();
			
			if(map.containsKey(fieldName)) { 
				map.put(fieldName, addListValue(map.get(fieldName), objError.getDefaultMessage()));
			}
			else {
				map.put(fieldName, addListValue(new ArrayList<>(), objError.getDefaultMessage()));
			}	
		}

		return ResponseEntity.badRequest().body(createApiError(map, request));



	}
	
	@ExceptionHandler(value = {AuthorizationDeniedException.class})
	public ResponseEntity<String> handleAuthorizationDenied(AuthorizationDeniedException ex) {
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Yetkiniz yok");
	}
	
	private List<String> addListValue(List<String> list , String newValue){
		list.add(newValue);
		return list;
	}
	
	private String getHostName() {
		try {
			return Inet4Address.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <E> ApiError<E> createApiError(E message , WebRequest request){
		ApiError<E> apiError = new ApiError<>();
		apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		Exception<E> exception = new Exception<>();
		exception.setDate(LocalDateTime.now());
		exception.setPath(request.getDescription(false).substring(4));
		exception.setMessage(message);
		exception.setHostName(getHostName());
		
		apiError.setExpception(exception);
		
		return apiError;
	}
	
}
