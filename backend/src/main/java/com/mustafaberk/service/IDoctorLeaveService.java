package com.mustafaberk.service;

import java.time.LocalDate;
import java.util.List;

import com.mustafaberk.dto.DtoDoctorLeave;
import com.mustafaberk.dto.DtoDoctorLeaveIU;
import com.mustafaberk.dto.DtoDoctorLeaveUpdate;

public interface IDoctorLeaveService {

	public DtoDoctorLeave saveDoctorLeave(DtoDoctorLeaveIU doctorLeaveIU);
	
	public List<DtoDoctorLeave> getAllDoctorLeave(Long doctorId);
	
	public DtoDoctorLeave updateDoctorLeave(Long id, DtoDoctorLeaveUpdate doctorLeaveUpdate);//id->DoctorLeaveId
	
	public void cancelDoctorLeave(Long id);
	
	public List<DtoDoctorLeave> getAllDoctorLeaveForAdmin(Long doctorId,LocalDate startDate,LocalDate endDate);
	
}
