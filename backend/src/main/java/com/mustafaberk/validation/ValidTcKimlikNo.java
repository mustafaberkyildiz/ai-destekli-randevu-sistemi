package com.mustafaberk.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TcKimlikNoValidator.class)
public @interface ValidTcKimlikNo {

	
		String message() default "Geçersiz TC Kimlik No";

	    Class<?>[] groups() default {};
	    
	    Class<? extends Payload>[] payload() default {};
	    
	    /* 	
			message() → “Hata mesajı” 
			groups() → “Ne zaman çalışacak?”
			payload() → “Ekstra bilgi var mı?”
	    */
	  


}
