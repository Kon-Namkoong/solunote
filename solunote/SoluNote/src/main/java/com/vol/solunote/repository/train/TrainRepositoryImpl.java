package com.vol.solunote.repository.train;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.vol.solunote.batch.train.vo.TrainCompareVo;
import com.vol.solunote.batch.train.vo.TrainServerDataVo;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.vo.SearchVo;
import com.vol.solunote.mapper.train.TrainMapper;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.train.TrainResultVo;
import com.vol.solunote.model.vo.train.TrainVo;
import org.springframework.stereotype.Repository;

@Repository
public class TrainRepositoryImpl implements TrainRepository {
	
	@Value("${train.url}")
	private String trainUrl; 
	
	@Value("${train.project_id}")
	private String PROJECT_ID; 
	
	@Autowired
	private TrainMapper mapper;
	
	@Override
	public int insertTrain(TrainVo vo) throws Exception {
		return mapper.insertTrain(vo);
	}
	
	@Override
	public	int updateTransPairModel(TrainVo vo)  throws Exception
	{
		return	mapper.updateTransPairModel(vo);
	}
	
	@Override
	public int getTransPairCount() throws Exception {
		return mapper.getTransPairCount();
	}
	
	@Override
	public	void insertTrainResult(TrainResultVo vo)  throws Exception {
		mapper.insertTrainResult(vo);
	}
	
	@Override
	public	void updateTrainData(TrainVo resultVo) throws Exception {
		mapper.updateTrainData(resultVo);
	}
	
	@Override
	public	int updateTestTransPairModel(TrainVo vo)  throws Exception {
		return mapper.updateTestTransPairModel(vo);
	}
	
	@Override
	public List<TrainVo> getSchedule() throws Exception {
		return mapper.getSchedule();
	}


	@Override
	public List<TrainVo> getTrainData(SearchVo search, OffsetPageable offsetPageable) throws Exception {
		return mapper.getTrainData(search, offsetPageable);
	}


	@Override
	public List<String> getDataIdFromModel(String modelId) {
		return null;
	}

	@Override
	public	void insertTrainServerData(TrainServerDataVo vo) throws Exception {
		mapper.insertTrainServerData(vo);
	}

	@Override
	public	void deleteAllTrainServerData()  throws Exception {
		mapper.deleteAllTrainServerData();
	}

	@Override
	public	List<TrainServerDataVo> selectTrainServerData(TrainServerDataVo search) throws Exception {
		return	mapper.selectTrainServerData(search);
	}

	@Override
	public	void deleteTransPair(String dataId) throws Exception {
		mapper.deleteTransPair(dataId);	
	}

	
	@Override
	public	void deleteTestPair(String dataId) throws Exception {
		mapper.deleteTestPair(dataId);
	}
	
	@Override
	public	List<TrainCompareVo> readTrainServerAndTrans() throws Exception{
		return	mapper.readTrainServerAndTrans();
	}
	
	@Override
	public	void updateTransServerMode(TrainCompareVo vo)  throws Exception{
		mapper.updateTransServerMode(vo);
	}
	
	@Override
	public	List<TrainCompareVo> readTrainServerAndTestTrans()  throws Exception{
		return	mapper.readTrainServerAndTestTrans();
	}
	
	@Override
	public	List<TrainCompareVo> readTrainServerNoneExists() throws Exception{
		return	mapper.readTrainServerNoneExists();
	}
	
	public	List<TrainVo> getList(Map<String, Object> param) throws Exception 
	{
		return	mapper.getList(param);
	}
	
	public	List<TrainVo> requestList(Map<String, Object> param) throws Exception
	{
		return	mapper.requestList(param);
	}

	public	List<Map<String, Object>> getTrainResultList(Map<String, Object> param) throws Exception
	{
		return	mapper.getTrainResultList(param);
	}
	
	public	void listAdd(String startTime)
	{
		mapper.listAdd(startTime);
	}
	
	public	int checkdata(String startTime)
	{
		return	mapper.checkdata(startTime);
	}
	
	public	void excludeTrans(int[] seq)
	{
		mapper.excludeTrans(seq);
	}
	
	public	List<Map<String, Object>> getTrainList(String modelId) throws Exception
	{
		return	mapper.getTrainList(modelId);
	}
	
	public	List<TranscriptionVo> getAllTestTransList() throws Exception
	{
		return	mapper.getAllTestTransList();
	}
}
