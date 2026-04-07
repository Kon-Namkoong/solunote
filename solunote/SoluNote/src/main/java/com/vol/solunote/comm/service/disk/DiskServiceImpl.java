package com.vol.solunote.comm.service.disk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DirPathVo;
import com.vol.solunote.repository.upload.UploadDiskRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DiskServiceImpl implements DiskService {
	
	public static final String subdirPattern = "yyyy/MM/dd";
	
	@Value("${file.upload.path.train:#{null}}")
	private String TRAIN_PATH;
	
	@Value("${file.upload.path.test:#{null}}")
	private String TEST_PATH;
	
	@Value("${file.upload.path.meet:#{null}}")
	private String MEET_PATH;
	
	@Value("${file.upload.path.temp:#{null}}")
	private String TEMP_PATH;
	
	@Value("${file.upload.path.stereo:#{null}}")
	private String STEREO_PATH;
	
	@Value("${file.upload.path.tts:#{null}}")
	private String TTS_PATH;
	
	private int trainPathCount;
	private int testPathCount;

	
	@Autowired
	UploadDiskRepository uploadDiskRepository;


	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(subdirPattern);  // thread safe from java 8
	
	@Override
	public String getUploadPath(Category category) throws Exception {
		String path = null;
		
		switch( category ) {
		case TRAIN :
			path = this.TRAIN_PATH;
			break;
		case TTS :
			path = this.TTS_PATH;
			break;
		case STEREO :
			path = this.STEREO_PATH;
			break;
		case TEST :
			path = this.TEST_PATH;
			break;
		case MEET :
			path = this.MEET_PATH;
			break;
		case TEMP :
			path = this.TEMP_PATH;
			break;
		default:
			throw new RuntimeException("unknown root path for : " + category);
		}
		
		return path;
	}
	
	@Override
	public	boolean	hasDirectoryScanChar(String fileName) 
	{
		boolean	hasDirScanChar = false;
	    // 경로조작 패턴 차단
	    if (fileName.contains("..") ||
	        fileName.contains("/") ||
	        fileName.contains("\\") ||
	        fileName.contains("%2e") ||
	        fileName.contains("%2f")) {
	    	hasDirScanChar = true;	
	    }	    		
		return	hasDirScanChar;
	}
	
	@Override
	public Path getUploadFilePath(Category category, String fileName) throws Exception {
		String path = null;

		
	    // 경로조작 패턴 차단
	    if ( hasDirectoryScanChar(fileName) )
	    {
	        throw new Exception("File Name has directory scan character");
	    }
	    
		switch( category ) {
		case TRAIN :
			path = this.TRAIN_PATH;
			break;
		case TTS :
			path = this.TTS_PATH;
			break;
		case STEREO :
			path = this.STEREO_PATH;
			break;
		case TEST :
			path = this.TEST_PATH;
			break;
		case MEET :
			path = this.MEET_PATH;
			break;
		case TEMP :
			path = this.TEMP_PATH;
			break;
		default:
			throw new Exception("unknown root path for : " + category);
		}		
		return Paths.get( path + File.separator + fileName);
	}		
	
	
	@Override
	public void copyFile(String orgNm) throws Exception {
		
		 // 1. yyyy/mmdd 의 list 생성하기
		 String today = LocalDate.now().format(formatter);
		 
		 String name = FilenameUtils.getName(orgNm);
		 
		 Path trainPath = Paths.get(TRAIN_PATH, orgNm);
		 Path testPath = Paths.get(TEST_PATH, today, name);
		 
		 if ( Files.exists(testPath) == true ) {
			 String dir =  FilenameUtils.getFullPathNoEndSeparator(testPath.toString());
			 String ext = FilenameUtils.getExtension(name);
			 String base = FilenameUtils.removeExtension(name);
			 boolean flag = false;
			 for( int no = 1; no < 1000; no++ ) {
				 String comp = base + no + "." + ext;
				 File file = new File(dir, comp);
				 if ( ! file.exists() ) {
					 testPath = Paths.get(file.toString());
					 flag = true;
					 break;
				 }
			 }
			 
			 if ( flag == false ) {
				 log.debug("test file already exists = {}", testPath);
				 return;
			 }
		 }
		 
		 Path testDir = testPath.getParent();
		 if ( Files.exists(testDir) == false ) {
			 log.debug("Files.createDirectorie {}", testDir);
			Files.createDirectories(testDir);
		 }
		 
		 log.debug("copy {} to {}", trainPath, testPath);
		 
		 Path copy = Files.copy(trainPath, testPath);
		 log.debug("result : {}", copy);
		
	}
	
	@Override
	public File getUploadedFile(Category category, String path, String channelCount, String channelId) throws Exception {
		
		Path realPath = null;
		
	    // 경로조작 패턴 차단
	    if ( hasDirectoryScanChar(path) )
	    {
	        throw new Exception("File Name has directory scan character");
	    }
		
		if ( "2".equals(channelCount) == true ) {
			realPath = Paths.get(this.STEREO_PATH, path + "_" + channelId + ".wav");
		} else {
			String root = getUploadPath(category);
			realPath = Paths.get(root, path);
		}
		
		log.debug("realPath = " + realPath.toString() + "path = " + path);
		
		return realPath.toFile();
	}

	@Override
	public Category strToCategory(String text) throws Exception {
		Category category = null;
		
		switch( text ) {
		case "train" :
			category = Category.TRAIN;
			break;
		case "tts" :
			category = Category.TTS;
			break;
		case "stereo" :
			category = Category.STEREO;
			break;
		case "test" :
			category = Category.TEST;
			break;
		case "meet" :
			category = Category.MEET;
			break;
		case "temp" :
			category = Category.TEMP;
			break;
		default:
			throw new RuntimeException("unknown text  for : " + text);
		}
		
		return category;
	}
	
	@Override
	public String copyMeetFile(String target, String fromPath) throws Exception 
	{
		
		String result = null;
		
		// 1. yyyy/mmdd 의 list 생성하기
		String today = LocalDate.now().format(formatter);
		
		String name = FilenameUtils.getName(fromPath);
		Path meetPath = Paths.get(MEET_PATH, fromPath);
		String uploadDir = target.equals("train") ? TRAIN_PATH : TEST_PATH;
		Path targetPath = Paths.get(uploadDir, today, name);
		
		if ( Files.exists(targetPath) == true ) {
			String dir =  FilenameUtils.getFullPathNoEndSeparator(targetPath.toString());
			String ext = FilenameUtils.getExtension(name);
			String base = FilenameUtils.removeExtension(name);
			boolean flag = false;
			for( int no = 1; no < 1000; no++ ) {
				String comp = base + no + "." + ext;
				File file = new File(dir, comp);
				if ( ! file.exists() ) {
					targetPath = Paths.get(file.toString());
					flag = true;
					result = today + "/" + comp;
					break;
				}
			}
			
			if ( flag == false ) {
				log.debug("target file already exists = {}", targetPath);
				throw new RuntimeException("File already exists : " + targetPath);
			}
		} else {
			result = today + "/" + name;
		}
		
		Path targetDir = targetPath.getParent();
		if ( Files.exists(targetDir) == false ) {
			log.debug("Files.createDirectorie {}", targetDir);
			Files.createDirectories(targetDir);
		}
		
		log.debug("copy {} to {}", meetPath, targetPath);
		
		Path copy = Files.copy(meetPath, targetPath);
		log.debug("result : {}", copy);
		
		return result;
		
	}
	
	
	@Override
	public	String copyTrainFile(String fromPath) throws Exception 
	{
		
		String result = null;
		
		// 1. yyyy/mmdd 의 list 생성하기
		String today = LocalDate.now().format(formatter);
		
		String name = FilenameUtils.getName(fromPath);
		Path trainPath = Paths.get(TRAIN_PATH, fromPath);
		Path targetPath = Paths.get(TEST_PATH, today, name);
		
		if ( Files.exists(targetPath) == true ) {
			String dir =  FilenameUtils.getFullPathNoEndSeparator(targetPath.toString());
			String ext = FilenameUtils.getExtension(name);
			String base = FilenameUtils.removeExtension(name);
			boolean flag = false;
			for( int no = 1; no < 1000; no++ ) {
				String comp = base + no + "." + ext;
				File file = new File(dir, comp);
				if ( ! file.exists() ) {
					targetPath = Paths.get(file.toString());
					flag = true;
					result = today + "/" + comp;
					break;
				}
			}
			
			if ( flag == false ) {
				log.debug("target file already exists = {}", targetPath);
				throw new RuntimeException("File already exists : " + targetPath);
			}
		} else {
			result = today + "/" + name;
		}
		
		Path targetDir = targetPath.getParent();
		if ( Files.exists(targetDir) == false ) {
			log.debug("Files.createDirectorie {}", targetDir);
			Files.createDirectories(targetDir);
		}
		
		log.debug("copy {} to {}", trainPath, targetPath);
		
		Path copy = Files.copy(trainPath, targetPath);
		log.debug("result : {}", copy);
		
		return result;		
	}	

	
	@Override
	public List<DirPathVo> listDirectory(String category) throws Exception {

		 String lastRead = uploadDiskRepository.getLastRead(category);
			 
		 // 1. yyyy/mmdd 의 list 생성하기
		 String today = LocalDate.now().format(formatter);
		 LocalDate eDate = LocalDate.parse(today, formatter).plusDays(1);
		 List<String> subdirs = new ArrayList<>();
		 
		 
		 String separationT = "";
		 int pathCount;
		 if(category.equals("train")) {
			 separationT = TRAIN_PATH;
			 pathCount = this.trainPathCount;
		 }else {
			 separationT = TEST_PATH;
			 pathCount = this.testPathCount;
		 }
		 
		 if ( lastRead == null ) {
			 
			 List<Path> collect = Files.list(Paths.get(separationT)).filter(Files::isDirectory).collect(Collectors.toList());
			 for( Path p : collect ) {
				 log.debug(" P = {}", p.toString());
				 List<Path> dirs = Files.list(p).filter(Files::isDirectory).collect(Collectors.toList());
				 for( Path d : dirs ) {
					 String lastDir = d.getName(pathCount).toString() + "/" +  d.getName(pathCount+1).toString();
					 LocalDate date = LocalDate.parse(lastDir, formatter);
					 if ( date.isBefore(eDate)) {
						 subdirs.add(lastDir );
						 
						 						 
					 } else {
						 log.debug("skip : {}", date);
					 }
					 
				 }
				 dirs.clear();
			 }
			 collect.clear();
		 } else {
			 Path lastPath = Paths.get(lastRead);
			 String lastDir = lastPath.getName(0).toString() + "/" +  lastPath.getName(1).toString();
			 LocalDate sDate = LocalDate.parse(lastDir, formatter);
			 
			 //List<Path> list = null;
			 for( LocalDate date = sDate; date.isBefore(eDate); date = date.plusDays(1)) {
				subdirs.add(date.format(formatter) );
			 }
		 }
		 
		 Collections.sort(subdirs);
		 
		 List<DirPathVo> files = new ArrayList<>();
		 
		 // 2. yyyy/mmdd 안의 모든 파일 읽기
		 for( String dir : subdirs ) {
			 
			 Path path = Paths.get(separationT + "/" + dir);
			 
			 
			 if ( Files.exists(path) && Files.isDirectory(path)) {
				 List<Path> col = Files.list(path).filter(Files::isRegularFile).filter(f -> f.toString().toLowerCase().endsWith(".wav") 
							 ||  f.toString().toLowerCase().endsWith(".mp3")
//							 ||  f.toString().toLowerCase().endsWith(".json")
						 ).collect(Collectors.toList());
				 
				 DirPathVo vo = new DirPathVo();
				 vo.setDir(dir);
				 vo.setFiles(col);
				 
				 if ( col.size() > 0 ) {
					 files.add(vo);
				 }
				 col.clear();
			 }
			 
		 }
		 
		return files;
	}
	
}


	


	

	


