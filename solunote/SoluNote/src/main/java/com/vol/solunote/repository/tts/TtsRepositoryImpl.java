package com.vol.solunote.repository.tts;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.vol.solunote.mapper.tts.TtsMapper;
import org.springframework.stereotype.Repository;
@Repository
public class TtsRepositoryImpl implements TtsRepository {

	@Autowired
	private TtsMapper	mapper;
	
	@Override
	public List<Map<String, Object>> getList(Map<String, Object> param) throws Exception {
		return mapper.getList(param);
	}

	@Override
	public List<Map<String, Object>> getTextList(Map<String, Object> param) throws Exception {
		return mapper.getTextList(param);
	}

	@Override
	public List<Map<String, Object>> searchList(Map<String, Object> param) throws Exception {
		return mapper.searchList(param);
	}

	@Override
	public void updateTtsList(Integer seq, String type) {
		mapper.updateTtsList(seq, type);
	}

	@Override
	public void updateKeywordDate(Map<String, Object> params) {
		mapper.updateKeywordDate(params);
	}

	@Override
	public void clickLeastOnce(Integer seq) {
		mapper.clickLeastOnce(seq);
	}

	@Override
	public void saveAudio(Map<String, Object> result) throws Exception {
		mapper.saveAudio(result);
	}

	@Override
	public void crateTitle(Map<String, Object> params) throws Exception {
		mapper.crateTitle(params);
	}

	@Override
	public void updateAudio(Map<String, Object> params) throws Exception {
		mapper.updateAudio(params);
	}

	@Override
	public Map<String, Object> getAudioInfo(Map<String, Object> params) throws Exception {
		return mapper.getAudioInfo(params);
	}

	@Override
	public void updateTts(Map<String, Object> map) throws Exception {
		mapper.updateTts(map);
	}
}
