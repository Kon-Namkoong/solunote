package com.vol.solunote.menu28.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;

public interface Menu28Service  {

	List<TransVo> getTtsTransListBatch(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller) throws Exception;
	
	public List<Map<String, Object>> getList(Map<String, Object> param) throws Exception;
		
	List<Map<String, Object>> getExcelList(MultipartFile file) throws Exception;
	
	void saveAudio(Map<String, Object> resultAudio) throws Exception;
	
	Path getUploadPath(String fileName) throws Exception;
	
	int crateTitle(String title, int tcUserSeq) throws Exception;
	
	void updateAudio(int titleSeq, int seq) throws Exception;
	
	void excelForm(HttpServletResponse response) throws IOException;
	
	void callStt(int titleSeq,int seq) throws Exception;

	List<Map<String, Object>> getTextList(Map<String, Object> param) throws Exception;

	void clickLeastOnce(Integer seq) throws IOException;
	
	public List<Map<String, Object>> searchList(Map<String, Object> param) throws Exception;

	public List<Map<String, Object>> getTransList(Map<String, Object>param) throws Exception;
	
	void updateTtsList(Integer seq, String type) throws Exception;
		
	public void trash(List<Map<String, Object>> list,String type) throws Exception;

	List<TransVo> getTtsTransListBatchTTS(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller)
			throws Exception;
	
	
}
