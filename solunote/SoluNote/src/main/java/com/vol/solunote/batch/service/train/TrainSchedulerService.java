package com.vol.solunote.batch.service.train;
import java.util.List;
import java.util.Map;

import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.model.vo.transcription.TransVo;

public interface TrainSchedulerService {

	public	List<TrainVo>	getTrainList(SearchVo	search)	throws	Exception;
	public	TrainVo 		callTrainHistoryFromModel(String modelId) throws Exception;
	public 	void			updateTrain(TrainVo resultVo)	throws Exception;	
	public 	TrainVo 		fillFailedMap(String modelId ) throws	Exception;
	public	int				getTrainingCnt() throws Exception;
	public	String			requestTraining() throws Exception;
	public	void			afterTrainingCall(String model, int cnt, TrainVo findFirst) throws Exception;
	public	void			sendDataTrans() throws Exception;
	public	void			sendTtsTrans() throws Exception;
	public	void			deleteTransServer() throws Exception;
	public	void			sendTestTrans()	throws	Exception;
	public 	List<TrainVo> 	callTrainHistory(int page, int size) throws Exception;
	public	List<TrainVo>	getTrainScheduleList() throws Exception;		
}
