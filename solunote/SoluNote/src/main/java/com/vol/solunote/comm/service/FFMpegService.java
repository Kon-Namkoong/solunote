package com.vol.solunote.comm.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.vol.solunote.comm.model.Category;
import com.vol.solunote.model.dto.transcription.TranscriptionExt;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;

public interface FFMpegService {

	void printMediaInfo(String filePath) throws IOException;

	byte[] cutAudio(Category category, String path, double startSecond, double endSecond) throws Exception;

	String convertFile(String infile, String subPath) throws Exception;

	String isFilePathExists(Category category, String path) throws Exception;

	void saveAudio(String dir, String no, String text, File file, double startSecond, double endSecond)	throws Exception;

	Path cutAndSaveAudio(Category category, String path, double startSecond, double endSecond) throws Exception;

	public	void convertLocalFile(String infile, String outfile) throws Exception;

	byte[] cutAudio(Category category, String path, String startSecond, String endSecond, String channelCount, String channelId) throws Exception;

	public	String copyStereoFileAndRemove(String inpath, String stereoDir, String orgPath, String baseName) throws Exception;

	String isFilePathExists(Category category, TransVo vo) throws Exception;

	String getAudioCodec(String filePath) throws IOException;

	File getOrCopyWaveFile(Category catetory, String fileNm) throws Exception;

	String copyStereoFileAndRemove(Category category, MeetingVo vo) throws Exception;
	
	void writeWave(OutputStream outputStream, Category category, String fileNm, float start, float end, float prevEnd, float nextStart) throws Exception;

	List<Map<String, Object>> replaceWaveSpectrum(Category catetory, SoundVo soundVo, List<TranscriptionExt> list) throws Exception;

	<T extends TranscriptionVo> List<Map<String, Object>> appendWaveSpectrum(Category category, SoundVo soundVo, List<T> list) throws Exception;
}
