package com.vol.solunote.common.service;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vol.solunote.Exception.FFMpegCallException;
import com.vol.solunote.Exception.TrainCallException;
import com.vol.solunote.comm.Util;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.comm.service.ffmpec.FFMpegService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.repository.test.TestRepository;
import com.vol.solunote.repository.transcription.TranscriptionRepository;

import lombok.extern.slf4j.Slf4j;

import com.vol.solunote.repository.sound.SoundRepository;

@Service
@Slf4j
public class CommonDataServiceImpl implements CommonDataService{
	
	@Value("${train.url}")
	private String trainUrl; 
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	private	TranscriptionRepository	transcriptionRepository;
	
	@Autowired
	private	SoundRepository		soundRepository;
	
	@Autowired
	private	TestRepository 	testRepository;
	
	@Autowired
	private FFMpegService	ffmpegService;
	
	@Autowired
	private CommonSteelServiceImpl	commonService;
	
	
	@Override
	public void copySoundAndTrans(int seq, Map<String, Object> meetMap) throws Exception {
		
		Map<String, Object> param = new HashMap<>();
		int userSeq = Util.getSessionUserSeq();
		param.put("userSeq", userSeq);
		param.put("seq", seq);
		
		String dirPath = diskService.copyMeetFile("train", (String)meetMap.get("file_new_nm"));
		param.put("fileNewNm", dirPath);
		
		if ( meetMap.get("file_conv_nm") != null ) {
			dirPath = diskService.copyMeetFile("train", (String)meetMap.get("file_conv_nm"));
			param.put("fileConvNm", dirPath);
		}
		
		soundRepository.copySound(param);
		
		int soundSeq = ((Number)param.get("id")).intValue();
		param.put("soundSeq", soundSeq);
		
		transcriptionRepository.copyTrans(param);
	}
	
	@Override
	@Transactional
	public void trainCopyTestAndTestTrans(int seq, SoundVo trainMap) throws Exception {
		
		Map<String, Object> param = new HashMap<>();
		int userSeq = Util.getSessionUserSeq();
		param.put("userSeq", userSeq);
		param.put("seq", seq);
		
		String dirPath = diskService.copyTrainFile(trainMap.getFileNewNm());
		param.put("fileNewNm", dirPath);
		
		if ( trainMap.getFileConvNm() != null ) {
			dirPath = diskService.copyTrainFile(trainMap.getFileConvNm());
			param.put("fileConvNm", dirPath);
		}
		
		testRepository.trainCopyTest(param);
		
		int testSeq = ((Number)param.get("id")).intValue();
		param.put("testSeq", testSeq);
		
		testRepository.trainCopyTestTrans(param);
	}
	
	@Override
	@Transactional
	public void copyTestAndTestTrans(int seq, Map<String, Object> meetMap) throws Exception {
		
		Map<String, Object> param = new HashMap<>();
		int userSeq = Util.getSessionUserSeq();
		param.put("userSeq", userSeq);
		param.put("seq", seq);
		
		String dirPath = diskService.copyMeetFile("test", (String)meetMap.get("file_new_nm"));
		param.put("fileNewNm", dirPath);
		
		if ( meetMap.get("file_conv_nm") != null ) {
			dirPath = diskService.copyMeetFile("test", (String)meetMap.get("file_conv_nm"));
			param.put("fileConvNm", dirPath);
		}
		
		testRepository.copyTest(param);
		
		int testSeq = ((Number)param.get("id")).intValue();
		param.put("testSeq", testSeq);
		
		testRepository.copyTestTrans(param);
	}
	

	private void postDataTxn(TransVo vo, Category category, int seq, String dataId, boolean reset, String errorMsg) throws Exception {
		
		switch( category ) {
		case TEST :
			testRepository.updateTestDataId(seq, dataId, errorMsg, vo.getUseYn());
			testRepository.insertTestTransPair(dataId, seq, vo.getTrainText(), vo.getPrUseYn(), vo.getPrStart(), vo.getPrEnd());
			break;
		case TRAIN :
		case TTS :
			transcriptionRepository.updateDataId(seq, dataId, reset, errorMsg, vo.getUseYn());
			transcriptionRepository.insertTransPair(dataId, seq, vo.getTrainText(), vo.getPrUseYn(), vo.getPrStart(), vo.getPrEnd());
			break;
		default:
			throw new RuntimeException("unknown category for : " + category);
		}
		
	}
	
	@Transactional
	@Override
	public void postErrorTxn(TransVo vo, Category category, int seq, boolean reset, String errorMsg) throws Exception {
		
		switch( category ) {
		case TEST :
			testRepository.updateTestDataId(seq, null, errorMsg, "N");
			break;
		case TRAIN :
		case TTS :
			transcriptionRepository.updateDataId(seq, null, reset, errorMsg, "N");
			break;
		default:
			throw new RuntimeException("unknown category for : " + category);
		}

	}
	
	private Map<String, Object> postWaveFile(TransVo vo, Category category, String text, String url) throws Exception {
		
		Map<String, Object> resultMap;
		String errorMsg = ffmpegService.isFilePathExists(category, vo);
		
		if ( errorMsg == null ) {
			resultMap = commonService.restPostStereoFile(url, vo, text, category);
		} else {
			FFMpegCallException fce = new FFMpegCallException(vo.getFileNewNm(), errorMsg);
			throw fce;
		}
			
		return resultMap;
	}
	
	private	 Map<String, Object> putWaveFile(TransVo vo, Category category, String text, String url) throws Exception {
		
		Map<String, Object> resultMap;
		String errorMsg = ffmpegService.isFilePathExists(category, vo.getFileNewNm());
		
		if ( errorMsg == null ) {
				resultMap = commonService.restPutFile(url, vo, text, category);
		} else {
			FFMpegCallException fce = new FFMpegCallException(vo.getFileNewNm(), errorMsg);
			throw fce;
		}
		
		return resultMap;
	}
	
	@Override
	public void postData( TransVo vo, Category category, boolean reset, boolean fakeFlag) throws Exception {
		
		int seq = vo.getSeq();
		String errorMsg = null;

		
		String text = vo.getTrainText().trim();
		
		String url = this.trainUrl + "/data/";
		Map<String, Object> resultMap = null;
		
		try {
			
			if ( vo.getDataId() != null && vo.getDataId().length() > 0 ) {
				// 1. 동일한 data id 가 있으면 :  put request 
				// 다음에 생성하므로 무조건 useYn = False 로 전달함
				
					if (  vo.getPrDataId() != null && ( vo.getStart() != vo.getPrStart() || vo.getEnd() != vo.getPrEnd() ) ) {
						// 첨부파일을 첨부하여 수정함
	//				resultMap = commonService.restPutData(url, vo.getDataId(), text, testFlag, vo.getUseYn());
						resultMap = putWaveFile(vo, category, text, url);
					} else {
						// 첨부파일 없이 변경함
						resultMap = commonService.restPutData(url, vo.getDataId(), text, category, vo.getUseYn());
					}
				
				log.debug("resutlmap : ", resultMap);
			}  else {
				// 2. 음성 파일 등록   : post 
				// POST 는 use_yn = 'N' 은 하지 않고, 'Y'만 한다.
				if ( vo.getUseYn().equals("N")) {
					log.debug("use_yn = N 은 POST 하지 않음 : testFlag = {}, seq = {}", category, vo.getSeq());
					return;
				}
				
				resultMap = postWaveFile(vo, category, text, url);
								
			}
		
		} catch ( TrainCallException tce ) {
			String status = tce.getStatus();
			
			if ( "404".equals(status) ) {
				log.error("STT SEND ERROR - continue-1 : 404 not found");
				return;   // ex-1. continue next time

			} else if ( "400".equals(status) == true ) {
				log.error("STT SEND ERROR - continue-2 : 400 bad request");
				if (  tce.canTrainRepeatable() ) {
					return;   // ex-2. continue next time
				}
			} 
			// ex-3. error 에세지 쓰고 not useable 로 처리
			log.error("STT SEND ERROR - halt-1 : TrainCallException");
			postErrorTxn(vo, category, seq, reset, tce.getDetail());
			return;
			
		} catch ( FFMpegCallException fce ) {
			log.error("STT SEND ERROR - halt-2 : FFMpegCallException");
			postErrorTxn(vo, category, seq, reset, fce.getMessage());
			return;
		} catch ( Exception e ) {
			e.printStackTrace();
			log.error("STT SEND ERROR - continue-3 : Exception");
			return;
		} 
		
		if ( resultMap == null ) {
			return;
		}
		
		String dataId = (String) resultMap.get("id");
		
		// txn 은 여기서 호출한다. (self 로 호출해야 txn 이 작동한다)
		postDataTxn(vo, category, seq, dataId, reset, errorMsg);
	}
}
