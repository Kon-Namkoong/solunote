package com.vol.solunote.comm.service.tts;

import java.util.List;
import java.util.Map;

public interface TtsService {

	public	Map<String, Object> generateText(String keyword, String speech, String detail) throws Exception;
	public	Map<String, Object> saveTts(String voice, String sentence) throws Exception;
}
