package com.vol.solunote.comm.model;

public enum Color {
    BLACK("Blacks"), 
    BLUE("Blue"), 
    RED("Red"), 
    YELLOW("Yellow"), 
    GREEN("Green"),
    ORANGE("Orange"), 
    PURPLE("Purple"), 
    WHITE("White");
    
    private final String displayValue;
    
    private Color(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
}