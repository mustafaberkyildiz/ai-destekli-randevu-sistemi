package com.mustafaberk.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mustafaberk.dto.DtoDepartment;
import com.mustafaberk.dto.DtoDepartmentIU;
import com.mustafaberk.enums.AppointmentStatus;
import com.mustafaberk.exception.BaseException;
import com.mustafaberk.exception.ErrorMessage;
import com.mustafaberk.exception.MessageType;
import com.mustafaberk.model.Appointment;
import com.mustafaberk.model.Department;
import com.mustafaberk.model.Doctor;
import com.mustafaberk.repository.AppointmentRepository;
import com.mustafaberk.repository.DepartmentRepository;
import com.mustafaberk.repository.DoctorLeaveRepository;
import com.mustafaberk.repository.DoctorRepository;
import com.mustafaberk.service.IAppointmentService;
import com.mustafaberk.service.IDepartmentService;

@Service
public class DepartmentServiceImpl implements IDepartmentService{

    private final DoctorLeaveRepository doctorLeaveRepository;

	@Autowired
	private DepartmentRepository departmentRepository;
		
	@Autowired
	private IAppointmentService appointmentService;
	
	@Autowired
	private AppointmentRepository appointmentRepository;

    DepartmentServiceImpl(DoctorLeaveRepository doctorLeaveRepository) {
        this.doctorLeaveRepository = doctorLeaveRepository;
    }
	
	@Override
	public DtoDepartment saveDepartment(DtoDepartmentIU dtoDepartmentIU) {
		
		boolean isThere = departmentRepository.existsByName(dtoDepartmentIU.getName());
		if (isThere) {
			throw new BaseException(new ErrorMessage(MessageType.DEPARTMENT_ALREADY_EXIST, null));
		}
		
		Department department = new Department();
		
		department.setName(dtoDepartmentIU.getName());
		
		departmentRepository.save(department);
		
		DtoDepartment dtoDepartment = new DtoDepartment();
		
		BeanUtils.copyProperties(department, dtoDepartment);
		
		return dtoDepartment;
	}

	@Override
	public List<DtoDepartment> getAllDepartment() {
		List<DtoDepartment> dtoDepartmentList = new ArrayList<>();
		
		for ( Department department : departmentRepository.findByIsActive(true) ) {
			DtoDepartment dtoDepartment = new DtoDepartment();
			dtoDepartment.setActive(department.getIsActive());
			dtoDepartment.setName(department.getName());
			dtoDepartment.setId(department.getId());
			dtoDepartment.setCreateTime(department.getCreateTime());
			dtoDepartmentList.add(dtoDepartment);
		}
		
		return dtoDepartmentList;
	}

	@Override
	public void disableDepartment(Long departmentId) {
		
		Department department = departmentRepository.findById(departmentId).
													 orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DEPARTMENT_NOT_FOUND, null))  );
		

		if (!department.getIsActive()) {
			throw new BaseException(new ErrorMessage(MessageType.DEPARTMENT_ALREADY_DISABLE, null));
		}
		
		List<Appointment> appointmentList = appointmentRepository.findFutureAppointmentsFromDateTime(department, LocalDate.now(), LocalTime.now());
		

		for (Appointment appointment : appointmentList) {
			if (appointment.getStatus()==AppointmentStatus.SCHEDULED) {
				appointmentService.cancelledByAdmin(appointment.getId());
			}
		}
		
		department.setIsActive(false);
		departmentRepository.save(department);
		
	}

	@Override
	public void enableDepartment(Long departmentId) {
		
		Department department = departmentRepository.findById(departmentId).
				 orElseThrow( ()->new BaseException(new ErrorMessage(MessageType.DEPARTMENT_NOT_FOUND, null))  );
		

		if (department.getIsActive()) {
			throw new BaseException(new ErrorMessage(MessageType.DEPARTMENT_ALREADY_ENABLE, null));
		}
		
		department.setIsActive(true);
		departmentRepository.save(department);
	}

	@Override
	public List<DtoDepartment> getAllDepartmentForAdmin() {
		
		List<DtoDepartment> dtoDepartmentList = new ArrayList<>();
		
		for ( Department department : departmentRepository.findAll() ) {
			DtoDepartment dtoDepartment = new DtoDepartment();
			dtoDepartment.setActive(department.getIsActive());
			dtoDepartment.setName(department.getName());
			dtoDepartment.setId(department.getId());
			dtoDepartment.setCreateTime(department.getCreateTime());
			dtoDepartmentList.add(dtoDepartment);
		}
		
		return dtoDepartmentList;
	}
	

}
