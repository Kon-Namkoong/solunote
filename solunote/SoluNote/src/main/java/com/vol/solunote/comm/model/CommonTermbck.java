package com.vol.solunote.comm.model;

public enum CommonTermbck {
    WEEKLY("주별"), 
    MONTHLY("월별"), 
    QUARTER("분기"), 
    SEMIANNUAL("반기"), 
    YEAR("년별")
    ;

    private final String displayValue;
    
    private CommonTermbck(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }

}