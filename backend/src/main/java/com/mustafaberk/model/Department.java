package com.mustafaberk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "department")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department extends BaseEntity{

	@JsonProperty("active")
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "name" , unique = true)
	private String name;
	
}
