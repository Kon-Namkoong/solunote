package com.vol.solunote.comm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import com.vol.solunote.comm.service.CommonSteelServiceImpl;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.comm.vo.DefaultVo;
import com.vol.solunote.comm.vo.DomainVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultController {
	
	private static Set<String> paramNameSet = new HashSet<>();


	@Resource(name="${service.class.commonService}")
	
	@Autowired
	protected CommonSteelServiceImpl commonService;
	
	protected String menuId = null;
	
	@PostConstruct
	private void init() {
		DefaultController.paramNameSet.add("offsetPageable");
		DefaultController.paramNameSet.add("activeMenu");
		DefaultController.paramNameSet.add("keyword");
		DefaultController.paramNameSet.add("search");
		DefaultController.paramNameSet.add("searchStartDate");
		DefaultController.paramNameSet.add("searchEndDate");
		DefaultController.paramNameSet.add("seq");
		DefaultController.paramNameSet.add("scheduleSeq");
		DefaultController.paramNameSet.add("meetSeq");
		DefaultController.paramNameSet.add("reliability");
		DefaultController.paramNameSet.add("changeTextValue");
		DefaultController.paramNameSet.add("caller");
		DefaultController.paramNameSet.add("modelId");
		DefaultController.paramNameSet.add("orderby");
	}

	protected void getDomainRationInfo(Model model) throws Exception {
		
		//DOMAIN LIST SETTING 
		List<DomainVo> domainList = commonService.selectDomainList();
		model.addAttribute("domainList", domainList);

	}
	
	protected void getSchedulerDomainList(Model model) throws Exception {
		
		//DOMAIN LIST SETTING 
		List<DomainVo> domainList = commonService.selectSchedulerDomainList();
		
		if(domainList != null && !domainList.isEmpty()){
			model.addAttribute("domainList", domainList);
		}
	}
	
	
	// 날짜 format yyyy-MM-dd
	protected void setSearchTerm(DefaultVo search) {

		Calendar c = Calendar.getInstance();
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//		String todayString = sf.format(date);
		String today = DateUtil.nowDate(4);
		
		if("dayTerm".equals(search.getSearchTermType())) {
			search.setSearchStartDate(today);
			search.setSearchEndDate(today);
		} else if("monthTerm".equals(search.getSearchTermType())) {
			c.add(Calendar.MONTH, -1);
			Date date = c.getTime();
			search.setSearchStartDate(sf.format(date));
			search.setSearchEndDate(today);
		} else if("yearTerm".equals(search.getSearchTermType())) {
			c.add(Calendar.YEAR, -1);
			Date date = c.getTime();
			search.setSearchStartDate(sf.format(date));
			search.setSearchEndDate(today);
		} else {
			if(search != null && search.getSearchStartDate() == null || "".equals(search.getSearchStartDate()) 
					&& search.getSearchEndDate() == null || "".equals(search.getSearchEndDate())) {
				c.add(Calendar.MONTH, -1);
				c.add(Calendar.DATE, +1);
				Date date = c.getTime();
				search.setSearchStartDate(sf.format(date));
				search.setSearchEndDate(today);
			} 
		}
	}
	
	/*
	 * CustomErrorController 에 의하여
	 * 406 에러와 custom error code 를 전달하여
	 * "thymeleaf/errors/error.html" 에서 에러 메세지를 보여 준다
	 */
	
	protected ResponseStatusException sendErrorPut(String msg) throws Exception {
		
		return new ResponseStatusException( HttpStatus.NOT_ACCEPTABLE, msg);
	}

	protected ResponseStatusException sendErrorPut(String msg, int code) throws Exception {
		
		return new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, msg);
	}
	
	public static Map<String, Object> generateRequestParam(String nameParam, Object... objParam) {
		
		Map<String, Object> param = new HashMap<>();
		
		addRequestParam(param, nameParam, objParam);
		
//		
//		
//		String[] names = nameParam.trim().split("\\s*,\\s*");
//		
//		for( int i = 0; i < names.length; i++ ) {
//			String name = names[i];
//			if ( paramNameSet.contains(name) ) {
//					Object obj = objParam[i];
//					if ( name.equals("keyword") ) {
//						obj = ((String)obj).trim();
//					}
//					param.put(name, obj);
//			} else {
//				throw new RuntimeException("parameter name is not registered : " + name.toString() );
//			}
//		}
		
		return param;
	}
	
	public static void addRequestParam(Map<String, Object> param, String nameParam, Object... objParam) {
		
		String[] names = nameParam.trim().split("\\s*,\\s*");
		
		for( int i = 0; i < names.length; i++ ) {
			String name = names[i];
			if ( paramNameSet.contains(name) ) {
				Object obj = objParam[i];
				if ( name.equals("keyword") ) {
					obj = obj == null ? null : ((String)obj).trim();
				}
				param.put(name, obj);
			} else {
				throw new RuntimeException("parameter name is not registered : " + name.toString() );
			}
		}
		
	}
	

}
