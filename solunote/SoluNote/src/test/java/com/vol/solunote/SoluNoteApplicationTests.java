package com.vol.solunote;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.vol.solunote.repository.siteuser.SiteUserRepository;

import java.util.HashMap;

@Slf4j
@SpringBootTest
class SoluNoteApplicationTests {

    @Autowired
    SiteUserRepository siteUserRepository;
    
	@Autowired
	PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Rollback(false)
    void testJpa(){
    	
    	Map<String,String> paramMap = new HashMap<>();
    	
    	paramMap.put("tcId", "root");
    	paramMap.put("tcName","관리자");
    	paramMap.put("tcPw", passwordEncoder.encode("solugate1234!"));
    	paramMap.put("tcEmail","nkkon@naver.com");
    	paramMap.put("tcPhone","010-2713-2543");
    	paramMap.put("tcLevel", "100");
    	try
    	{
    		siteUserRepository.insertUser(paramMap);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}   	
    }
}

