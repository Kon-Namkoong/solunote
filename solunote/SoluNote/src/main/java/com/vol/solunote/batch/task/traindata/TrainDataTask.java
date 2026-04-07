package com.vol.solunote.batch.task.traindata;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vol.solunote.batch.task.SoundLauncher;
import com.vol.solunote.comm.service.stt.SttService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.repository.sound.SoundRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrainDataTask {
	
	private static final int SLEEP_TIME = 3;
	private static final int SLEEP_ERROR = 60;
	private int noDataSleep;

    private final int maxQueueSize;
    private Lock lock = new ReentrantLock();
    private AtomicInteger queueSize = new AtomicInteger(0);
    private BlockingQueue<MeetingVo> queue;
    private ConcurrentHashMap<String, Integer> conmap;			// key: consumer-thread-name, value : meeting seq no 
    
    List<MeetingVo> apiList = null;
    List<MeetingVo> errorList = new ArrayList<>();
    private int lastSeq = 0;
    private AtomicInteger readSize = new AtomicInteger(0);
    private AtomicInteger readIndex = new AtomicInteger(0);
    
    private SttService sttService;
    
    @Autowired
    private	SoundRepository soundRepository;

    public TrainDataTask(int maxQueueSize, SttService sttService, int noDataSleep) {
        this.maxQueueSize = maxQueueSize;
        this.sttService = sttService;
        this.noDataSleep = noDataSleep;
        
        this.queue = new ArrayBlockingQueue<>(maxQueueSize);
        this.conmap = new ConcurrentHashMap<>();
    }

    public int putMessage() throws Exception {
    	
    	log.debug("queue putMessage : started");
    	
    	int sleep = 0;   // sleep == 0 일때만 queue 에 put 한다
    	
        if (queueSize.get() == maxQueueSize) {
            sleep = SLEEP_TIME;
            log.debug(" while (queueSize.get() == maxQueueSize) {");
            return sleep;
        }
        
        lock.lock();
        try {

        	if ( readIndex.get() >= readSize.get()  ) {
        		// 1. TB_CS_MEETING_API
        		MeetingVo search = new MeetingVo();
        		search.setSeq(lastSeq);
        		search.setStatus(SoundLauncher.API_STATUS_UPLOAD);
        		apiList = soundRepository.getSoundFromSeq(search);
        		if ( apiList.size() > 0 ) {
        			// sql 로 읽어 온 마지막 seq 를 저장함
        			lastSeq = apiList.get(apiList.size()-1).getSeq();
        			log.debug("lastSeq 1 = {}", lastSeq);
        			for( MeetingVo vo : apiList ) {
        				log.debug("apiList read : {}", vo);
        			}
        		} 
        		
        		// 2. errorList 가 있으면 apiList 에 추가함
        		if ( errorList.size() > 0 ) {
        			apiList.addAll(errorList);
        			errorList = new ArrayList<>();
        		}
        		
        		readSize.set(apiList.size());
        		readIndex.set(0);
        		if ( apiList.size() == 0 ) {
        			sleep = noDataSleep;
        		}
        	} else {
        		log.debug("queue readIndex.get() {} >= readSize.get() {} ", readIndex.get(), readSize.get());
        	}
			
			if ( sleep == 0 ) {
				int gap = maxQueueSize - queue.size();
				
				if ( gap > 0) {
					for( int g = 0; g < gap; g++ ) {
						
						int x = readIndex.getAndUpdate(v -> {
						    if (v == Integer.MAX_VALUE) {
						        throw new ArithmeticException("overflow");
						    }
						    return v + 1;
						});
						
						if ( x < apiList.size() ) {
							MeetingVo vo = apiList.get(x);
							if ( conmap.values().contains( vo.getSeq() ) ) {
								log.debug("queue 1 skip for working seq  {}", vo.getSeq());
							} else {
								queue.put(vo);
								log.debug("queue 2 queue.put ; key {} queue size: {}, i = {}, queue.size = {}", vo.getSeq(), queue.size(), x, queue.size());
								for( Entry<String, Integer> e : conmap.entrySet() )	 {
									log.debug("conmap list 1 : {}, {}", e.getKey(), e.getValue());
								}
							}
						} else {
							log.debug("else breakk");
							break;
						}
					}
				} else {
					log.debug("else gap");
					sleep = SLEEP_ERROR;
				}
			}
			
        } catch (ArithmeticException e) {
        	log.error("Integer Overflow, Exit Loop");
        }
        finally {
            lock.unlock();
        }
        
        log.debug("queue putMessage : ended - sleep = {}", sleep);
        return sleep;
    }

    
  public void updateMessage(String url) throws Exception {
    	
    	log.debug("queue updateMessage in TrainDataTask : started ");
    	
    	// queue.take() wait until queue are full
    	MeetingVo vo = queue.take();   
    	log.debug("queue.take ; key {} queue size: {}", vo.getSeq(), queue.size());
    	
    	boolean flag = true;
    	try {
    		conmap.put(Thread.currentThread().getName(), vo.getSeq());
    		
    		log.debug("2. sttService.backendStt(vo); {} ", vo);
    		flag = sttService.backendStt(Category.TRAIN, vo);
    	}
    	catch (IOException e) {
    		log.error("IOException ,", e);
    	}
    	catch (Exception e) {
    		log.error("Exception ,", e);    		
    	}
    	finally {
    		log.debug( "conmap.remove(Thread.currentThread().getName()) = {}, {} : {}", Thread.currentThread().getName(), vo.getSeq(), flag);
    		
    		// STT 에러가 발생했으면 해당 vo 를 apiList 에 추가함
    		if ( flag == true ) {
    			lock.lock();
    			try {
                	log.debug("SLEEP {}, TrainDataTask.updateMessage()", SLEEP_ERROR);
                	Thread.sleep( SLEEP_ERROR * 1000);
					errorList.add(vo);
					log.debug("errorList add : {}", vo);
    			} catch (NullPointerException e) {
    				log.error("NullPointerException Occured", e);    
    			} catch (Exception e) {
					log.error("Exception Occured", e);    				
    			} finally {
    				lock.unlock();
    			}
    		}
    		
    		conmap.remove(Thread.currentThread().getName());
    	}
    	
    	for( Entry<String, Integer> e : conmap.entrySet() )	 {
    		log.debug("conmap list 2 : {}, {}", e.getKey(), e.getValue());
    	}
    	
        log.debug("queue updateMessage : ended");
    }
}

