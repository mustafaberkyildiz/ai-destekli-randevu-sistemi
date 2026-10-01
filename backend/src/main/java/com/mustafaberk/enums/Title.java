package com.mustafaberk.enums;

public enum Title {
	 	DR("Dr."),
	    UZM_DR("Uzm. Dr."),
	    DOC_DR("Doç. Dr."),
	    PROF_DR("Prof. Dr.");

	    private final String displayName;

	    Title(String displayName) {
	        this.displayName = displayName;
	    }

	    public String getDisplayName() {
	        return displayName;
	    }
}
