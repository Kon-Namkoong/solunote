package com.vol.solunote.repository.tts;

import java.util.List;
import java.util.Map;


public interface TtsRepository {
	public	List<Map<String, Object>> getList(Map<String, Object> param) throws Exception;	
	public	List<Map<String, Object>> getTextList(Map<String, Object> param) throws Exception;
	public	List<Map<String, Object>> searchList(Map<String, Object> param) throws Exception;
	public	void updateTtsList(Integer seq, String type);
	public	void 	updateKeywordDate(Map<String, Object> params);	
	public	void 	clickLeastOnce(Integer seq);	
	public	void	saveAudio(Map<String, Object> result) throws Exception;
	public	void 	crateTitle(Map<String, Object> params) throws Exception; 
	public	void 	updateAudio(Map<String, Object> params) throws Exception;
	public	Map<String, Object> getAudioInfo(Map<String, Object> params) throws Exception;
	public	void updateTts(Map<String, Object> map) throws Exception;
}
