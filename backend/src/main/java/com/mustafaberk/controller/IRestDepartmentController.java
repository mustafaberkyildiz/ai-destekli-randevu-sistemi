package com.mustafaberk.controller;

import java.util.List;

import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;

public interface IRestDepartmentController {

	public RootEntity<DtoDepartment> saveDepartment(DtoDepartmentIU dtoDepartmentIU);
	
	public RootEntity<List<DtoDepartment>> getAllDepartment();
	
	public RootEntity<List<DtoDepartment>> getAllDepartmentForAdmin();

	public RootEntity<List<Doctor>> getDoctorByDepartment(Department department);
	
	public RootEntity<Void> disableDepartment(Long departmentId);
	
	public RootEntity<Void> enableDepartment(Long departmentId);
}
