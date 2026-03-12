package com.vol.solunote.mapper.train;

import java.util.List;

import java.util.Map;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.train.TrainCompareVo;
import com.vol.solunote.model.vo.train.TrainResultVo;
import com.vol.solunote.model.vo.train.TrainServerDataVo;
import com.vol.solunote.model.vo.train.TrainVo;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainMapper {

	public	List<TrainVo> getSchedule() throws Exception;

	public	int insertTrain(TrainVo vo) throws Exception;

	public	int getTransPairCount() throws Exception;

	public	int updateTransPairModel(TrainVo vo)  throws Exception;

	public	int updateTestTransPairModel(TrainVo vo)  throws Exception;

	public	List<TrainVo> getTrainData(SearchVo search, OffsetPageable offsetPageable) throws Exception;

	public	void updateTrainData(TrainVo resultVo)  throws Exception;

	public	void insertTrainResult(TrainResultVo vo)  throws Exception;

	public	void insertTrainResultFull(TrainResultVo vo)  throws Exception;

	public	void updateTrainSchedule(TrainVo scheduleVo)  throws Exception;

	public	void listAdd(String startTime);
	
	public	int checkdata(String startTime);
	
	public	List<TrainVo> getList(int activeMenu ,OffsetPageable offsetPageable ,String searchStartDate , String searchEndDate) throws Exception;	

	public	List<TrainVo> requestList(int activeMenu ,OffsetPageable offsetPageable ,String searchStartDate , String searchEndDate) throws Exception;
	public	List<TrainVo> requestList(Map<String, Object> param) throws Exception;
	
	public	List<Map<String, Object>> getTrainResultList(Map<String, Object> param) throws Exception;
	public	void excludeTrans(int[] seq);
	
	public	List<Map<String, Object>> getTrainList(String modelId) throws Exception;
	public	List<TrainVo> getList(Map<String, Object> param) throws Exception;	
	
	public	void insertTrainServerData(TrainServerDataVo vo) throws Exception;

	public	void deleteAllTrainServerData()  throws Exception;

	public	List<TrainServerDataVo> selectTrainServerData(TrainServerDataVo search) throws Exception;


	public	void deleteTransPair(String dataId) throws Exception;

	public	List<TranscriptionVo> getAllTestTransList() throws Exception;

	public	void deleteTestPair(String dataId) throws Exception;

	public	List<TrainCompareVo> readTrainServerAndTrans() throws Exception;

	public	void updateTransServerMode(TrainCompareVo vo)  throws Exception;

	public	List<TrainCompareVo> readTrainServerAndTestTrans()  throws Exception;

	public	List<TrainCompareVo> readTrainServerNoneExists() throws Exception;		
}
