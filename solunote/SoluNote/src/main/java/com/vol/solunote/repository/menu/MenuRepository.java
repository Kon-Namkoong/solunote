package com.vol.solunote.repository.menu;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.vol.solunote.model.vo.menu.MenuListVo;

public interface MenuRepository {
	public 	int updateMenu(@Param("userInfo") Map<String,String> userInfo, @Param("menuList") List<Map<String,Object>> menuList) throws Exception;
	public 	int updateAuthBase(@Param("menuInfo") Map<String,String> menuInfo, @Param("menuList") List<Map<String,Object>> menuList) throws Exception;
	public 	int deleteAuthBase(String seq) throws Exception;
	public 	String[] findTopMenu() throws Exception;
	public 	int deleteMenu(int seq) throws Exception;
	public 	List<Map<String,Object>> findBaseAuthList() throws Exception;
	public 	List<Map<String, Object>> readMenuAll() throws Exception;	
	public	List<MenuListVo> getAuthList() throws Exception;
}
