package com.mustafaberk.model;

import java.time.LocalDateTime;

import com.mustafaberk.enums.VertificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reset_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetCode extends BaseEntity{

	@Column(name = "code",nullable = false)
    private String code;

    @Column(name = "expire_time",nullable = false)
    private LocalDateTime expireTime;
	
    @Column(name = "used",nullable = false)
    private Boolean used = false;// kod henuz kullanilmadigindan direk en basta olustururken false olarak olusturuyoruz

    
    @Column(name = "vertification_type")
    @Enumerated(EnumType.STRING)
    private VertificationType type;
    
    @Column(name = "new_email",nullable = true)
    private String newEmail;
    
	@ManyToOne
    private User user;
}
