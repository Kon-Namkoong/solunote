package com.vol.solunote.repository.division;

import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.mapper.division.DivisionMapper;
import org.springframework.stereotype.Repository;


@Repository
public class DivisionRepositoryImpl implements DivisionRepository {

	@Autowired
	private	DivisionMapper	mapper;
	
	@Override
    public 	int checkDivision(String division) throws Exception
    {
    	return	mapper.checkDivision(division);
    }
    
	@Override
    public	void divisionRes(String division, String armyName)
    {
    	mapper.divisionRes(division, armyName);
    }
    
	@Override
    public	List<Map<String, Object>>  divisionInfo(String division) throws Exception
    {
    	return	mapper.divisionInfo(division);
    }
    
	@Override
    public	void changeDivisionInfo(String division,String armyNameChange,int seq)
    {
    	mapper.changeDivisionInfo(division, armyNameChange, seq);
    }
    
	@Override
    public	void deleteDivisionInfo(String[] division)
    {
    	mapper.deleteDivisionInfo(division);	
    }
	
	@Override
	public 	List<Map<String, Object>> selectInfoDivision(OffsetPageable offsetPageable,String division , int getlist) throws Exception
	{	
		List<Map<String, Object>> list = mapper.selectInfoDivision(offsetPageable,division,getlist);		
		return list;
	}
}
