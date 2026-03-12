package com.vol.solunote.mapper.siteuser;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.siteuser.SiteUser;
import com.vol.solunote.model.vo.siteuser.SiteUserVo;
import com.vol.solunote.model.vo.siteuser.UserRegisterVo;

@Mapper
public interface SiteUserMapper {
	
	public SiteUserVo findUserInfo(int seq) throws Exception;
	
	public int updateUser(Map<String,String> userVo) throws Exception;
	
	public int deleteUser(int seq) throws Exception;
	
	public int insertUser(Map<String, String> paramMap) throws Exception;

	public int checkUserId(String code) throws Exception;

	public SiteUserVo getUser(String userId) throws Exception;
	
	public SiteUser readUser(String username);

	public List<String> readAuthority(String username);

	public	List<UserRegisterVo> getList(int activeMenu ,OffsetPageable offsetPageable,int userType,String tcId ,String tcName ,String tcPhone,String searchStartDate, String searchEndDate) throws Exception;

	public	List<Map<String, Object>>  userinfo(String userId) throws Exception;

	public	void removeUser(String[] userId);
	
	public	void saveUser(String[] userId);	
	
	void permanentlyUser(String[] userId);
	
	public UserRegisterVo getUserForReg(String userId) throws Exception;
	
	void changeUserinfo(String userId, String userPw, String userName, String userPhone, String userEmail, String userLevel );
	
	void changeUserinfoNotPw(String userId, String userName, String userPhone, String userEmail, String userLevel );
	
}
