package com.mustafaberk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.model.User;
import com.mustafaberk.repository.DoctorRepository;

@Component("reviewSecurity")
public class ReviewSecurity {

	@Autowired
	private DoctorRepository doctorRepository;
	
	public boolean hasDoctorAccess(Authentication authentication, Long doctorId) {

        User user = (User) authentication.getDetails();

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isDoctor = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        
        if (isAdmin) {
            return true;
        }
        
        if (!isDoctor) {
			return false;
		}

        Doctor doctor = doctorRepository.findByUser(user).orElseThrow( ()-> new BaseException(new ErrorMessage(MessageType.DOCTOR_NOT_FOUND, null)) );


        return doctor.getId().equals(doctorId);
    }
	
}
