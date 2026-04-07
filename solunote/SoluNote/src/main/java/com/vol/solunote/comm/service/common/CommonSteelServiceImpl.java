package com.vol.solunote.comm.service.common;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.tika.Tika;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.comm.service.disk.DiskServiceImpl;
import com.vol.solunote.comm.service.ffmpec.FFMpegService;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.model.type.Category;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("commonService")
public class CommonSteelServiceImpl extends CommonServiceImpl {
	

	@Value("${train.project_id}")
	private String PROJECT_ID; 
	
	@Autowired
	FFMpegService	ffmpegService;
	
	@Autowired
	private DiskService diskService;
	
	@Override
	public Map<String, Object> saveUploadFileConvert(Category category, MultipartFile file) throws Exception {
		
		Tika tika = new Tika();
		String type = tika.detect(file.getInputStream());
		
		if ( !type.startsWith("audio/"))
		{
			log.error("지원하지 않는 파일입니다");
			return	null;			
		}
		String originalfileName = file.getOriginalFilename();
		String extension = FilenameUtils.getExtension(originalfileName);
		String saveFileName = UUID.randomUUID().toString() + "." + extension;
		
		//		String uploadFileName = commonService.saveUploadFileConvert(file);
		String subdir = DateUtil.getFormatString(DiskServiceImpl.subdirPattern);
		
		String uploadPath = diskService.getUploadPath(category);
		Path path = Paths.get(uploadPath + File.separator  + subdir);
		if ( Files.exists(path) == false ) {
			Files.createDirectories(path);
		}

		
		// 1. save file AS-IS
		Path savePath = Paths.get(path.toString(), saveFileName);
		file.transferTo(savePath);
		String uploadFileName = subdir +  "/"  + saveFileName;   // db 에 저장하는 path 이므로 File.separator 대신에 "/" 를 사용함
		
		long size = 0;
		Resource resource = null;
		// wav, mp3 가 아니면, ffmpeg 으로 convert 함
		String convnm = null;
		boolean convFlag = false;
		
		if ( extension.equalsIgnoreCase("wav") ) {
			String codec = ffmpegService.getAudioCodec(savePath.toAbsolutePath().toString());
			if ( "pcm_s16le".equals(codec) == false ) {
				convFlag = true;
			}
		} else {
			convFlag = true;
		}
		
		if ( convFlag == true ) {
			convnm = uploadFileName;
			// 2. save converted file
			uploadFileName = convertUploadFile(category, uploadFileName);
			Path convPath = Paths.get(uploadPath, uploadFileName);
			resource = new FileSystemResource(convPath);
			size = convPath.toFile().length();
			log.debug("CONV : yes");
		}
		else {
//			resource = file.getResource();
			resource = new FileSystemResource(savePath);
			size = file.getSize();
			log.debug("CONV : no");
		}
		
		int fileExtensionIdx = FilenameUtils.indexOfExtension(originalfileName);
		String subject = fileExtensionIdx == -1 ? originalfileName : originalfileName.substring(0, fileExtensionIdx);

		Map<String, Object> param = new HashMap <>();
		param.put("subject", subject);
		// timeDurationStr 은 callStt()  이후 set 해야 함
		//		param.put("timeDurationStr", durationMs);
		param.put("orgnm", originalfileName);
		param.put("newnm", uploadFileName);
		param.put("convnm", convnm);
		param.put("fileSizeBytes", size);
		// tcUserSeq 은 별도로 set 해야 함
		//		param.put("tcUserSeq", tcUserSeq);
		
		param.put("resource", resource);
		
		return param;
	}
	
//	@Override
	private String convertUploadFile(Category category, String subAndName) throws Exception {
		
//		String subdir = DateUtil.getFormatString(DiskServiceImpl.subdirPattern);
//		Path path = Paths.get(UPLOAD_PATH + File.separator  + subdir);
//		if ( Files.exists(path) == false ) {
//			Files.createDirectories(path);
//		}
		
		String uploadPath = diskService.getUploadPath(category);
		
		Path inPath = Paths.get(uploadPath + File.separator  + subAndName);
		String subPath = FilenameUtils.getFullPath(subAndName);
		String convBasename = UUID.randomUUID().toString();
		String outPath = uploadPath + File.separator  + subPath + convBasename;

		
		log.debug("ffmpegService.convertFile( {})", inPath.toString());
		
		String convExt = ffmpegService.convertFile(inPath.toString(), outPath);
		
		return subPath + convBasename + "." + convExt;
	}
	
	
}
