package com.vol.solunote.domain.mgmt.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.repository.division.DivisionRepository;
import com.vol.solunote.repository.meeting.MeetingRepository;
import com.vol.solunote.repository.menu.MenuRepository;
import com.vol.solunote.repository.siteuser.SiteUserRepository;
import com.vol.solunote.repository.upload.UploadDiskRepository;
import com.vol.solunote.model.vo.menu.MenuListVo;
import com.vol.solunote.model.vo.siteuser.UserRegisterVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
public class ManagementServiceImpl implements ManagementService {


	@Autowired
	SiteUserRepository	userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UploadDiskRepository uploadDiskRepository;
	
	@Autowired
	MeetingRepository 	meetingRepository;

	@Autowired
	MenuRepository		menuRepository;
	
	@Autowired
	DivisionRepository	divisionRepository;
	
	@Override
	public List<UserRegisterVo> getList(int activeMenu ,OffsetPageable offsetPageable,int userType,String tcId ,String tcName ,String tcPhone,String searchStartDate, String searchEndDate) throws Exception {
		
		List<UserRegisterVo> list = userRepository.getList(activeMenu, offsetPageable,userType,tcId ,tcName ,tcPhone,searchStartDate,searchEndDate);
		
			
		return list;
	}

	@Override
	public List<MenuListVo> getAuthList() throws Exception {
		
		List<MenuListVo> menuList = menuRepository.getAuthList();
		
		return menuList;
	}
	
	
	@Override
	public String checkCode(String code) throws Exception {
		
		String result = null;
		
		List<Map<String, Object>> list = userRepository.userinfo(code);
		
		if ( list.size() == 1 ) {
			Map<String, Object> map = list.get(0);
			result = map.get("tcUseyn").toString();
//			result = Integer.toString((int) map.get("tcUseyn"));
		} else if ( list.size() == 0 ) {
			result = "-1";
		} else {
			result = "2";
		}
		
		return result;
	}
	
	
	private JSONObject insertUserInfo(Map<String, String> paramMap) throws Exception {
		
		JSONObject json = new JSONObject();
		int checkuserId = userRepository.checkUserId(paramMap.get("tcId").trim());
		
		if (checkuserId == 0) {
			String tcPassword = paramMap.get("tcPw");
			
			if(tcPassword != null && !"".equals(tcPassword)) {
				String securedPasswd = passwordEncoder.encode(tcPassword);
				paramMap.put("tcPw", securedPasswd );
			}
				
			userRepository.insertUser(paramMap);
			
			json.put("result", true);
			json.put("errorCode", "");			
		}else {
			json.put("result",false );
			json.put("errorCode", "중복");			
		}
					
		return json;
	}
	
	
	@Override
	@Transactional
	public JSONObject insertContents(Map<String, String> paramMap) throws Exception {

		JSONObject json = new JSONObject();
		
		json = insertUserInfo(paramMap);
		
		return json;
	}
	
	
	@Override
	public void deleteUser(String[] userId) throws IOException {
		userRepository.removeUser(userId);
		
	}

	
	@Override
	public void saveUser(String[] userId) throws IOException {
		userRepository.saveUser(userId);
		
	}
	
	@Override
	public void permanentlyUser(String[] userId) throws IOException {
		userRepository.permanentlyUser(userId);
		
	}	
	
	@Override
	public List<Map<String, Object>> userinfo(String userId) throws Exception {
		
		List<Map<String, Object>> list = userRepository.userinfo(userId);
		
			
		return list;
	}
	
	@Override
	public UserRegisterVo getUser(String userId) throws Exception {
		return userRepository.getUserForReg(userId);
	}
	
	@Override
	public void changeUserinfo(String userId, String userPw, String userName, String userPhone, String userEmail, String userLevel,String url ) throws IOException {
			
		if(userPw != null && !"".equals(userPw)) {
			if (!url.equals("api")) {
				userPw = passwordEncoder.encode(userPw);
			}
			userRepository.changeUserinfo(userId,userPw,userName,userPhone,userEmail,userLevel);
		}else {
			userRepository.changeUserinfoNotPw(userId,userName,userPhone,userEmail,userLevel);
		}
		
			
	}
	
    
    @Override
    public List<Map<String, Object>> getUploadDirList(OffsetPageable offsetPageable) throws Exception {
        List<Map<String, Object>> list = uploadDiskRepository.getUploadDirList(offsetPageable, null);            
        return list;
    }
    
    
    @Override
    public void uploadDirRegister(String uploadDir, String uploadDirUseYn ,String uploadDirCategory) throws IOException {
            
    	uploadDiskRepository.uploadDirRegister( uploadDir, uploadDirUseYn ,uploadDirCategory);    
            
    }
    
    
    @Override
    public int checkUploadDir(String uploadDir) throws Exception {
                
        return uploadDiskRepository.checkUploadDir(uploadDir);
    }
    	
	
	
	@Override
	public List<Map<String, Object>> uploadDirInfo(String uploadDir) throws Exception {
		
		List<Map<String, Object>> list = uploadDiskRepository.uploadDirInfo(uploadDir);
		
			
		return list;
	}
	
	
	@Override
	public void trainChangeToN() throws IOException {
		uploadDiskRepository.trainChangeToN();
		
	}

	@Override
	public void testChangeToN() throws IOException {
		uploadDiskRepository.testChangeToN();
		
	}
	
	@Override
	public void changeToY(String uploadDirChange) throws IOException {
		uploadDiskRepository.changeToY(uploadDirChange);
		
	}
	
	
	@Override
	public void changeMeetingInfo(String meetingTitle,String meetingValue,String meetingUseYn,String meetingDetail) throws IOException {
			
		meetingRepository.changeMeetingInfo(meetingTitle,meetingValue,meetingUseYn,meetingDetail);
				
	}
    
	@Override
	public void deleteMeetingInfo(String[] meetingTitle) throws IOException {
		meetingRepository.deleteMeetingInfo(meetingTitle);
		
	}
    
	
    @Override
    public List<Map<String, Object>> getSettingList(OffsetPageable offsetPageable,String useYn,String searchType, String searchKeyword,String searchStartDate,String searchEndDate) throws Exception {
        
        List<Map<String, Object>> list = meetingRepository.getSettingList(offsetPageable,useYn,searchType,searchKeyword,searchStartDate,searchEndDate);
                    
        return list;
    }
    
    
    @Override
    public void settingRes(String settingTitle, String settingValue, String settingUseYn,String settingDetail) throws IOException {
            
    	meetingRepository.settingRes( settingTitle, settingValue,  settingUseYn, settingDetail);    
            
    }
    
    
    @Override
    public int checkSetting(String settingTitle) throws Exception {
        
        
        return meetingRepository.checkSetting(settingTitle);
    }    
    
    
	@Override
	public List<Map<String, Object>> settingInfo(String settingTitle) throws Exception {
		
		List<Map<String, Object>> list = meetingRepository.settingInfo(settingTitle);
		
			
		return list;
	}    
    
    
	@Override
	public void changeSettingInfo(String settingTitleChange,String settingValueChange,String settingUseYnChange,String settingDetailChange) throws IOException {
			
		meetingRepository.changeSettingInfo(settingTitleChange,settingValueChange,settingUseYnChange,settingDetailChange);
				
	}    
	
	@Override
	public void deleteSettingInfo(String[] settingTitle) throws IOException {
		meetingRepository.deleteSettingInfo(settingTitle);
		
	}
	
	
    @Override
    public List<Map<String, Object>> getSettingConfigList() throws Exception {
        
        List<Map<String, Object>> list = meetingRepository.getSettingConfigList();
                    
        return list;
    }	
	
    @Override
    public int checkDivision(String division) throws Exception {
        
        
        return divisionRepository.checkDivision(division);
    }
    
    @Override
    public void divisionRes(String division, String armyName) throws IOException {
            
     divisionRepository.divisionRes( division, armyName);    
            
    }
    
	@Override
	public List<Map<String, Object>> divisionInfo(String division) throws Exception {
		
		List<Map<String, Object>> list = divisionRepository.divisionInfo(division);
		
			
		return list;
	}    
    
	
	@Override
	public void changeDivisionInfo(String division,String armyNameChange,int seq) throws IOException {
			
		divisionRepository.changeDivisionInfo(division,armyNameChange,seq);
				
	}
	
	@Override
	public void deleteDivisionInfo(String[] division) throws IOException {
		divisionRepository.deleteDivisionInfo(division);
		
	}
	
	
	@Override
	public void excelForm(HttpServletResponse response) throws IOException {

	    XSSFWorkbook workbook = new XSSFWorkbook();
	    XSSFSheet sheet = workbook.createSheet();

	    
	    Row headerRow = sheet.createRow(0);
	    Cell keywordCell = headerRow.createCell(0);
	    keywordCell.setCellValue("아이디");
	    
	    Cell pronunciationCell = headerRow.createCell(1);
	    pronunciationCell.setCellValue("이름");	    

	    // 파일 출력 로직은 동일
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    workbook.write(baos);
	    workbook.close();

	    ServletOutputStream out = response.getOutputStream();
	    out.write(baos.toByteArray());
	    
	    response.setContentType("application/octet-stream");
	    response.setHeader("filename_base64", Base64.getEncoder().encodeToString(("다계정 등록양식.xlsx").getBytes()));

	    out.flush();
	    out.close();
	}
	
	
	@Override
	public List<Map<String, Object>> getExcelList(MultipartFile file) throws Exception {
	    List<Map<String, Object>> pages = new ArrayList<>();

	    boolean isFirstRow = true;
	    
	    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            if (isFirstRow) {
	                isFirstRow = false;
	                continue; // 첫 번째 행을 건너뜀
	            }
	        		        	
	            Map<String, Object> list = new HashMap<>();
	            
	            Cell keywordCell = row.getCell(0);
	            Cell speechCell = row.getCell(1);


                String tcId = (keywordCell != null) ? keywordCell.getStringCellValue() : "";
                String tcName = (speechCell != null) ? speechCell.getStringCellValue() : "";

                
                list.put("tcId", tcId);
                list.put("tcName", tcName);
                
                pages.add(list);
	        }
	    } catch (IOException e) {
	        throw new Exception("Excel 파일 읽기 실패", e);
	    }
	    
	    return pages;
	}	
	
}
