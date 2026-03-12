package com.vol.solunote.repository.server;

import java.util.List;
import java.util.Map;

import com.vol.solunote.model.vo.server.AgentVo;
import com.vol.solunote.model.vo.server.DomainVo;
import com.vol.solunote.model.vo.server.ServerVo;



public interface ServerRepository {
	// update/3
	public int updateServer(Map<String,String> server17Vo) throws Exception;
	
	public List<ServerVo> selectServerList() throws Exception;
	
	public int checkServerInfo(Map<String,Object>reqMap) throws Exception;
	// update/2 에서 사용
	public int updateAgent(Map<String,String> agentVo) throws Exception;
	// insert/2 에서 사용
	public int insertAgent(Map<String,String> agentVo) throws Exception;
	// popup/2 에서 사용
	public AgentVo findAgentInfo(int seq) throws Exception;
	
	// delete/2 에서 사용
	public int deleteAgent(int seq) throws Exception;
	
	// delete/4 에서 사용
	public int deleteDomain(int seq) throws Exception;
	
	// check/4 에서 사용
	public int checkDomain(Map<String,Object> param) throws Exception;
	
	// insert/4 에서 사용
	public int insertDomain(Map<String,String> domain17Vo) throws Exception;

	// update/4 에서 사용
	public int updateDomain(Map<String,String> domain17Vo) throws Exception;
	
	// popup/4 에서 사용
	public DomainVo findDomainInfo(int seq) throws Exception;
}
