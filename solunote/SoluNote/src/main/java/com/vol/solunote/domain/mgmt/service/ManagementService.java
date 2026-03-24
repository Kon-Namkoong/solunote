package com.vol.solunote.domain.mgmt.service;

import com.vol.solunote.comm.OffsetPageable;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import com.vol.solunote.model.type.DefaultService;
import com.vol.solunote.model.vo.menu.MenuListVo;
import com.vol.solunote.model.vo.siteuser.UserRegisterVo;

public interface ManagementService extends DefaultService {


	public List <UserRegisterVo> getList(int activeMenu,OffsetPageable offsetPageable,int userType,String tcId ,String tcName ,String tcPhone, String searchStartDate, String searchEndDate) throws Exception;
	
	public	List<MenuListVo> getAuthList() throws Exception;
	
	public String checkCode(String code) throws Exception;

	public JSONObject insertContents(Map<String, String> paramMap)   throws Exception;
	
	public void deleteUser(String[] userId) throws IOException;
	
	public void saveUser(String[] userId) throws IOException;
	
	public void permanentlyUser(String[] userId) throws IOException;

	public List<Map<String, Object>> userinfo(String userId) throws Exception;
	
	public UserRegisterVo getUser(String userId) throws Exception;
	
	public void changeUserinfo(String userId, String userPw, String userName, String userPhone, String userEmail, String userLevel,String url ) throws IOException;
	
    public List<Map<String, Object>> getUploadDirList(OffsetPageable offsetPageable) throws Exception;
    
    public void uploadDirRegister(String uploadDir, String uploadDirUseYn , String uploadDirCategory) throws IOException;
    
    public int checkUploadDir(String uploadDir) throws Exception;
    
    public void trainChangeToN() throws IOException;
    
    public void testChangeToN() throws IOException;
    
    public void changeToY(String uploadDirChange) throws IOException;
    
    public List<Map<String, Object>> uploadDirInfo(String uploadDir) throws Exception;
    
	public void changeMeetingInfo(String meetingTitle,String meetingValue,String meetingUseYn,String meetingDetail) throws IOException;
	
	public void deleteMeetingInfo(String[] meetingTitle) throws IOException;

	public List<Map<String, Object>> getSettingList(OffsetPageable offsetPageable,String useYn,String searchType, String searchKeyword,String searchStartDate,String searchEndDate) throws Exception;
	
	public int checkSetting(String settingTitle) throws Exception;
		
	public void settingRes(String settingTitle, String settingValue, String settingUseYn,String settingDetail) throws IOException;
	
	public List<Map<String, Object>> settingInfo(String settingTitle) throws Exception;

	public void changeSettingInfo(String settingTitleChange,String settingValueChange,String settingUseYnChange,String settingDetailChange) throws IOException;
	
	public void deleteSettingInfo(String[] settingTitle) throws IOException;
	
	public List<Map<String, Object>> getSettingConfigList() throws Exception;
	
	// 부대관리
	public int checkDivision(String division) throws Exception;
	
	public void divisionRes(String division, String armyName) throws IOException;
	
	public List<Map<String, Object>> divisionInfo(String settingTitle) throws Exception;
	
	public void changeDivisionInfo(String division,String armyNameChange,int seq) throws IOException;
	
	public void deleteDivisionInfo(String[] division) throws IOException;
	
	void excelForm(HttpServletResponse response) throws IOException;
	
	List<Map<String, Object>> getExcelList(MultipartFile file) throws Exception;
		
}

