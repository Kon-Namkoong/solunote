package com.vol.solunote.model.status;

public enum CommonTrainStep {
	데이터수집(1),
	데이터전처리_RawText(2), 
    데이터전처리_Wst(3),  
    단어장생성(4),
    언어모델학습_N_GRAM(5),
    언어모델학습_Interpolation(6), 
    발음사전생성(7),  
    언어모델빌드(8),
    모델이동(9),
	학습완료(10)    
    ;

    private final int value;
    
    private CommonTrainStep(int displayValue) {
        this.value = displayValue;
    }
    
    public int getValue() {
        return value;
    }

}
