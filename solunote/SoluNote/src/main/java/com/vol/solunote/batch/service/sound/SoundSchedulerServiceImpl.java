package com.vol.solunote.batch.service.sound;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DirPathVo;
import com.vol.solunote.repository.meeting.MeetingRepository;
import com.vol.solunote.repository.sound.SoundRepository;
import com.vol.solunote.repository.test.TestRepository;
import com.vol.solunote.repository.transcription.TranscriptionRepository;
import com.vol.solunote.repository.upload.UploadDiskRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SoundSchedulerServiceImpl implements SoundSchedulerService {
	@Autowired
	private SoundRepository soundRepository;

	
	@Autowired
	private TestRepository testRepository;

	@Autowired
	private TranscriptionRepository transcriptionRepository;
	
	@Autowired
	private UploadDiskRepository uploadDiskRepository;
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private CommonSteelServiceImpl commonService;
	

	@Override
	public void setDeletedAt(int seq, String fileNm, String fileConvNm) throws Exception {
		
		boolean flag = deleteWaveFile(seq, fileNm, fileConvNm);
		
		if (flag == true ) {
			soundRepository.updateDeletedAtBySeq(seq);
		}
	}	
	
	@Override
	public boolean deleteWaveFile(int seq, String fileNm, String fileConvNm) throws Exception 
	{
		
		transcriptionRepository.deleteTrans(seq);
		
		
		if ( fileNm != null ) {
			commonService.removeDiskFile(Category.TRAIN, fileNm);
		}
		
		if ( fileConvNm != null ) {
			commonService.removeDiskFile(Category.TRAIN, fileConvNm);
		}
		
		return true;
	}	
	
	@Override
	public void removeWavFile(Map<String, Object> map) throws Exception {
		log.debug("started removeMeetingFile");
		
		// 1. config value 에러 체크
		
		String configValue = (String) map.get("configValue");
		String useYn = (String) map.get("useYn");
		
		if ( configValue == null || useYn == null  ) {
			log.error("Error : SOUND_DELETE_TERM any value is null, configValue = {}, useYn = {}", configValue, useYn);
			return;
		}
		
		if ( ! "Y".equals(useYn) && !"y".equals(useYn) ) {
			log.error("Error : SOUND_DELETE_TERM useYn is wrong (Y/y), useYn = {}", useYn);
			return;
		}
		
		int term = Integer.parseInt(configValue);
		if ( term < 1  ) {
			log.error("Error : SOUND_DELETE_TERM configValue wrong < 1, configValue = {}", configValue);
			return;
		}
		
		// 2. 
		
		log.debug("remove process : term = {}", term);
		
		List<Map<String, Object>> list = soundRepository.getMeettingRemoveCandiate(term);
		
		for( Map<String, Object> meetMap : list ) {
			int seq = (int) meetMap.get("seq");
			String name = (String) meetMap.get("file_org_nm");
			String createdAt = (String) meetMap.get("created_at");
			String file = (String) meetMap.get("file_new_nm");
			String orig = (String) meetMap.get("file_conv_nm");
			
			log.debug("removing seq = {}, createdAt = {}, name = {}, file={}, orig={}", seq, createdAt, name, file, orig);
				
			
			setDeletedAt(seq, (String) meetMap.get("file_new_nm"),  (String) meetMap.get("file_conv_nm"));
		}		
		log.debug("end removeMeetingFile");
	}
	
	@Override
	public void readDirectory(String category) throws Exception, IOException {
		
		List<DirPathVo> vos = diskService.listDirectory(category);
		
		
		 for( DirPathVo vo : vos ) {
			 
			 String dir = vo.getDir();
			 
			 List<Path> files = vo.getFiles();
			 
			 for( Path p : files ) {
				 
				 String fullname = p.toString();
				 String fileName = p.getFileName().toString();
				 String division = fileName.substring(0, 2);
				 
				 long size = p.toFile().length();
				 
				 String orgnm = dir + "/" + fileName;
				 
				 log.debug("p : {} ", p.toString());
				 
				 Map<String, Object> param = new HashMap <>();
				 param.put("subject", "subject");
				 param.put("time_duration_ms", "0");
				 param.put("tc_user_seq", 0);
				 
				 
				 param.put("category", category);
				 param.put("orgnm", orgnm);
				 param.put("dir", orgnm);
				 param.put("newnm", fullname);
				 param.put("division", division);
				 param.put("fileSizeBytes", size);
				 
				 if(category.equals("train")) {
					 soundRepository.insertSoundAndRead(param);
				 }else {
					 testRepository.insertTestAndRead(param);
				 }
				 uploadDiskRepository.createLastRead(param);
			 }
		 }
	}
	
	@Override
	public	List<Map<String, Object>>	getSettingConfigList()	throws Exception
	{
		return	meetingRepository.getSettingConfigList();
	}
}
