package com.vol.solunote.repository.train;

import java.util.List;
import java.util.Map;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.train.TrainCompareVo;
import com.vol.solunote.model.vo.train.TrainResultVo;
import com.vol.solunote.model.vo.train.TrainServerDataVo;
import com.vol.solunote.model.vo.train.TrainVo;

public interface TrainRepository {
	public static final String TRAIN_CALLED = "10";
	public static final String TRAIN_CANCELED = "15";
	public static final String TRAIN_RUNNING = "20";
	public static final String TRAIN_ENDED = "30";
	
	int getTransPairCount() throws Exception;

	List<TrainVo> getSchedule() throws Exception;

	List<TrainVo> getTrainData(SearchVo search, OffsetPageable offsetPageable) throws Exception;

	int insertTrain(TrainVo vo) throws Exception;
	
	int updateTransPairModel(TrainVo vo)  throws Exception;
	
	void insertTrainResult(TrainResultVo vo)  throws Exception;
	
	void updateTrainData(TrainVo resultVo) throws Exception;
	
	int updateTestTransPairModel(TrainVo vo)  throws Exception;

	List<String> getDataIdFromModel(String modelId)  throws Exception;

	void insertTrainServerData(TrainServerDataVo vo) throws Exception;

	void deleteAllTrainServerData()  throws Exception;

	List<TrainServerDataVo> selectTrainServerData(TrainServerDataVo search) throws Exception;


	void deleteTransPair(String dataId) throws Exception;

	List<TranscriptionVo> getAllTestTransList() throws Exception;

	void deleteTestPair(String dataId) throws Exception;

	List<TrainCompareVo> readTrainServerAndTrans() throws Exception;

	void updateTransServerMode(TrainCompareVo vo)  throws Exception;

	List<TrainCompareVo> readTrainServerAndTestTrans()  throws Exception;

	List<TrainCompareVo> readTrainServerNoneExists() throws Exception;	
	
	public	List<TrainVo> getList(Map<String, Object> param) throws Exception;	
	
	public	List<TrainVo> requestList(Map<String, Object> param) throws Exception;
	
	public	List<Map<String, Object>> getTrainResultList(Map<String, Object> param) throws Exception;
	
	public	void listAdd(String startTime);
	
	public	int checkdata(String startTime);
	
	public	void excludeTrans(int[] seq);
	
	public	List<Map<String, Object>> getTrainList(String modelId) throws Exception;
	
}
