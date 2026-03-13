package com.vol.solunote.common.service;

import java.util.Map;

import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;

public interface CommonDataService {
	
	public 	void copySoundAndTrans(int seq, Map<String, Object> meetMap) throws Exception;
	public	void trainCopyTestAndTestTrans(int seq, SoundVo trainMap) throws Exception;	
	public	void copyTestAndTestTrans(int seq, Map<String, Object> meetMap) throws Exception;	
	public 	void postErrorTxn(TransVo vo, Category category, int seq, boolean reset, String errorMsg) throws Exception;	
	public 	void postData( TransVo vo, Category category, boolean reset, boolean fakeFlag) throws Exception;
}

