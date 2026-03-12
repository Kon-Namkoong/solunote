package com.vol.solunote.repository.siteuser;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.mapper.siteuser.SiteUserMapper;
import com.vol.solunote.model.vo.siteuser.SiteUser;
import com.vol.solunote.model.vo.siteuser.SiteUserVo;
import com.vol.solunote.model.vo.siteuser.UserRegisterVo;
import org.springframework.stereotype.Repository;
@Repository
public class SiteUserRepositoryImpl implements SiteUserRepository {

	@Autowired
	private	SiteUserMapper	mapper;	
	
	@Override
	public SiteUserVo findUserInfo(int seq) throws Exception
	{
		return	mapper.findUserInfo(seq);
	}
	
	@Override
	public int updateUser(Map<String,String> userVo) throws Exception
	{
		return	mapper.updateUser(userVo);
	}
	
	@Override
	public int deleteUser(int seq) throws Exception
	{
		return	mapper.deleteUser(seq);
	}
	
	@Override
	public int insertUser(Map<String, String> paramMap) throws Exception 
	{
		return	mapper.insertUser(paramMap);
	}

	@Override
	public int checkUserId(String code) throws Exception 
	{
		return	mapper.checkUserId(code);
	}

	@Override
	public SiteUserVo getUser(String userId) throws Exception
	{
		return	mapper.getUser(userId);
	}
	
	@Override
	public SiteUser readUser(String username) 
	{
		return	mapper.readUser(username);
	}

	@Override
	public List<String> readAuthority(String username)
	{
		return	mapper.readAuthority(username);
	}

	@Override
	public	List<UserRegisterVo> getList(int activeMenu ,OffsetPageable offsetPageable,int userType,String tcId ,String tcName ,String tcPhone,String searchStartDate, String searchEndDate) throws Exception
	{
		return	mapper.getList(activeMenu, offsetPageable, userType, tcId, tcName, tcPhone, searchStartDate, searchEndDate);
	}

	@Override
	public	List<Map<String, Object>>  userinfo(String userId) throws Exception
	{
		return	mapper.userinfo(userId);
	}

	@Override
	public	void removeUser(String[] userId)
	{
		mapper.removeUser(userId);
	}
	
	@Override
	public	void saveUser(String[] userId)
	{
		mapper.saveUser(userId);
	}
	
	@Override
	public	void	permanentlyUser(String[] userId)
	{
		mapper.permanentlyUser(userId);
	}
	
	@Override
	public UserRegisterVo getUserForReg(String userId) throws Exception
	{
		return	mapper.getUserForReg(userId);
	}
	
	@Override
	public	void changeUserinfo(String userId, String userPw, String userName, String userPhone, String userEmail, String userLevel )
	{
		mapper.changeUserinfo(userId, userPw, userName, userPhone, userEmail, userLevel);
	}
	
	@Override
	public	void changeUserinfoNotPw(String userId, String userName, String userPhone, String userEmail, String userLevel )
	{
		mapper.changeUserinfoNotPw(userId, userName, userPhone, userEmail, userLevel);
	}

}
