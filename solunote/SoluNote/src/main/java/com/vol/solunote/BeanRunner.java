package  com.vol.solunote;

import java.io.IOException;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.vol.solunote.comm.service.FFMpegService;

import lombok.extern.slf4j.Slf4j;

//@SpringBootApplication
@EnableScheduling
@MapperScan(value={"com.vol.solunote.**.mapper"})
@MapperScan(value={"com.vol.solunote.repository"})
@EnableJpaAuditing
@ComponentScan(basePackages = "com.vol.solunote")
@Slf4j
public class BeanRunner {
	

	public static void main(String[] args) throws Exception {
			 
		 
		 log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		 
		 
		 log.debug("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
		 log.debug("finished");
		 
		 System.exit(0);
	}

	public static void audioInfo(ApplicationContext context) throws IOException {
		
		String[] ary = {
				  "D:\\Temp\\wav\\wang\\wangfei.aac",
				  "D:\\Temp\\wav\\wang\\wangfei.wav",
				  "D:\\Temp\\wav\\wang\\cut\\wangfei1.aac",
				  "D:\\Temp\\wav\\wang\\cut\\wangfei2.aac",
				  "D:\\Temp\\wav\\wang\\cut\\wangfei.wav"
		};
				  
		  FFMpegService bean = context.getBean(FFMpegService.class);
			
			for( String s : ary ) {
				bean.printMediaInfo(s);
			}

	}
		
	
}
