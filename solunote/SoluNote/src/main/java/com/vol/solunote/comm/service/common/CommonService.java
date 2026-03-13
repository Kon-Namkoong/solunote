package com.vol.solunote.comm.service.common;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DomainVo;
import com.vol.solunote.model.vo.comm.ErrorShelf;
import com.vol.solunote.model.vo.transcription.TransVo;

public interface CommonService {
	
	List<DomainVo> selectDomainList() throws Exception ;

	Long selectRatio() throws Exception ;

	List<String> selectServerList() throws Exception;

	List<DomainVo> selectSchedulerDomainList() throws Exception ;

	<T,S> S callRestTemplate(HttpHeaders httpHeaders, HttpMethod method, String url, T body) throws Exception;
	
//	Map<String, Object> restPostData(String url, File file, String name, String text, double start, double end) throws Exception;

	<T> T restPostFile(String url, String name, String text, double start, double end, Category category) throws Exception;
	<T> T restPutFile(String url, TransVo vo, String text, Category category) throws Exception;

	<T> T restPutData(String url, String dataId, String trainText, Category category, String useYn)  throws Exception;

	<T> T restPostData(String url, Map<String, Object> body) throws Exception;

	<T> T restGetData(String url) ;

	ErrorShelf getErrorShelf();

	void removeErrorShelf();

	String saveUploadFile(MultipartFile file, String saveFileName) throws IOException;

//	String convertUploadFilec(String subAndName, String extension) throws Exception;

	Map<String, Object> saveUploadFileConvert(Category category, MultipartFile file) throws Exception;

	void removeDiskFile(Category category, String subPath) throws Exception;

	Path getUploadPath(String fileName) throws Exception;

	<T,S> S callRestTemplateGen(HttpHeaders httpHeaders, HttpMethod method, String url, T body) throws Exception;

	<T> T restPostStereoFile(String url, TransVo vo, String text, Category category) throws Exception;

//	String convertUploadFile(String subAndName) throws Exception;
	
}
