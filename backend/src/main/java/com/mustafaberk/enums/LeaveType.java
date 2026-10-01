package com.mustafaberk.enums;

public enum LeaveType {
	ANNUAL_LEAVE("Yıllık İzin"), 
	SICK_LEAVE("Hastalık İzni"), 
	EMERGENCY("Acil İzin");
	
	private final String context;
	
	private LeaveType(String context) {
		this.context=context;
	}
	
	public String getContext() {
		return this.context;
	}
}





