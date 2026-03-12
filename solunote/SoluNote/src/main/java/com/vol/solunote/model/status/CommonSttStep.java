package com.vol.solunote.model.status;

public enum CommonSttStep {
	대기중(0),
	시작(1), 
    진행중(2),  
    완료(3),
    에러_알수없는채널정보(8),
    에러_파일없음(11),
    에러_오디오변환실패(12), 
    에러_청취음원변환실패(14)  
    ;

    private final int value;
    
    private CommonSttStep(int displayValue) {
        this.value = displayValue;
    }
    
    public int getValue() {
        return value;
    }

}
