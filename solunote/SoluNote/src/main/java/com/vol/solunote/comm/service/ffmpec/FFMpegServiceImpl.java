package com.vol.solunote.comm.service.ffmpec;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import jakarta.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vol.solunote.Exception.FFMpegCallException;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.comm.util.SoundSpectrum;
import com.vol.solunote.model.dto.transcription.TranscriptionDto;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.SoundFile;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.probe.FFmpegStream.CodecType;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

@Service
@Slf4j
public class FFMpegServiceImpl implements FFMpegService {

	@Value("${ffmpeg.ffmpeg.path}")
	private String ffmpegPath;

	@Value("${ffmpeg.ffprobe.path}")
	private String ffprobePath;

	private FFmpeg ffmpeg;
	private FFprobe ffprobe;
	
	@Value("${stt.ffmgeg.toType:wav}")
	private String sttToType;
	
	@Autowired
	private DiskService diskService;

	@PostConstruct
	public void init() {
		try {
			ffmpeg = new FFmpeg(ffmpegPath);
			Assert.isTrue(ffmpeg.isFFmpeg());

			ffprobe = new FFprobe(ffprobePath);
			Assert.isTrue(ffprobe.isFFprobe());

			log.debug("VideoFileUtils init complete.");
			
		} catch (IOException e) {
			log.error("VideoFileUtils init fail.", e);	
		} catch (Exception e) {
			log.error("VideoFileUtils init fail.", e);
		}
	}
	
	@Override
    public void printMediaInfo(String filePath) throws IOException {
    	
        FFmpegProbeResult ffmpegProbeResult;
        
        System.out.println("file name ----- " + filePath);
        
        try {
            ffmpegProbeResult = ffprobe.probe(filePath);
            List<FFmpegStream> streams = ffmpegProbeResult.getStreams();
            
            int idx = 1;
            System.out.println("  frame  ----- " + idx);
            for( FFmpegStream stream : streams ) {
            	System.out.println("    bits_per_sample : "    +stream.bits_per_sample);
            	System.out.println("    비트레이트 : "    +stream.bit_rate);
            	System.out.println("    sample_rate : "    +stream.sample_rate);
            	System.out.println("    채널 : "    +stream.channels);
            	System.out.println("    코덱 명 : "    +stream.codec_name);
            	System.out.println("    코덱 유형 : "    +stream.codec_type);
            	System.out.println("    해상도(너비) : "    +stream.width);
            	System.out.println("    해상도(높이) : "    +stream.height);
            	System.out.println("    포맷(확장자) : "    +ffmpegProbeResult.getFormat());
            }


        } catch (IOException e) {
            System.err.println(e.toString());
            throw new RuntimeException(e);
        }

        
//        FFmpegProbeResult probeResult = ffprobe.probe(filePath);
//
//        System.out.println("========== VideoFileUtils.getMediaInfo() ==========");
//        System.out.println("filename : {}" + probeResult.getFormat().filename);
//        System.out.println("format_name : {}" + probeResult.getFormat().format_name);
//        System.out.println("format_long_name : {}" + probeResult.getFormat().format_long_name);
////            System.out.println("tags : {}" + probeResult.getFormat().tags.toString());
//        System.out.println("duration : {} second" + probeResult.getFormat().duration);
//        System.out.println("size : {} byte" + probeResult.getFormat().size);
//
//        System.out.println("width : {} px" + probeResult.getStreams().get(0).width);
//        System.out.println("height : {} px" + probeResult.getStreams().get(0).height);
//        System.out.println("===================================================");
    }

	@Override
	public String getAudioCodec(String filePath) throws IOException {
		
		FFmpegProbeResult ffmpegProbeResult;
		String result = null;
		
//		log.info("file name ----- " + filePath);
		
		try {
			ffmpegProbeResult = ffprobe.probe(filePath);
			List<FFmpegStream> streams = ffmpegProbeResult.getStreams();
			

//			log.info("  frame  ----- " + idx);
			for( FFmpegStream stream : streams ) {
				if ( stream.codec_type == CodecType.AUDIO ) {
					result = stream.codec_name;
					break;
				}
			}
			
			
		} catch (IOException e) {
			System.err.println(e.toString());
			throw new RuntimeException(e);
		}
		
		return result;
	}

	/**
	 * file 이 disk 에 존재하는지 확인한다
	 * return : null - disk 에 있음
	 *        : String - 에러 메세지
	 */
	@Override
    public String isFilePathExists(Category category, String path) throws Exception {
		
		String root = diskService.getUploadPath(category);
		File file = Paths.get(root, path).toFile();

		String result = null;
		
		if ( file.exists() == false ) {
			result = "File not found - " + file.toString();
			
//			log.debug("file.toString()        : " + file.toString());
//			log.debug("file.getAbsolutePath() : " + file.getAbsolutePath());
		}
		
		return result;		
	}
	
	@Override
	public String isFilePathExists(Category category, TransVo vo) throws Exception {
		
		String path = null;
		int channelCount = vo.getChannelCount();
		if ( channelCount == 2 ) {
			path = vo.getFileStereoPrefix();
		} else {
			path = vo.getFileNewNm();
		}	
		
		File file = diskService.getUploadedFile(category, path, Integer.toString(channelCount), Integer.toString(vo.getChannelId()));
		
		String result = null;
		
		if ( file.exists() == false ) {
			result = "File not found - " + file.toString();
		}
		
		return result;		
	}
	
	@Override
    public byte[] cutAudio(Category category, String path, double startSecond, double endSecond) throws Exception {
		
		if (diskService.hasDirectoryScanChar(path))
		{
			throw new Exception("File Name has directory scan character");
		}
		else
		{
			String root = diskService.getUploadPath(category);		
			String	newPath = diskService.removeDirScanChar(path);
			
			File file = Paths.get(root, newPath).toFile();
		
			byte[] bytes = null;
			String ext = FilenameUtils.getExtension(file.getName());

			Path temp = Files.createTempFile("ffmpeg", "." + ext);

			FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addInput(file.getAbsolutePath()) // 입력 영상 경로의
				.addExtraArgs("-ss", Double.toString(startSecond)) // 영상의 i초 위치 부
				.addExtraArgs("-to", Double.toString(endSecond)) // 3초 동안 재생한 영상
				.addOutput(temp.toString()) // outputpath 위치에
//                   .addExtraArgs("-an") //영상의 소리를 제거하고
				.done(); // 저장

			try {
				FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
				excutor.createJob(builder).run();
			} catch ( Exception e ) {
				log.error("ffmpeg run error : {}", e.getMessage());
				log.error("cmd : ffmpeg -i {} -ss {} -to {} {}", file.getAbsolutePath(), Double.toString(startSecond), Double.toString(endSecond), temp.toString());
				FFMpegCallException fce = new FFMpegCallException(file.toString(), e.getMessage());
				throw fce;
			}

			bytes = Files.readAllBytes(Paths.get(temp.toString()));
		
			Files.delete(temp);
		
			return bytes;
		}
    }
	
	@Override
	public byte[] cutAudio(Category category, String path, String startSecond, String endSecond, String channelCount, String channelId) throws Exception {
		
		if (diskService.hasDirectoryScanChar(path))
		{
			throw new Exception("File Name has directory scan character");
		}		
		
		File file = diskService.getUploadedFile(category, path, channelCount, channelId);
		log.debug("file file : " + file.toString());
		
		byte[] bytes = null;
		String ext = FilenameUtils.getExtension(file.getName());
		
		Path temp = Files.createTempFile("ffmpeg", "." + ext);
//		log.debug("temporary file : " + temp.toString());
		
		FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addInput(file.getAbsolutePath()) // 입력 영상 경로의
				.addExtraArgs("-ss", startSecond) // 영상의 i초 위치 부
				.addExtraArgs("-to", endSecond) // 3초 동안 재생한 영상
				.addOutput(temp.toString()) // outputpath 위치에
				.done(); // 저장
		
		try {
			FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
			excutor.createJob(builder).run();
		} catch ( Exception e ) {
			log.error("ffmpeg run error : {}", e.getMessage());
			FFMpegCallException fce = new FFMpegCallException(file.toString(), e.getMessage());
			throw fce;
		}
		
		bytes = Files.readAllBytes(Paths.get(temp.toString()));
		
		Files.delete(temp);
//		log.debug("temp file : {}", temp.toString());
		
		return bytes;
	}
	
	/**
	 * 이를 호출한 프로그램은 return 된 temp 파일을 try 안에서 Files.delete(path) 해야 한다.
	 * 
	   Path tempFile = null;
	   try {
			tempFile = ffmpegService.cutAndSaveAudio(testFlag == true ? "test" : "train", vo.getFileNewNm(), vo.getStart(), vo.getEnd());
		} finally {
			if ( tempFile != null ) {
				Files.delete(tempFile);
			}
		}
	 */
	@Override
	public Path cutAndSaveAudio(Category category, String path, double startSecond, double endSecond) throws Exception {
		
		String root = diskService.getUploadPath(category);
		File file = Paths.get(root, path).toFile();
		
		String ext = FilenameUtils.getExtension(file.getName());
		
		Path temp = Files.createTempFile("ffmpeg", "." + ext);
//		log.debug("temporary file : " + temp.toString());
		
		FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addInput(file.getAbsolutePath()) // 입력 영상 경로의
				.addExtraArgs("-ss", Double.toString(startSecond)) // 영상의 i초 위치 부
				.addExtraArgs("-to", Double.toString(endSecond)) // 3초 동안 재생한 영상
				.addOutput(temp.toString()) // outputpath 위치에
//                   .addExtraArgs("-an") //영상의 소리를 제거하고
				.done(); // 저장
		
		try {
			FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
			excutor.createJob(builder).run();
		} catch ( Exception e ) {
			log.error("ffmpeg run error : {}", e.getMessage());
			FFMpegCallException fce = new FFMpegCallException(file.toString(), e.getMessage());
			throw fce;
		}
		
//		bytes = Files.readAllBytes(Paths.get(temp.toString()));
//		
		Files.delete(temp);
//		log.debug("temp file : {}", temp.toString());
		
		return temp;
	}
	
	@Override
	public void saveAudio(String dir, String no, String text, File file, double startSecond, double endSecond) throws Exception {
		
		
		String ext = FilenameUtils.getExtension(file.getName());
		file.getName();
		

		
		Path temp = Files.createTempFile(Paths.get(dir), no + "_", "." + ext);
		log.debug("temporary file : " + temp.toString());
		
		FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addInput(file.getAbsolutePath()) // 입력 영상 경로의
				.addExtraArgs("-ss", Double.toString(startSecond)) // 영상의 i초 위치 부
				.addExtraArgs("-to", Double.toString(endSecond)) // 3초 동안 재생한 영상
				.addOutput(temp.toString()) // outputpath 위치에
//                   .addExtraArgs("-an") //영상의 소리를 제거하고
				.done(); // 저장
		
		FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
		excutor.createJob(builder).run();
			
		String wavname = temp.toString();
		
		int len = wavname.length();
		String textfile = wavname.substring(0, len - 3) + "txt";
		
		
		 try {
	            FileUtils.writeStringToFile(new File(textfile), text, StandardCharsets.UTF_8);
	        }
	 
	        catch (IOException ex) {
	            System.out.print("Invalid Path");
	        }
		 
		
		Files.delete(temp);
		
	}
    
	@Override
	public String convertFile(String infile, String outPathAndBase) throws Exception {
		
		log.debug("convtime start, infile = {},  outfile = {}", infile, outPathAndBase);
		
		String fullPath = FilenameUtils.getFullPath(outPathAndBase);
		Path path = Paths.get(fullPath);
		if ( Files.exists(path) == false ) {
			throw new RuntimeException("Error : output directory does not exists - " + fullPath);
		}
		
		String inputExt = FilenameUtils.getExtension(infile).toLowerCase();
		String convExt = null;
		
		switch( inputExt ) {
			case "mp3" :
			case "mp4" :
			case "aac" :
			case "m4a" : 
			case "avi" :
			case "mpeg" : 
			case "ogg" : 
			case "webm" : convExt = sttToType;
				break;
			
			default : throw new RuntimeException("unknown file type : " + inputExt);
		}
		
		Path inPath = Paths.get(infile);
		long inSize = Files.size(inPath) / 1024;
		Instant start = Instant.now();
		
		String outfile = outPathAndBase + "." + convExt;
		
		 FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addInput(infile)
				.addOutput(outfile)
				.setAudioChannels(1)         // Mono audio
				.setAudioCodec("pcm_s16le")        // using the PCM 16-bit singed little-endian codec
				.setAudioSampleRate(16000)  // 16kHz : at 48KHz
//				.setAudioBitRate(32768)      // at 32 kbit/s
				.addExtraArgs("-vn") //영상의 소리를 제거하고
				.done();
		
		FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
		excutor.createJob(ffmpegBuilder).run();
		
		Path outPath = Paths.get(outfile);
		long outSize = Files.size(outPath) / 1024;
		
		Instant end = Instant.now();
		long time = Duration.between(start, end).toMillis();
//		Duration between = Duration.between(start, end);
		String format = DurationFormatUtils.formatDuration(time, "H:mm:ss", true);
		log.debug("convtime, infile = {}, size = {}, time = {}, outfile = {}, size = {}", 
				infile, inSize, format, outfile, outSize);
		
		return convExt;	
	}

	@Override
	public void convertLocalFile(String infile, String outfile) throws Exception {
		
		Instant start = Instant.now();
		Path inPath = Paths.get(infile);
		long inSize = Files.size(inPath) / 1024;
		
		FFmpegProbeResult in = ffprobe.probe(infile);
		
		FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.addInput(infile)
				.addOutput(outfile)
				.setAudioChannels(1)         // Mono audio
				.setAudioCodec("pcm_s16le")        // using the PCM 16-bit singed little-endian codec
				.setAudioSampleRate(16000)  // 16kHz : at 48KHz
//				.setAudioBitRate(32768)      // at 32 kbit/s
				.addExtraArgs("-vn") //영상의 소리를 제거하고
				.done();
		
		FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
//		excutor.createJob(ffmpegBuilder).run();
		excutor.createJob(ffmpegBuilder, new ProgressListener() {

			 // Using the FFmpegProbeResult determine the duration of the input
			 final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

			 @Override
			 public void progress(Progress progress) {
			  double percentage = progress.out_time_ns / duration_ns;

			  // Print out interesting information about the progress
			  System.out.println(String.format(
			   "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
			   percentage * 100,
			   progress.status,
			   progress.frame,
			   FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
			   progress.fps.doubleValue(),
			   progress.speed
			  ));
			 }
			}).run();
//		excutor.createJob(ffmpegBuilder, p -> {
//			if(p.isEnd()) {
//				log.debug("convertLocalFile finished : {}", infile);
//			}
//		}).run();
		log.debug("convertLocalFile run() finished : {}", infile);
//		excutor.createTwoPassJob(builder).run();
		
		Path outPath = Paths.get(outfile);
		long outSize = Files.size(outPath) / 1024;
		
		Instant end = Instant.now();
		long time = Duration.between(start, end).toMillis();
//		Duration between = Duration.between(start, end);
		String format = DurationFormatUtils.formatDuration(time, "H:mm:ss", true);
		log.debug("convtime, infile = {}, size = {}, time = {}, outfile = {}, size = {}", 
				infile, inSize, format, outfile, outSize);
		log.debug("convertLocalFile method finished : {}", infile);
		
	}
	
	@Override
	public String copyStereoFileAndRemove(String inpath, String stereoDir, String orgPath, String baseName) throws Exception {
		
		log.debug("copyStereoFileAndRemove start");
		
		Path stereoPath = Paths.get(stereoDir, orgPath);
		
		File stereoFile = stereoPath.toFile();
		if ( ! stereoFile.exists() ) {
			stereoFile.mkdirs();
		}
		
		String base = checkExistName(stereoPath.toString(), baseName);
		String left = Paths.get(stereoPath.toString(), base + "_0.wav").toString();
		String right = Paths.get(stereoPath.toString(), base + "_1.wav").toString();
		
		// https://github.com/bramp/ffmpeg-cli-wrapper/wiki/Random-Examples
		FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
				  .setVerbosity(FFmpegBuilder.Verbosity.DEBUG)
				  .setInput(inpath)
				  .addOutput(left)
				      .addExtraArgs("-map_channel", "0.0.0")
				      .done()
				  .addOutput(right)
				      .addExtraArgs("-map_channel", "0.0.1")
				      .done();
		
		FFmpegExecutor excutor = new FFmpegExecutor(ffmpeg, ffprobe);
		excutor.createJob(ffmpegBuilder).run();
		
		log.debug("copyStereoFileAndRemove, ffmpeg -i {} -map_channel 0.0.0 {} -map_channel 0.0.1 {}", inpath, left, right);
		
		// to-do : 삭제는 daily batch 로 처리함
		// scheduler 로 처리
		
//		File infile = new File(inpath);
//		infile.delete();
//		
//		log.debug("copyStereoFileAndRemove, delete {}", inpath);
		
		return orgPath + "/" + base;
	}

	private String checkExistName(String dir, String baseName) throws Exception {
		
		boolean exists = true;
		String sur = "";
		String baseSur = "";
		
		for( int i = 0; i < 100; i++ ) {
			if ( i != 0 ) {
				sur = "_" + Integer.toString(i);
			}
			for( int n = 0; n < 2; n++ ) {
				exists = false;
				baseSur = baseName + sur;
				File file = Paths.get(dir, baseSur + "_" + n + ".wav").toFile();
				if ( file.exists() ) {
					exists = true;
					break;
				}
			}
			if ( exists == false ) {
				break;
			}
		}
		
		
		if ( exists == true ) {
			throw new RuntimeException("Error : checkExistsName end with 100 tries");
		}
		return baseSur;
	}
		
	@Override
	public File getOrCopyWaveFile(Category category, String fileNm) throws Exception {
		
		String subdir = diskService.getUploadPath(category);
		Path path = Paths.get(subdir, fileNm);
		File file = path.toFile();
		
		boolean needConvert = SoundFile.needConvert(file);
		
		if ( needConvert == true ) {
			
			String convName = fileNm + ".wav";
			
			String tempPath = diskService.getUploadPath(Category.TEMP);
			Path convPath = Paths.get(tempPath, convName);
			File convFile = convPath.toFile();
			
			if ( ! convFile.exists() ) {
//				Files.copy(path, convPath);
				
				String targetDir = FilenameUtils.getFullPathNoEndSeparator(convPath.toString());
				File targetFile = new File(targetDir);
				if ( ! targetFile.exists() ) {
					targetFile.mkdirs();
				}
				
				convertLocalFile(file.toString(), convPath.toString());
				log.debug("copy : {} to {}", path.toString(), convPath.toString());
			} else {
				log.debug("copy : no, already exists at {}", convPath.toString());
			}
			
			file = convFile;
		} 
		
		return file;
	}
	
	@Override
	public String copyStereoFileAndRemove(Category category, MeetingVo vo) throws Exception {
		
		String orgNm = vo.getFileNewNm();
		String orgPath = FilenameUtils.getFullPathNoEndSeparator(orgNm);
		String baseName = FilenameUtils.getBaseName(orgNm);
		
		String fromDir = diskService.getUploadPath(category);
		Path fromPath = Paths.get(fromDir, orgNm);
		File fromFile = fromPath.toFile();
		
		String stereoDir = diskService.getUploadPath(Category.STEREO);
		
		String base = copyStereoFileAndRemove(fromFile.toString(), stereoDir, orgPath, baseName);
		
		return base;
	}
	@Override
	public void writeWave(OutputStream outputStream, Category category, String fileNm, float start, float end, float prevEnd, float nextStart)	throws Exception {

		String root = diskService.getUploadPath(category);
		if (diskService.hasDirectoryScanChar(fileNm))
		{
			throw new Exception("FileName is not valid format");
		}
		else
		{
			String	newFileNm = diskService.removeDirScanChar(fileNm);
			File file = Paths.get(root, newFileNm).toFile();
		
			SoundFile soundFile = new SoundFile(file);
			SoundSpectrum spectrum = new SoundSpectrum(soundFile);
			spectrum.writeWave(outputStream, prevEnd, start, end, nextStart);
		}
	}


	public <T extends TranscriptionVo> List<Map<String, Object>> appendWaveSpectrum(Category category, SoundVo soundVo, List<T> list)	throws Exception {
		
		long start = System.currentTimeMillis();
		
		int channelCount = soundVo.getChannelCount();
		List<Integer> IdList = list.stream().map(TranscriptionVo::getChannelId).distinct().collect(Collectors.toList());
		SoundSpectrum[] spectrums = new SoundSpectrum[channelCount];
		
		if ( channelCount == 2 ) {
			for( int i = 0; i < channelCount; i++ ) {
				if ( IdList.contains(i) ) {
					File file = diskService.getUploadedFile(category, soundVo.getFileStereoPrefix(), Integer.toString(channelCount), Integer.toString(i));
					spectrums[i] = new SoundSpectrum(file);
				}
				
			}
		} else {
			File file =  getOrCopyWaveFile(category, soundVo.getFileNewNm() );
			spectrums[0] = new SoundSpectrum(file);
		}
		
	
		List<Map<String, Object>> result = new ArrayList<>();
		
		for( T vo : list ) {
			int channelId = vo.getChannelId();
			byte[] bytes = spectrums[channelId].writeWaveSvg( vo.getPrevEnd(), vo.getStart(), vo.getEnd(), vo.getNextStart());
			Map<String, Object> map = new HashMap<>();
			map.put("seq", vo.getSeq());
//			map.put("image", Base64.getEncoder().encode(bytes));
			map.put("image", Base64.getEncoder().encodeToString(bytes));
			
			result.add(map);
		}
		
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		log.debug("appendWaveSpectrum : {} for {}", timeElapsed, list.size());
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> replaceWaveSpectrum(Category category, SoundVo soundVo, List<TranscriptionDto> list)	throws Exception {
		
		long start = System.currentTimeMillis();
		
		String channelId = Integer.toString( list.get(0).getChannelId() );
		File file = null;
		
		int channelCount = (int) soundVo.getChannelCount();
		if ( channelCount == 2 ) {
			file = diskService.getUploadedFile(category, soundVo.getFileStereoPrefix(), Integer.toString(channelCount), channelId);
		} else {
			file =  getOrCopyWaveFile(category, soundVo.getFileNewNm());
		}
		
		SoundSpectrum spectrum = new SoundSpectrum(file);
		
		List<Map<String, Object>> result = new ArrayList<>();
		
		for( TranscriptionDto vo : list ) {
			
			byte[] bytes = spectrum.writeWaveSvg( vo.getPrevEnd(), vo.getStart(), vo.getEnd(), vo.getNextStart());
			Map<String, Object> map = new HashMap<>();
			map.put("seq", vo.getSeq());
//			map.put("image", Base64.getEncoder().encode(bytes));
			map.put("image", Base64.getEncoder().encodeToString(bytes));
			
			result.add(map);
		}
		
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		log.debug("replaceWaveSpectrum : {} for {}", timeElapsed, list.size());
		
		return result;
	}

}
