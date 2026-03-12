package com.vol.solunote.menu24.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.util.HtmlVisitor;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.repository.sound.SoundRepository;
import com.vol.solunote.repository.transcription.TranscriptionRepository;

import lombok.extern.slf4j.Slf4j;

@Service("Menu24SteelService")
@Slf4j
public class Menu24SteelServiceImpl implements Menu24Service {
	
	@Autowired
	private TranscriptionRepository transcriptionRepository;
	
	@Autowired
	private SoundRepository soundRepository;
	
	@Override
	public List<TransVo> getList(Map<String, Object> param) throws Exception {
		
//		DefaultController.addRequestParam(param, "caller", "list");
		List<TransVo> list = getDataTransListBatch(param);
		
		for( TransVo vo : list ) {
			String stt = vo.getSttText();
			String train = vo.getTrainText();

			if ( train != null ) {
				HtmlVisitor htmlVisitor = new HtmlVisitor();
				htmlVisitor.diff(stt, train);
				
				vo.setSttText(htmlVisitor.getLeft());
				vo.setTrainText( htmlVisitor.getRight());
			}
		}
		
		return list;
	}
	
	@Override
	public List<TransVo> getDataTransListBatch(Map<String, Object> param) throws Exception {
		
		return transcriptionRepository.getDataTransListBatch(param);
		
	}
	
	@Override
	public List<TranscriptionVo> getTranscriptionList(int meetSeq, String origin, int reliability) {
		
		List<TranscriptionVo> list = getTranscriptionList(meetSeq, origin, reliability);
		
		for( TranscriptionVo tr : list ) {
			if ( tr.getTrainText() == null ) {
				if ( tr.getMeetText() != null ) {
					tr.setTrainText(tr.getMeetText());
				} else {
//					tr.setTrainText(tr.getSttText());
					tr.setTrainText("");
				}
			}
		}
		
		return list;
	}
	
	
	@Override
	public List<TransVo> getDataTransListX(Map<String, Object> param) throws Exception {
		
		List<TransVo> list = null;
		
		if ( "reserve".equals(param.get("caller").toString())) {
			list = transcriptionRepository.getDataTransListReserve(param);
		} else {
			throw new RuntimeException("Not yet defined");
			// list = mapper.getDataTransListX(search, offsetPageable,keywoed, caller);
		}		
		
		return list;
	}
	
	@Override
	public List<TransVo> getDataTransListBatch(SearchVo search, OffsetPageable offsetPageable ,String keyword, String caller) throws Exception {
		
		List<TransVo> list = null;
		
		list = transcriptionRepository.getDataTransListBatch(search, offsetPageable,keyword, caller);
		
		return list;
	}
	

	@Override
	public void requestTrain(int seq) {
		System.out.println("todo");
	}

	@Override
	public int updateTrainTextBySeq(int meetSeq, String trainText) throws Exception
	{
		return	transcriptionRepository.updateTrainTextBySeq( meetSeq, trainText);
	}
	
	@Override
	public int updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception
	{
		return	soundRepository.updateRemarkAndUpdatedAtBySeq(updateRemark, updatedAt, meetSeq);
	}


	@Override
	@Transactional
	public void includeTrans(int[] seq) throws Exception {
		
		transcriptionRepository.includeTransTranscription(seq);
		
	}
	
	@Override
	@Transactional
	public void excludeTrans(int[] seq) throws IOException {
		
		transcriptionRepository.excludeTransTranscription(seq);
		
		excludeTransTransPair(seq);
		
	}
	
	@Override
	public void excludeTransTransPair(int[] seq) throws IOException {
		transcriptionRepository.excludeTransTransPair(seq);
	}
	
	@Override
	@Transactional
	public void updateTrainTextNull(int seq) throws Exception {
		
		transcriptionRepository.updateTrainTextNull( seq);
		
	}
	
	@Override
	@Transactional
	public void updateDataId(int seq, String dataId, boolean reset, String errorMsg, String useYn) throws Exception {
		
		transcriptionRepository.updateDataId( seq, dataId, reset, errorMsg, useYn);
		
	}

	@Override
	@Transactional
	public void insertTransPair(String dataId, int seq, TransVo vo) throws Exception {
			
		transcriptionRepository.insertTransPair( dataId, seq, vo.getTrainText(), vo.getUseYn(), vo.getStart(), vo.getEnd());
	}
	
	
	@Override
	public List<Map<String, Object>> getFailList(SearchVo search, OffsetPageable offsetPageable,String keyword) throws Exception {
		
		List<Map<String, Object>> list = soundRepository.getFailList(search, offsetPageable,keyword);
				
		return list;
	}

	@Override
	public String getUuid() throws Exception {
		return transcriptionRepository.getUuid();
	}

	@Override
	public int updateTranscriptionForSplit(Map<String, String> map) throws Exception {
		
		return transcriptionRepository.updateTranscriptionForSplit(map);
	}

	@Override
	public int updateTranscriptionForReset(Map<String, String> map) throws Exception {
		
		return transcriptionRepository.updateTranscriptionForReset(map);
	}
		
}
