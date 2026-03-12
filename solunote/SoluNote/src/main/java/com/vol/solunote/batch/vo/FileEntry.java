package com.vol.solunote.batch.vo;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileEntry {

	private File file;
//	private String desc;
//	private String basename;
//	private String path;
	private String ext;
	private String name;

	public FileEntry(String path) {
		
		this.file = Paths.get(path).toFile();
//		this.basename = FilenameUtils.getBaseName(path);
		this.name = FilenameUtils.getName(path);
//		this.path = path;
		
//		String ext = FilenameUtils.getExtension(path);
//		String desc = "";
//		switch( ext ) {
//		case "wav" : desc = "type=audio/wav";   break;
//		case "mp3" : desc = "type=audio/mp3";   break;
//		}
//		
//		this.desc = desc;

	}

}
