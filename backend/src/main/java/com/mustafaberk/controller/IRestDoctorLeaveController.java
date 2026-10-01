package com.mustafaberk.controller;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.dto.DtoDoctorLeave;
import com.mustafaberk.dto.DtoDoctorLeaveIU;
import com.mustafaberk.dto.DtoDoctorLeaveUpdate;

public interface IRestDoctorLeaveController {

	public RootEntity<DtoDoctorLeave> saveDoctorLeave(DtoDoctorLeaveIU doctorLeaveIU);
	
	public RootEntity<List<DtoDoctorLeave>> getAllDoctorLeave(Long doctorId);
	
	public RootEntity<DtoDoctorLeave> updateDoctorLeave(Long id , DtoDoctorLeaveUpdate doctorLeaveUpdate);//id->DoctorLeaveId
	
	public void cancelDoctorLeave(Long id);
	
	public RootEntity<List<DtoDoctorLeave>> getAllDoctorLeaveForAdmin(Long doctorId,LocalDate startDate,LocalDate endDate);
}
