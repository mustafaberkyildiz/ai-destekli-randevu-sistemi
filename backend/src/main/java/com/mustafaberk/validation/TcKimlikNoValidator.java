package com.mustafaberk.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TcKimlikNoValidator implements ConstraintValidator<ValidTcKimlikNo, String>{

	@Override
	public boolean isValid(String tc, ConstraintValidatorContext context) {

		if (tc == null || !tc.matches("\\d{11}")) return false;
		
		int[] digits = tc.chars().map(c -> c - '0').toArray();




		
        if (digits[0] == 0) return false;


        int sumOdd = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int sumEven = digits[1] + digits[3] + digits[5] + digits[7];


        
        int digit10 = ((sumOdd * 7) - sumEven) % 10;
        digit10 = (digit10 + 10) % 10;

        int digit11 = (sumOdd + sumEven + digits[9]) % 10;
        digit11 = (digit11 + 10) % 10;

        return digits[9] == digit10 && digits[10] == digit11;
	}

}
