package com.vol.solunote.repository.division;

import java.util.List;
import java.util.Map;

public interface DivisionRepository {
    public 	int checkDivision(String division) throws Exception;
    public	void divisionRes(String division, String armyName);    
    public	List<Map<String, Object>>  divisionInfo(String division) throws Exception;
    public	void changeDivisionInfo(String division,String armyNameChange,int seq);  
    public	void deleteDivisionInfo(String[] division);
}
