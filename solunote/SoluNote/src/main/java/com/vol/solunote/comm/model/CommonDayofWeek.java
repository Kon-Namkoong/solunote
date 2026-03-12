package com.vol.solunote.comm.model;

public enum CommonDayofWeek {
	월요일(1),
	화요일(2), 
    수요일(3),  
    목요일(4),
    금요일(5),
    토요일(6),
    일요일(0)
    
    ;

    private final int value;
    
    private CommonDayofWeek(int displayValue) {
        this.value = displayValue;
    }
    
    public int getValue() {
        return value;
    }

}
