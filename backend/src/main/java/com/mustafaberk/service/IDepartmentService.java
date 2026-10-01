package com.mustafaberk.service;

import java.util.List;

import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;

public interface IDepartmentService {

	public DtoDepartment saveDepartment(DtoDepartmentIU dtoDepartmentIU);
	
	public List<DtoDepartment> getAllDepartment();
	
	public List<DtoDepartment> getAllDepartmentForAdmin();
	
	public void disableDepartment(Long departmentId);
	
	public void enableDepartment(Long departmentId);
}
