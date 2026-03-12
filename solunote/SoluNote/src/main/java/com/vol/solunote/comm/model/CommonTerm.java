package com.vol.solunote.comm.model;

public enum CommonTerm {
	일별(0),
	주별(1), 
    월별(2),  
    직접입력(3)
    ;

    private final int value;
    
    private CommonTerm(int displayValue) {
        this.value = displayValue;
    }
    
    public int getValue() {
        return value;
    }

}