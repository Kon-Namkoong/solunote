package com.vol.solunote.model.vo.comm;

import java.nio.file.Path;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirPathVo {
	private String dir;
	private List<Path> files;

}
