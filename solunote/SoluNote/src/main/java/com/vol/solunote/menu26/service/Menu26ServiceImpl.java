package com.vol.solunote.menu26.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vol.solunote.comm.util.HtmlVisitor;
import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.repository.train.TrainRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Menu26ServiceImpl implements Menu26Service {

	@Autowired
	private TrainRepository trainRepository;

	@Override
	public List<TrainVo> getList(Map<String, Object> param) throws Exception {
		
		
		List<TrainVo> list = trainRepository.getList(param);
//		List<TrainVo> list = mapper.getList(activeMenu, offsetPageable, searchStartDate ,searchEndDate);
		
		for (TrainVo vo : list ) {
			String startTime = (String) vo.getStartTime();
			String findStatus = (String) vo.getStrStatus();
			String todayfm = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date(dateFormat.parse(startTime).getTime()); 
			Date today = new Date(dateFormat.parse(todayfm).getTime());
			int compare = date.compareTo(today); 
			
			if (compare>0){
				vo.setColor(1);
			}else {
				vo.setColor(0);
				
			}
			if(findStatus == "UNDEFINED") {
				vo.setStatus("");	
			}else {
				vo.setStatus(vo.getStrStatus());
			}
			
			if(findStatus== "학습완료" && vo.isSuccess()==true) {
				vo.setSuccessTrueFalse("o");
			}else if(findStatus== "학습완료" && vo.isSuccess()==false) {
				vo.setSuccessTrueFalse("x");
			}else {
				vo.setSuccessTrueFalse("");
			}
		}
			
		return list;
	}

	@Override
	public List<TrainVo> requestList(Map<String, Object> param) throws Exception {
		
		List<TrainVo> list = trainRepository.requestList(param);
		
		for (TrainVo vo : list ) {
			String duration = vo.getDuration();
			String findStatus = (String) vo.getStrStatus();
			
			if(findStatus == "UNDEFINED") {
				vo.setStatus("");	
			}else {
				vo.setStatus(vo.getStrStatus());
			}
			
			if (vo.getCer() >0) {
				double cer= (1-vo.getCer())*100;
				vo.setCer(cer);	
			}
			
			
			if ( duration != null) {
				int time = Integer.parseInt(duration);
				int hours = time / 3600;
				int minutes = (time % 3600) / 60;
				int seconds = time % 60;
				String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				vo.setDuration(timeString);
			}
			
			if(findStatus== "학습완료" && vo.isSuccess()==true) {
				vo.setSuccessTrueFalse("o");
			}else if(findStatus== "학습완료" && vo.isSuccess()==false) {
				vo.setSuccessTrueFalse("x");
			}else {
				vo.setSuccessTrueFalse("");
			}
			
		}
			
		return list;
	}

	@Override
	public List<Map<String, Object>> getTrainResultList(Map<String, Object> param) throws Exception {
		
		
		List<Map<String, Object>> list = trainRepository.getTrainResultList(param);

		
		for( Map<String, Object> map : list ) {
			float cer = (float) map.get("cer");
			if ( cer > 1 ) {
				cer = 1;
			}
			int pct = (int) (100 * ( 1 - cer ));
			map.put("pct", pct);
			
			HtmlVisitor htmlVisitor = new HtmlVisitor();
			htmlVisitor.diff((String)map.get("trainText"), (String)map.get("sttText"));

			map.put("trainText", htmlVisitor.getLeft());
			map.put("sttText", htmlVisitor.getRight());
		}	
		return list;
	}
	
	
	@Override
	public void listAdd(String startTime) throws Exception {
		trainRepository.listAdd(startTime);
	}

	
//	@Override
//	public void registerChange(int seq,String type, String day,String time,String useYn) throws Exception {
//		mapper.registerChange(seq,type,day,time,useYn);
//	}
	
	@Override
	public int checkdata(String startTime) throws Exception {
		return trainRepository.checkdata(startTime);
	}
	
	@Override
	public void excludeTrans(int[] seq) throws IOException {
		trainRepository.excludeTrans(seq);
		
	}


	@Override
	public List<Map<String, Object>> getTrainList(String modelId) throws Exception {
		
		return trainRepository.getTrainList(modelId);
	}
	
}
