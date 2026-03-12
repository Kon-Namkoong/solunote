package com.vol.solunote.mapper.division;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DivisionMapper {
    public 	int checkDivision(String division) throws Exception;
    public	void divisionRes(String division, String armyName);    
    public	List<Map<String, Object>>  divisionInfo(String division) throws Exception;
    public	void changeDivisionInfo(String division,String armyNameChange,int seq);  
    public	void deleteDivisionInfo(String[] division);    
}
