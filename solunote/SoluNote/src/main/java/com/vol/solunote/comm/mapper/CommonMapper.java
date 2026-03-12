package com.vol.solunote.comm.mapper;

import java.util.List;
import java.util.Map;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.vo.DomainVo;

public interface CommonMapper {
 
    public List<DomainVo> selectDomainList() throws Exception;
    
    public List<DomainVo> selectSchedulerDomainList() throws Exception;
    
	public Long selectRatio()  throws Exception;

	public Map<String, Object> selectRatioConfigration() throws Exception;

	public List<String> selectServerList() throws Exception;
	
	public List<Map<String, Object>> selectInfoDivision(OffsetPageable offsetPageable,String division , int getlist) throws Exception;
	
}
