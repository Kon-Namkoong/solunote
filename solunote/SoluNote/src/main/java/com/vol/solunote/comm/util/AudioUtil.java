package com.vol.solunote.comm.util;

import java.io.File;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioUtil {
	
	public static void copyAudioX(File file,  OutputStream out, double startSecond, double endSecond) throws Exception {
		
		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
		AudioFormat format = fileFormat.getFormat();
		
		double duration = endSecond - startSecond;
		long framesOfAudioToCopy = (long)(duration * format.getFrameRate());
		
		try ( AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
				AudioInputStream shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);	) {
			
			double bytesPerSecond = format.getFrameSize() * format.getFrameRate();
			inputStream.skip((long)(startSecond * bytesPerSecond));
			
			AudioSystem.write(shortenedStream, fileFormat.getType(), out);
			
		} catch (Exception e) {
			throw e;
		} 
		
//		AudioInputStream inputStream = null;
//		AudioInputStream shortenedStream = null;
//		try {
////			File file = new File(sourceFileName);
//			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
//			AudioFormat format = fileFormat.getFormat();
//			
//			inputStream = AudioSystem.getAudioInputStream(file);
//			double bytesPerSecond = format.getFrameSize() * format.getFrameRate();
//			
//			inputStream.skip((long)(startSecond * bytesPerSecond));
//			
//			double duration = endSecond - startSecond;
//			
//			long framesOfAudioToCopy = (long)(duration * format.getFrameRate());
//			
//			shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
//			AudioSystem.write(shortenedStream, fileFormat.getType(), out);
//			
//		} catch (Exception e) {
//			println(e);
//		} finally {
//			if (inputStream != null)
//				try {
//					inputStream.close();
//				} catch (Exception e) {
//					println(e);
//				}
//			if (shortenedStream != null)
//				try {
//					out.close();
//				} catch (Exception e) {
//					println(e);
//				}
//		}
	}
	
	public static void println(Object o) {
		System.out.println(o);
	}

	public static void print(Object o) {
		System.out.print(o);
	}


}

