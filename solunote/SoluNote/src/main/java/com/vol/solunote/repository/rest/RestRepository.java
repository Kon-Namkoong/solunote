package com.vol.solunote.repository.rest;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.vol.solunote.model.vo.rest.DomainVo;
import com.vol.solunote.model.vo.rest.RestUserVo;
import com.vol.solunote.model.vo.rest.STTResultVo;

public interface RestRepository {
	public RestUserVo getMember(RestUserVo userInfo) throws Exception;
	
	public int insertWavFile(Map<String,Object> param) throws Exception;

	public DomainVo getDomainInfo() throws Exception;

	public STTResultVo getSTTQ(int sttSeq) throws Exception;
	
	public List<STTResultVo> getSTTQList() throws Exception;

	public List<STTResultVo> getSTTResult(@Param("sttSeq") int sttSeq) throws Exception;

	public int setUpdateSTT(int sttSeq) throws Exception;

	public STTResultVo getLastSTTSEQ(String domainCode) throws Exception;
}
