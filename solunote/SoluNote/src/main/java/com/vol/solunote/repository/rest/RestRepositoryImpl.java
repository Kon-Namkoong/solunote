package com.vol.solunote.repository.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.mapper.rest.RestMapper;
import com.vol.solunote.model.vo.rest.DomainVo;
import com.vol.solunote.model.vo.rest.RestUserVo;
import com.vol.solunote.model.vo.rest.STTResultVo;

import org.springframework.stereotype.Repository;
@Repository
public class RestRepositoryImpl implements RestRepository {

	@Autowired
	private RestMapper mapper;
	
	@Override
	public RestUserVo getMember(RestUserVo userInfo) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getMember(userInfo);
	}

	@Override
	public int insertWavFile(Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		return mapper.insertWavFile(param);
	}

	@Override
	public DomainVo getDomainInfo() throws Exception {
		// TODO Auto-generated method stub
		return mapper.getDomainInfo();
	}

	@Override
	public STTResultVo getSTTQ(int sttSeq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getSTTQ(sttSeq);
	}

	@Override
	public List<STTResultVo> getSTTQList() throws Exception {
		// TODO Auto-generated method stub
		return mapper.getSTTQList();
	}

	@Override
	public List<STTResultVo> getSTTResult(int sttSeq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getSTTResult(sttSeq);
	}

	@Override
	public int setUpdateSTT(int sttSeq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.setUpdateSTT(sttSeq);
	}

	@Override
	public STTResultVo getLastSTTSEQ(String domainCode) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getLastSTTSEQ(domainCode);
	}

}
