package com.vol.solunote.repository.server;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.mapper.server.ServerMapper;
import com.vol.solunote.model.vo.server.AgentVo;
import com.vol.solunote.model.vo.server.DomainVo;
import com.vol.solunote.model.vo.server.ServerVo;
import org.springframework.stereotype.Repository;
@Repository
public class ServerRepositoryImpl implements ServerRepository {

	@Autowired
	private	ServerMapper	mapper;
	
	@Override
	public int updateServer(Map<String, String> server17Vo) throws Exception {		
		return mapper.updateServer(server17Vo);
	}

	@Override
	public List<ServerVo> selectServerList() throws Exception {
		return mapper.selectServerList();
	}

	@Override
	public int checkServerInfo(Map<String, Object> reqMap) throws Exception {
		// TODO Auto-generated method stub
		return mapper.checkServerInfo(reqMap);
	}

	@Override
	public int updateAgent(Map<String, String> agentVo) throws Exception {
		// TODO Auto-generated method stub
		return mapper.updateAgent(agentVo);
	}

	@Override
	public int insertAgent(Map<String, String> agentVo) throws Exception {
		// TODO Auto-generated method stub
		return mapper.insertAgent(agentVo);
	}

	@Override
	public AgentVo findAgentInfo(int seq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.findAgentInfo(seq);
	}

	@Override
	public int deleteAgent(int seq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.deleteAgent(seq);
	}

	@Override
	public int deleteDomain(int seq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.deleteDomain(seq);
	}

	@Override
	public int checkDomain(Map<String, Object> param) throws Exception {
		// TODO Auto-generated method stub
		return mapper.checkDomain(param);
	}

	@Override
	public int insertDomain(Map<String, String> domain17Vo) throws Exception {
		// TODO Auto-generated method stub
		return mapper.insertDomain(domain17Vo);
	}

	@Override
	public int updateDomain(Map<String, String> domain17Vo) throws Exception {
		// TODO Auto-generated method stub
		return mapper.updateDomain(domain17Vo);
	}

	@Override
	public DomainVo findDomainInfo(int seq) throws Exception {
		// TODO Auto-generated method stub
		return mapper.findDomainInfo(seq);
	}

}
