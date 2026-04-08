package com.vol.solunote.domain.testdata.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.util.CommonUtil;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.repository.sound.SoundRepository;
import com.vol.solunote.repository.test.TestRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestdataServiceImpl implements TestdataService {
	
	@Autowired
	private TestRepository testRepository;

	@Autowired
	private SoundRepository soundRepository;
	
	
	@Override
	public List<SoundVo> getList(Map<String, Object> param) throws Exception {
		
		List<SoundVo> list = testRepository.getList(param);
		for( SoundVo vo : list ) {
			
			vo.setTimeDurationFormatted(  DurationFormatUtils.formatDuration(Double.valueOf(vo.getTimeDurationMs()).longValue(), "HH:mm:ss") );
			
			String value = vo.getTimeDurationMs();
			String convDuration = CommonUtil.convDuration(value);
			String timeDurationFormatted = vo.getTimeDurationFormatted();
			log.debug("{}", convDuration);
			log.debug("{}\n", timeDurationFormatted);
		}
		return list;
	}

	
	@Override
	public List<Map<String, Object>> getFailList(DefaultVo search, OffsetPageable offsetPageable,String keyword,String division) throws Exception {
		
		List<Map<String, Object>> list = testRepository.getFailList(search, offsetPageable,keyword,division);

		return list;
	}	
	
	@Override
	public List<Map<String, Object>> getTestTransList(int seq, OffsetPageable offsetPageable,int reliability) {
		
		if (reliability >100) {
			reliability = 100;
		}
				
		
		List<Map<String, Object>> list = testRepository.getTestTransList(seq, offsetPageable,reliability);
		return list;
	}
	
	@Override
	public List<TransVo> getTestTransListX(Map<String, Object> param) throws Exception {
		
		List<TransVo> list = testRepository.getTestTransListX(param);
		
		for( TransVo vo : list ) {
			if ( "Y".equals(vo.getCandidate()) && "N".equals(vo.getUseYn()) && StringUtils.isEmpty(vo.getDataId()) ) {
				vo.setCandidate("N");
			}
		}
		
		return list;
	}
	
	@Override
	public List<TransVo> getTestTransListBatch(String useYn, OffsetPageable offsetPageable, DefaultVo search) throws Exception {	
		List<TransVo> list = testRepository.getTestTransListBatch(useYn, offsetPageable, search);		
		return list;		
	}
		
	@Override
	@Transactional
	public void excludeTestCandiate(int[] seq, String value) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put("seq", seq);
		map.put("value", value);
		
		testRepository.excludeTestCandiate(map);
		
		testRepository.excludeTestTransPair(map);
		
		testRepository.updateTestUseYesFromTrans();
		testRepository.updateTestUseNoFromTrans();
		
	}
	
	@Override
	public List<SoundVo> getListData(Map<String, Object> param) throws Exception {

		List<SoundVo> list = testRepository.getListData(param);
		for (SoundVo vo : list) {
			vo.setTimeDurationFormatted(DurationFormatUtils.formatDuration(Double.valueOf(vo.getTimeDurationMs()).longValue(), "H:mm:ss"));
		}
		return list;
	}

	@Override
	@Transactional
	public void excludeTest(int seq) throws Exception {
		
		testRepository.excludeTest(seq);
		testRepository.excludeTestTrans(seq);
		
		List<Integer> list = testRepository.getDataIdFromTestTrans(seq);
		
		if ( list.size() > 0 ) {
			Map<String, Object> map = new HashMap<>();
			map.put("seq", list);
			testRepository.excludeTestTransPair(map);
		}
	}

	
	@Override
	@Transactional
	public void updateTrainTextBySeq(int seq, String trainText) throws Exception {
		
		if ( "NULL".equals(trainText) ) {
			trainText = null;
		}
		testRepository.updateTrainTextBySeq( seq, trainText);
		
		Map<String, Object> map = new HashMap<>();
		map.put("seq", new Integer[] { seq });
		
		testRepository.excludeTestTransPair(map);
		
		testRepository.updateTestUseYesFromTrans();
		testRepository.updateTestUseNoFromTrans();
		
	}
	
	@Override
	@Transactional
	public String successSoundAndStt(Map<String, Object> param, Map<String, Object> resultMap) throws Exception {

		testRepository.createTest(param);

		int testSeq = ((Number)param.get("id")).intValue();
		
		long sum = 0L;
		int count = 0;
		int confidence= 0;
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listSttResult = (List <Map<String, Object>>) resultMap.get("stt_result");
		
		if(listSttResult != null)
		{
			for (Map <String, Object> stt_result : listSttResult) {

				String confidenceStr = String.valueOf(stt_result.getOrDefault("confidence", "0"));
				confidence = (int) (Double.parseDouble(confidenceStr) * 100);
				sum += confidence;
				count++;
			
				testRepository.createTestTrans(
						String.valueOf(stt_result.get("start")),
			            String.valueOf(stt_result.get("end")),
			            String.valueOf(stt_result.getOrDefault("text", "")).trim(),
			            confidence,
			            testSeq);
			}
		}
		
		if ( count > 0 ) {
			int avg = (int) (sum / count);
			Map<String, Object> map = new HashMap<>();
			map.put("seq", testSeq);
			map.put("reliability", avg);
			
			testRepository.updateTest(map);
		}

		return Integer.toString(testSeq);
	}

	@Override
	public int updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception
	{
		return	soundRepository.updateRemarkAndUpdatedAtBySeq(updateRemark, updatedAt, meetSeq);
	}

	@Override
	@Transactional
	public void updateTestDataId(int seq, String dataId, String errorMsg, String useYn) throws Exception {
		
		testRepository.updateTestDataId( seq, dataId, errorMsg, useYn);
		
	}

	@Override
	public void insertTestTransPair(String dataId, int seq, TransVo vo) throws Exception {		
		testRepository.insertTestTransPair( dataId, seq, vo.getTrainText(), vo.getUseYn(), vo.getStart(), vo.getEnd());		
	}

	
	@Override
	public void clickLeastOnce(Integer seq) throws IOException {
		testRepository.clickLeastOnce(seq);
		
	}
	
	@Override
	@Transactional
	public int split(Map<String, String> map) throws Exception {
		
		
		int seq = Integer.parseInt( map.get("seq"));
		String start2 = map.get("start2");
		String text1 = map.get("text1");
		String soundSeq =  map.get("soundSeq");
		
		List<Map<String, Object>> list = getTranscription(seq);
		if ( list.size() != 1 ) {
			throw new RuntimeException("data not found for seq = " + seq);
		}
		
		map.put("end", start2);
		map.put("sttText", text1);
				
		int id = createTranscriptionGen(map.get("start2"),
				String.valueOf(list.get(0).get("end")),
				map.get("text2").trim(),
				String.valueOf(list.get(0).get("reliability")),
				Integer.parseInt(soundSeq));
		
		return id;
	}

	@Override
	@Transactional
	public int combine(Map<String, String> map) throws Exception {
		
		
		int seq = Integer.parseInt( map.get("seq"));
		int seq2 = Integer.parseInt( map.get("seq2"));
		String text = map.get("text").trim();
		
		List<Map<String, Object>> list = getTranscription(seq);
		if ( list.size() != 1 ) {
			throw new RuntimeException("data not found for seq = " + seq);
		}
		
		map.put("sttText", text);
		
		int updateCount = updateTranscriptionForSplit(map);
		
		testRepository.updateTranscriptionForCombine(seq2);
		
		return updateCount;
	}		
	
	

	
	public List<Map<String, Object>> getTranscription(int seq) throws Exception {
		
		List<Map<String, Object>> list = testRepository.getTranscription(seq);
		
		return list;
	}	
	
	public int createTranscriptionGen(String start, String end, String text, String confidence, int soundSeq) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		map.put("text", text);
		map.put("reliability", Integer.parseInt(confidence));
		map.put("soundSeq", soundSeq);
	
		map.put("seq", -1);     // useGeneratedKeys 가 return 하는 type 을 integer 로 하기 위하여, 미리 map 에 seq 를 set 해 놓음
		testRepository.createTranscriptionGen(map);
		
		int seq =  (int) map.get("seq");
		
		return seq;
	}	
	
	
	public int updateTranscriptionForSplit(Map<String, String> map) throws Exception {
		
		return testRepository.updateTranscriptionForSplit(map);
	}	
		
	
	@Override
	@Transactional
	public int resetFrame(Map<String, String> map) throws Exception {
		
		int count = testRepository.updateTranscriptionForReset(map);
		
		return count;
	}


	@Override
	public List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception {
		
		if ( (int)param.get("reliability") > 100) {
			param.put("reliability", "100");
		}
				
		List<TranscriptionVo> list = testRepository.getTranscriptionList(param);
		
		for( TranscriptionVo tr : list ) {
			if ( tr.getTrainText() == null ) {
				if ( tr.getMeetText() != null ) {
					tr.setTrainText(tr.getMeetText());
				} else {
					tr.setTrainText("");
				}
			}
		}	
		return list;
	}	
}
