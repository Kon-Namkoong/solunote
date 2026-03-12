package com.vol.solunote.repository.menu;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.mapper.menu.MenuMapper;
import com.vol.solunote.model.vo.menu.MenuListVo;
import org.springframework.stereotype.Repository;
@Repository
public class MenuRepositoryImpl implements MenuRepository {

	@Autowired
	private	MenuMapper	mapper;
	

	@Override
	public 	int updateMenu(@Param("userInfo") Map<String,String> userInfo, @Param("menuList") List<Map<String,Object>> menuList) throws Exception
	{
		return	mapper.updateMenu(userInfo, menuList);
	}
	@Override
	public 	int updateAuthBase(@Param("menuInfo") Map<String,String> menuInfo, @Param("menuList") List<Map<String,Object>> menuList) throws Exception
	{
		return	mapper.updateAuthBase(menuInfo, menuList);
	}
	@Override
	public 	int deleteAuthBase(String seq) throws Exception
	{
		return	mapper.deleteAuthBase(seq);
	}
	@Override
	public 	String[] findTopMenu() throws Exception
	{
		return	mapper.findTopMenu();
	}
	
	@Override
	public 	int deleteMenu(int seq) throws Exception
	{
		return	mapper.deleteMenu(seq);
	}
	
	@Override
	public 	List<Map<String,Object>> findBaseAuthList() throws Exception
	{
		return	mapper.findBaseAuthList();
	}
	
	@Override
	public 	List<Map<String, Object>> readMenuAll() throws Exception
	{
		return	mapper.readMenuAll();
	}
	
	@Override
	public	List<MenuListVo> getAuthList() throws Exception
	{
		return	mapper.getAuthList();
	}

}
