package com.mustafaberk.enums;

public enum AppointmentStatus {
	SCHEDULED("Planlandı"),
    COMPLETED("Tamamlandı"),
    CANCELLED("İptal Edildi"),
    DOCTOR_CANCELLED("Doktor İptal Etti"),
    NO_SHOW("Hasta Gelmedi");
    
    private final String context;
	
	private AppointmentStatus(String context) {
		this.context=context;
	}
	
	public String getContext() {
		return this.context;
	}
    
}
