package com.mustafaberk.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.controller.IRestAdminDashboardController;
import com.mustafaberk.controller.RestBaseController;
import com.mustafaberk.controller.RootEntity;
import com.mustafaberk.dto.DtoAdminDashboard;
import com.mustafaberk.service.IAdminDashboardService;

@RequestMapping("/admin")
@RestController
public class RestAdminDashboardControllerImpl extends RestBaseController implements IRestAdminDashboardController{

	@Autowired
	private IAdminDashboardService adminDashboardService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/dashboard")
	@Override
	public RootEntity<DtoAdminDashboard> adminDashboard() {
		return ok(adminDashboardService.adminDashboard());
	}

	
}
