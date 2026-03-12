package com.vol.solunote.menu21.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.batch.task.MeetingLauncher;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.Util;
import com.vol.solunote.comm.model.Category;
import com.vol.solunote.comm.service.CommonSteelServiceImpl;

import com.vol.solunote.comm.util.HwpFile;


import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.meeting.MeetingResultVo;
import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;

import com.vol.solunote.repository.meeting.MeetingRepository;
import com.vol.solunote.repository.meeting.MeetingResultRepository;
import com.vol.solunote.repository.meeting.MeetingSpeakerRepository;
import com.vol.solunote.security.SecurityMember;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service("menu21Service")
@Slf4j
public class Menu21ServiceImpl implements Menu21Service {


	@Value("${file.upload.path.meet}")
	private String UPLOAD_PATH;

	@Value("${stt.url}")
	private String sttUrl;

	@Value("${stt.multi-lang:false}")
	private boolean sttMultiLang;

	@Value("${stt.alternate.curl.size:500000000}")
	private long sttAlternateCurlSize;

	@Value("${stt.alternate.curl.command:/usr/bin/curl}")
	private String[] sttAlternateCurlCommand;

	@Value("${stt.alternate.curl.quote:'}")
	private String quote;

	@Value("${stt.confidence:false}")
	private boolean sttConfidence;

	@Value("${summary.url}")
	private String summaryUrl;

	@Value("${summary.url2}")
	private String summaryUrl2;

	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private MeetingResultRepository meetingResultRepository;
	
	@Autowired
	private MeetingSpeakerRepository meetingSpeakerRepository;


	@Autowired
	private CommonSteelServiceImpl commonService;

	@PostConstruct
	private void init() {
		this.quote = "double".equals(this.quote) ? "\"" : "'";
	}

	@Override
	public List<MeetingVo> getList(@NonNull String keyword, String category, int activeMenu, String searchStartDate,
			String searchEndDate, OffsetPageable offsetPageable) throws Exception {
		SecurityMember securityMember = Util.getSessionSecurityMember();
		List<MeetingVo> pages = null;

		if (activeMenu == 3) {
			pages = meetingRepository.getListForTrash(keyword, category, securityMember, searchStartDate, searchEndDate,
					offsetPageable);
		} else {
			pages = meetingRepository.getList(keyword, category, securityMember, activeMenu, searchStartDate, searchEndDate,
					offsetPageable);
		}

		for (MeetingVo vo : pages) {
			vo.setTimeDurationFormatted(
					DurationFormatUtils.formatDuration(Double.valueOf(vo.getTimeDurationMs()).longValue(), "H:mm:ss"));
		}

		return pages;
	}

	@Override
	public List<MeetingResultVo> getMeetResultList(int meetSeq) throws Exception {
		
		List<MeetingResultVo> list = meetingResultRepository.findByMeeting_SeqOrderByStartAsc(meetSeq);

		List<String> colorNumbers = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
				"12", "13", "14", "15", "16", "17", "18", "19", "20");

		Map<String, String> speakerColorMap = new LinkedHashMap<>();

		int index = 0;

		for (MeetingResultVo data : list) {
			long start = (Double.valueOf(data.getStart()).longValue()) * 1000;
			
			data.setStartTimeFormat(DurationFormatUtils.formatDuration(start, "HH:mm:ss"));			
			String speakerName = data.getName();
			
			log.debug(" speakerName = {}", speakerName );

			if (!speakerColorMap.containsKey(speakerName)) {
				// 색상 숫자를 리스트에서 가져와서 매핑
				String color = colorNumbers.get(index);
				speakerColorMap.put(speakerName, color);
				index++;
				if (index > 19) {
					index = 0;
				}
			}

			// 색상 설정
			data.setColor(speakerColorMap.get(speakerName));

			String formattedText = data.getText().replaceAll("([\\.\\?\\!…?!]+)\\s*", "$1<br>");

			data.setText(formattedText);

		}
		return list;
	}

	@Override
	public Map<String, Object> getMeetBySEQ(int meetSeq) {
		return meetingRepository.getMeetBySEQ(meetSeq);
	}

	@Override
	public Map<String, Object> getMeetBySEQAndDuration(int meetSeq) {
		Map<String, Object> map = meetingRepository.getMeetBySEQ(meetSeq);

		int durationSec = (int) Math.round(Double.parseDouble(map.get("time_duration_ms").toString()) / 1000);
		map.put("durationFormat", String.format("%02d:%02d:%02d", (durationSec) / 3600, ((durationSec) % 3600) / 60,
				((durationSec) % 60)));

		return map;
	}

	@Override
	@Transactional
	public void setDeletedAt(int seq) throws Exception {

		boolean flag = deleteWaveFile(seq);

		if (flag == true) {
			meetingRepository.updateDeletedRemovedAtBySeq(seq);
		} else {
			meetingRepository.updateDeletedAtBySeq(seq);
		}
	}

	private boolean deleteWaveFile(int seq) throws Exception {
		int trCount = meetingRepository.changedTransCount(seq);

		if (trCount > 0) {
			log.info("SKIP delete file : meeting seq {} has TB_CS_TRANSACTION data", seq);
			return false;
		}

		meetingRepository.deleteTrans(seq);

		meetingRepository.deleteMeetingResult(seq);

		meetingRepository.deleteMeetingSpeaker(seq);

		meetingRepository.deleteMeetingApi(seq);

		Map<String, Object> map = meetingRepository.readMeetBySeq(seq);

		if (map == null) {
			log.warn("WARNING : meeting file does not exists for seq = {}", seq);
			return true;
		}

		String path = (String) map.get("file_new_nm");
		if (path != null) {
			commonService.removeDiskFile(Category.MEET, path);
		}

		path = (String) map.get("file_conv_nm");
		if (path != null) {
			commonService.removeDiskFile(Category.MEET, path);
		}

		return true;
	}

	@Override
	public void setTrashedAt(int seq) throws Exception
	{
		meetingRepository.updateTrashedAtBySeq(LocalDateTime.now(), seq);
	}

	@Override
	public void setNullTrashedAt(int seq) throws Exception
	{
		meetingRepository.updateTrashedAtBySeq(null, seq);
	}

	@Override
	public void updateSubjectAndUpdatedAtBySeq(int meetSeq, String updateSubject, LocalDateTime updatedAt) throws Exception
	{
		meetingRepository.updateSubjectAndUpdatedAtBySeq(updateSubject, meetSeq, updatedAt);
	}

	@Override
	@Transactional
	public String saveDiarizeAndStt(Map<String, Object> param, MultipartFile file) throws Exception {
		// 2. insert TB_CS_MEETING_API
		meetingRepository.createMeetApi(param);
		String meetSeq = String.valueOf(param.get("seq"));

		return meetSeq;
	}


	@Override
	public String callSummary3(String text) throws Exception {

		Map<String, Object> map = new HashMap<>();
		map.put("content", text);

		Instant start = Instant.now();
		int min = Math.min(400, text.length());
		log.debug("summary started = {} content {}", start.toString(), text.substring(0, min));

		Map<String, Object> result = commonService.restPostData(this.summaryUrl, map);

		Instant finish = Instant.now();
		log.debug("summary ended   = {} data {}", finish.toString(), result.toString());

		String msg = (String) result.get("msg");
		if ("Summarize Success".equals(msg)) {
			return (String) result.get("body");
		} else {
			// summary 에서 에러 발생, rollback 하기 위하여 exception 을 return 함
			throw new RuntimeException("summary3 call error ");
		}
	}

	@Override
	public void updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception 
	{
		meetingRepository.updateRemarkAndUpdatedAtBySeq(updateRemark, updatedAt, meetSeq);
	}

	@Override
	public int updateSpeakerAndUpdatedAtBySeqArr(int[] meetResultSeqArr, String updateSpeakerText, int meetSeq,
			LocalDateTime updatedAt) throws Exception 
	{
		int result = 0;
		MeetingSpeakerVo meetingSpeaker = meetingSpeakerRepository.findByNameAndMeeting_Seq(updateSpeakerText, meetSeq);
		if (meetingSpeaker == null) {
			meetingSpeaker = new MeetingSpeakerVo();
			meetingSpeaker.setName(updateSpeakerText);
			meetingSpeaker.setSeq(meetSeq);
			meetingSpeakerRepository.saveMeetingSpeaker(meetingSpeaker);
			result = 1;
		}
		meetingResultRepository.updateSpeakerBySeqArr(meetingSpeaker.getMeetingSpeakerId(), meetResultSeqArr);
		meetingRepository.updateUpdatedAtBySeq(updatedAt, meetSeq);
		return result;
	}

	@Override
	public List<MeetingSpeakerVo> findSpeakerByMeeting_SEQ(int meetSeq) {
		return meetingSpeakerRepository.findSpeakerByMeeting_Seq(meetSeq);
	}

	@Override
	@Transactional
	public void updateTextAndUpdatedAtByMap(int meetSeq, Map<Integer, String> body, LocalDateTime updatedAt)  throws Exception 
	{
		for (int meetResultSeq : body.keySet()) {
			meetingResultRepository.updateTextBySeq(body.get(meetResultSeq), meetResultSeq);
		}
		meetingRepository.updateUpdatedAtBySeq(updatedAt, meetSeq);
	}

	@Override
	public LocalDateTime createSpeaker(int meetSeq, String speakerText) throws Exception 
	{
		if (!meetingSpeakerRepository.existsByMeeting_SeqAndName(meetSeq, speakerText)) {
			LocalDateTime ldt = LocalDateTime.now();
			meetingRepository.updateUpdatedAtBySeq(ldt, meetSeq);
			MeetingSpeakerVo meetingSpeaker = new MeetingSpeakerVo();  
			meetingSpeaker.setName(speakerText);
			meetingSpeaker.setSeq(meetSeq);			
			meetingSpeakerRepository.saveMeetingSpeaker(meetingSpeaker);
			return ldt;
		}
		return null;
	}

	@Override
	@Transactional
	public LocalDateTime deleteSpeaker(int meetSeq, String speakerText) throws Exception
	{
		if (meetingResultRepository.existsByMeeting_SeqAndMeetingSpeaker_Name(meetSeq, speakerText)) {
			return null;
		} else {
			LocalDateTime ldt = LocalDateTime.now();
			meetingRepository.updateUpdatedAtBySeq(ldt, meetSeq);
			meetingSpeakerRepository.deleteByMeeting_SeqAndName(meetSeq, speakerText);
			return ldt;
		}
	}

	@Override
	public String createTxt(OutputStream out, List<MeetingResultVo> pages, Map<String, Object> meetMap)
			throws Exception 
	{

		String subject = meetMap.get("subject").toString();

		StringBuilder sb = new StringBuilder(1000);
		sb.append("제목: ").append(subject).append('\n').append("작성일: ").append(meetMap.get("created_at").toString())
				.append('\n').append("최종 변경: ").append(meetMap.get("updated_at").toString()).append('\n')
				.append("파일 길이: ").append(meetMap.get("durationFormat").toString()).append('\n').append("\n\n\n");

		if (pages.size() == 0) {
			sb.append("결과가 없습니다.");
		} else {
			for (MeetingResultVo meetingResult : pages) {
				sb.append("화자: ").append(meetingResult.getName()).append('\n')
						.append("내용: ").append(meetingResult.getText().replace("<br>", "\n")).append('\n').append('\n');
			}
			sb.delete(sb.length() - 2, sb.length());
		}

		out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

		return (subject + ".txt");
	}

	@Override
	public void createTxt(HttpServletResponse response, List<MeetingResultVo> pages, Map<String, Object> meetMap,
			String summaryFlag) throws Exception 
	{

		String subject = meetMap.get("subject").toString();

		StringBuilder sb = new StringBuilder(1000);
		sb.append("제목: ").append(subject).append('\n').append("작성일: ").append(meetMap.get("created_at").toString())
				.append('\n').append("최종 변경: ").append(meetMap.get("updated_at").toString()).append('\n')
				.append("파일 길이: ").append(meetMap.get("durationFormat").toString()).append("\n")
//		.append("내용 요약: ").append(meetMap.get("summary").toString()).append('\n')
//		.append("내용 요약2: ").append(meetMap.get("summary2").toString()).append('\n')
				.append("\n\n");

		if (pages.size() == 0) {
			sb.append("결과가 없습니다.");
		} else {
			for (MeetingResultVo meetingResult : pages) {
				sb.append("화자: ").append(meetingResult.getName()).append(" (")
						.append(meetingResult.getStartTimeFormat()).append(")\n").append("내용: ")
						.append(meetingResult.getText().replace("<br>", "\n")).append('\n').append('\n');
			}
			sb.delete(sb.length() - 2, sb.length());
		}

		ServletOutputStream out = response.getOutputStream();
		out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

//		return (subject+".txt");

		response.setContentType("application/octet-stream");
		response.setHeader("filename_base64", Base64.getEncoder().encodeToString((subject + "_회의록.txt").getBytes()));

		out.flush();
		out.close();

	}

	@Override
	public String createSummaryTxt(List<MeetingResultVo> pages, Map<String, Object> meetMap) throws Exception {

		String subject = meetMap.get("subject").toString();

		StringBuilder sb = new StringBuilder(1000);
		sb.append("제목: ").append(subject).append("\n\n");

		if (pages.size() == 0) {
			sb.append("결과가 없습니다.");
		} else {
			for (MeetingResultVo meetingResult : pages) {
				sb.append("화자: ").append(meetingResult.getName()).append("\n")
						.append("내용: ").append(meetingResult.getText()).append('\n').append('\n');
			}
		}

		return sb.toString();
	}

	@Override
	public byte[] getTxt(List<MeetingResultVo> pages, Map<String, Object> meetMap) throws Exception {

		String subject = meetMap.get("subject").toString();

		StringBuilder sb = new StringBuilder(1000);
		sb.append("제목: ").append(subject).append('\n').append("작성일: ").append(meetMap.get("created_at").toString())
				.append('\n').append("최종 변경: ").append(meetMap.get("updated_at").toString()).append('\n')
				.append("파일 길이: ").append(meetMap.get("durationFormat").toString()).append("\n\n").append("내용 요약: ")
				.append(meetMap.get("summary").toString()).append('\n')
//		.append("내용 요약2: ").append(meetMap.get("summary2").toString()).append('\n')
				.append("\n\n\n");

		if (pages.size() == 0) {
			sb.append("결과가 없습니다.");
		} else {
			for (MeetingResultVo meetingResult : pages) {
				sb.append("화자: ").append(meetingResult.getName()).append(" (")
						.append(meetingResult.getStartTimeFormat()).append(")\n").append("내용: ")
						.append(meetingResult.getText().replace("<br>", "\n")).append('\n').append('\n');
			}
			sb.delete(sb.length() - 2, sb.length());
		}

		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public String createXlsx(OutputStream out, List<MeetingResultVo> pages, Map<String, Object> meetMap)
			throws Exception {

		String subject = meetMap.get("subject").toString();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		int rowNum = 0;
		Row row = sheet.createRow(rowNum++);
		Cell cell = null;

		cell = row.createCell(0);
		cell.setCellValue("제목");
		cell = row.createCell(1);
		cell.setCellValue(subject);

		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("작성일");
		cell = row.createCell(1);
		cell.setCellValue(meetMap.get("created_at").toString());

		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("최종 변경");
		cell = row.createCell(1);
		cell.setCellValue(meetMap.get("updated_at").toString());

		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("파일 길이");
		cell = row.createCell(1);
		cell.setCellValue(meetMap.get("durationFormat").toString());

		rowNum += 3;
		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("화자");
		cell = row.createCell(1);
		cell.setCellValue("내용");
		for (MeetingResultVo meetingResult : pages) {
			row = sheet.createRow(rowNum++);
			cell = row.createCell(0);
			cell.setCellValue(meetingResult.getName());
			cell = row.createCell(1);
			cell.setCellValue(meetingResult.getText().replace("<br>", "\n"));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		workbook.close();

		out.write(baos.toByteArray());
		baos.close();
		return (subject + ".xlsx");
	}

	@Override
	public String createDocx(OutputStream out, List<MeetingResultVo> pages, Map<String, Object> meetMap)
			throws Exception {

		String subject = meetMap.get("subject").toString();

		XWPFDocument clsDoc = new XWPFDocument();

		XWPFParagraph xwpfParagraph = clsDoc.createParagraph();
		xwpfParagraph.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun run = xwpfParagraph.createRun();

		run.setText("제목: " + subject);
		run.addBreak();
		run.setText("작성일: " + meetMap.get("created_at").toString());
		run.addBreak();
		run.setText("최종 변경:" + meetMap.get("updated_at").toString());
		run.addBreak();
		run.setText("파일 길이: " + meetMap.get("durationFormat").toString());

		run.addBreak();
		run.addBreak();

		for (MeetingResultVo meetingResult : pages) {
			run.addBreak();
			run.addBreak();
			run.setText("화자: " + meetingResult.getName());
			run.addBreak();
			run.setText("내용: ");

			String[] splitContents = meetingResult.getText().split("<br>|\n");
			for (int i = 0; i < splitContents.length; i++) {
				run.setText(splitContents[i]);
				if (splitContents.length != (i + 1))
					run.addBreak();
			}
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		clsDoc.write(baos);
		clsDoc.close();

		out.write(baos.toByteArray());
		baos.close();
		return subject + ".doc";
	}

	@Override
	public void createXlsx(HttpServletResponse response, List<MeetingResultVo> pages, Map<String, Object> meetMap,
			String flag) throws Exception {

		String subject = meetMap.get("subject").toString();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		int rowNum = 0;
		Row row = sheet.createRow(rowNum++);
		Cell cell = null;

		cell = row.createCell(0);
		cell.setCellValue("제목");
		cell = row.createCell(1);
		cell.setCellValue(subject);

		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("작성일");
		cell = row.createCell(1);
		cell.setCellValue(meetMap.get("created_at").toString());

		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("최종 변경");
		cell = row.createCell(1);
		cell.setCellValue(meetMap.get("updated_at").toString());

		row = sheet.createRow(rowNum++);
		cell = row.createCell(0);
		cell.setCellValue("파일 길이");
		cell = row.createCell(1);
		cell.setCellValue(meetMap.get("durationFormat").toString());

		row = sheet.createRow(rowNum++);

		cell = row.createCell(0);
		cell.setCellValue("화자");

		cell = row.createCell(1);
		cell.setCellValue("Time");

		cell = row.createCell(2);
		cell.setCellValue("내용");

		for (MeetingResultVo meetingResult : pages) {
			row = sheet.createRow(rowNum++);

			cell = row.createCell(0);
			cell.setCellValue(meetingResult.getName());

			cell = row.createCell(1);
			cell.setCellValue(meetingResult.getStartTimeFormat());

			cell = row.createCell(2);
			cell.setCellValue(meetingResult.getText().replace("<br>", "\n"));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		workbook.close();

		ServletOutputStream out = response.getOutputStream();
		out.write(baos.toByteArray());
		baos.close();

		response.setContentType("application/octet-stream");
		response.setHeader("filename_base64", Base64.getEncoder().encodeToString((subject + "_회의록.xlsx").getBytes()));

		out.flush();
		out.close();

	}

	@Override
	public void createDocx(HttpServletResponse response, List<MeetingResultVo> pages, Map<String, Object> meetMap,
			String flag) throws IOException {

		String subject = meetMap.get("subject").toString();

		XWPFDocument clsDoc = new XWPFDocument();

		XWPFParagraph xwpfParagraph = clsDoc.createParagraph();
		xwpfParagraph.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun run = xwpfParagraph.createRun();

		run.setText("제목: " + subject);
		run.addBreak();
		run.setText("작성일: " + meetMap.get("created_at").toString());
		run.addBreak();
		run.setText("최종 변경:" + meetMap.get("updated_at").toString());
		run.addBreak();
		run.setText("파일 길이: " + meetMap.get("durationFormat").toString());
//		run.addBreak();
//		run.addBreak();
//		run.setText( "내용 요약: "+meetMap.get("summary").toString());
//		run.addBreak();
//		run.setText( "내용 요약2: "+meetMap.get("summary2").toString());

		run.addBreak();
		run.addBreak();

		for (MeetingResultVo meetingResult : pages) {
			run.addBreak();
			run.addBreak();
			run.setText("화자: " + meetingResult.getName() + " (" + meetingResult.getStartTimeFormat()
					+ ")");
			run.addBreak();
			run.setText("내용: ");

			String[] splitContents = meetingResult.getText().split("<br>|\n");
			for (int i = 0; i < splitContents.length; i++) {
				run.setText(splitContents[i]);
				if (splitContents.length != (i + 1))
					run.addBreak();
			}
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		clsDoc.write(baos);
		clsDoc.close();

		ServletOutputStream out = response.getOutputStream();
		out.write(baos.toByteArray());
		baos.close();
//		return subject+".doc";

		response.setContentType("application/octet-stream");
		response.setHeader("filename_base64", Base64.getEncoder().encodeToString((subject + "_회의록.doc").getBytes()));

		out.flush();
		out.close();
	}

	@Override
	public void createHwp(HttpServletResponse response, List<MeetingResultVo> pages, Map<String, Object> meetMap,
			String flag) throws Exception {

		HwpFile hwpFile = new HwpFile();

		String subject = meetMap.get("subject").toString();

		hwpFile.addBoldParagraph("제목: " + subject, 0, 2);
		hwpFile.addBoldParagraph("작성일: " + meetMap.get("created_at").toString(), 0, 3);
		hwpFile.addBoldParagraph("최종변경:" + meetMap.get("updated_at").toString(), 0, 4);
		hwpFile.addBoldParagraph("파일길이: " + meetMap.get("durationFormat").toString(), 0, 4);
		hwpFile.addParagraph("");
//		hwpFile.addBoldParagraph("내용요약: " + meetMap.get("summary").toString(), 0, 4);
//		hwpFile.addParagraph("");

		hwpFile.addBoldParagraph("본문: ", 0, 0);
		for (MeetingResultVo meetingResult : pages) {
			hwpFile.addParagraph("");
			hwpFile.addBoldParagraph("화자: " + meetingResult.getName() + " ("
					+ meetingResult.getStartTimeFormat() + ")", 0, 2);

			String[] splitContents = meetingResult.getText().split("<br>|\n");
			for (int i = 0; i < splitContents.length; i++) {
				if (i == 0) {
					hwpFile.addBoldParagraph("내용: " + splitContents[i], 0, 2);
				} else {
					hwpFile.addParagraph(splitContents[i]);
				}
			}
		}

		response.setContentType("application/x-hwp");
		response.setHeader("filename_base64", Base64.getEncoder().encodeToString((subject + "_회의록.hwp").getBytes()));
		response.setHeader("Access-Control-Allow-Headers", "filename_base64");

		ServletOutputStream out = response.getOutputStream();
		hwpFile.toStream(out);

		out.flush();
		out.close();

	}

	@Override
	public void createSummaryTxt(HttpServletResponse response, Map<String, Object> meetMap) throws IOException {
		String subject = meetMap.get("subject").toString();
		String summary = meetMap.get("summary").toString();
		String summary2 = meetMap.get("summary2").toString();
		String summary3 = meetMap.get("summary3").toString();

		StringBuilder sb = new StringBuilder(1000);

		// 항목 순서 정의
		List<String> order = Arrays.asList("주제", "안건", "세부요약", "주요결정사항", "향후일정 및 계획", "키워드");

		List<String> order2 = Arrays.asList("주제", "세부요약", "키워드");

		// summary 값을 파싱하여 각 항목을 Map으로 가져옴
		Map<String, List<String>> parsedSummary = parseSummary(summary, 1);

		// 기본요약 섹션 작성
		sb.append("===== 1. 일반요약 =====").append(System.lineSeparator());
		for (String key : order) {
			sb.append(key).append(":").append(System.lineSeparator());
			List<String> values = parsedSummary.get(key);
			if (values != null) {
				for (String value : values) {
					sb.append("- ").append(value).append(System.lineSeparator());
				}
			}
			sb.append(System.lineSeparator()); // 항목 간 구분
		}

		// summary2 값을 파싱하여 각 항목을 Map으로 가져옴
		Map<String, List<String>> parsedSummary2 = parseSummary2(summary2, 1);

		// 기본요약 섹션 작성
		sb.append("===== 2. 발표요약 =====").append(System.lineSeparator());
		for (String key : order2) {
			sb.append(key).append(":").append(System.lineSeparator());
			List<String> values = parsedSummary2.get(key);
			if (values != null) {
				for (String value : values) {
					sb.append("- ").append(value).append(System.lineSeparator());
				}
			}
			sb.append(System.lineSeparator()); // 항목 간 구분
		}

		// 시간요약 섹션 작성
		sb.append("===== 3. 시간별요약 =====").append(System.lineSeparator());
		Map<String, List<Map<String, Object>>> parsedSummary3 = parseSummary3(summary3);

		for (Map.Entry<String, List<Map<String, Object>>> entry : parsedSummary3.entrySet()) {
			List<Map<String, Object>> blockList = entry.getValue();

			for (Map<String, Object> block : blockList) {
				String time = (String) block.get("time");
				String subjectText = (String) block.get("subject");
				@SuppressWarnings("unchecked")
				List<String> detailedList = (List<String>) block.get("detailed");

				if (time != null) {
					sb.append("시간: ").append(time).append(System.lineSeparator());
				}

				if (subjectText != null) {
					sb.append("주제: ").append(subjectText).append(System.lineSeparator());
				}

				if (detailedList != null) {
					sb.append("세부요약:").append(System.lineSeparator());
					for (String detail : detailedList) {
						sb.append("- ").append(detail).append(System.lineSeparator());
					}
				}
				sb.append(System.lineSeparator()); // 블록 간 구분
			}
		}

		// 파일 출력 로직
		ServletOutputStream out = response.getOutputStream();
		out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

		response.setContentType("application/octet-stream");
		response.setHeader("filename_base64",
				Base64.getEncoder().encodeToString((subject + "_요약.txt").getBytes(StandardCharsets.UTF_8)));

		out.flush();
		out.close();
	}

	@Override
	public void createSummaryXlsx(HttpServletResponse response, Map<String, Object> meetMap) throws IOException {
		String subject = meetMap.get("subject").toString();
		String summary = meetMap.get("summary").toString();
		String summary2 = meetMap.get("summary2").toString();
		String summary3 = meetMap.get("summary3").toString();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		XSSFCellStyle boldStyle = workbook.createCellStyle();
		XSSFFont boldFont = workbook.createFont();
		boldFont.setBold(true); // 굵은 글씨 설정
		boldStyle.setFont(boldFont);

		// 항목 순서 정의
		List<String> order = Arrays.asList("주제", "안건", "세부요약", "주요결정사항", "향후일정 및 계획", "키워드");

		List<String> order2 = Arrays.asList("주제", "세부요약", "키워드");

		// summary 값을 파싱하여 각 항목을 Map으로 가져옴
		Map<String, List<String>> parsedSummary = parseSummary(summary, 1);

		// summary 값을 파싱하여 각 항목을 Map으로 가져옴
		Map<String, List<String>> parsedSummary2 = parseSummary2(summary2, 1);

		// summary2 값을 파싱하여 Map<String, List<Map<String, Object>>> 형태로 가져옴
		Map<String, List<Map<String, Object>>> parsedSummary3 = parseSummary3(summary3);

		int rowNum = 0;

		// 기본요약 섹션 추가
		XSSFRow basicTitleRow = sheet.createRow(rowNum++);
		XSSFCell basicTitleCell = basicTitleRow.createCell(0);
		basicTitleCell.setCellValue("1.일반요약");
		basicTitleCell.setCellStyle(boldStyle); // 스타일 적용

		for (String key : order) {
			XSSFRow row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(key + ":");

			List<String> values = parsedSummary.get(key);
			if (values != null) {
				for (String value : values) {
					XSSFRow valueRow = sheet.createRow(rowNum++);
					valueRow.createCell(0).setCellValue("- " + value);
				}
			}
			rowNum++; // 항목 간 구분을 위한 빈 줄
		}

		XSSFRow presenTitleRow = sheet.createRow(rowNum++);
		XSSFCell presenTitleCell = presenTitleRow.createCell(0);
		presenTitleCell.setCellValue("2.발표요약");
		presenTitleCell.setCellStyle(boldStyle); // 스타일 적용

		for (String key : order2) {
			XSSFRow row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(key + ":");

			List<String> values = parsedSummary2.get(key);
			if (values != null) {
				for (String value : values) {
					XSSFRow valueRow = sheet.createRow(rowNum++);
					valueRow.createCell(0).setCellValue("- " + value);
				}
			}
			rowNum++; // 항목 간 구분을 위한 빈 줄
		}

		// 시간요약 섹션 추가
		XSSFRow timeTitleRow = sheet.createRow(rowNum++);
		XSSFCell timeTitleCell = timeTitleRow.createCell(0);
		timeTitleCell.setCellValue("3.시간별 요약");
		timeTitleCell.setCellStyle(boldStyle); // 스타일 적용

		for (Map.Entry<String, List<Map<String, Object>>> entry : parsedSummary3.entrySet()) {
			List<Map<String, Object>> blockList = entry.getValue();

			for (Map<String, Object> block : blockList) {
				String time = (String) block.get("time");
				String subjectText = (String) block.get("subject");
				@SuppressWarnings("unchecked")
				List<String> detailedList = (List<String>) block.get("detailed");

				if (time != null) {
					XSSFRow timeRow = sheet.createRow(rowNum++);
					timeRow.createCell(0).setCellValue("시간: " + time);
				}

				if (subjectText != null) {
					XSSFRow subjectRow = sheet.createRow(rowNum++);
					subjectRow.createCell(0).setCellValue("주제: " + subjectText);
				}

				if (detailedList != null) {
					XSSFRow detailedTitleRow = sheet.createRow(rowNum++);
					detailedTitleRow.createCell(0).setCellValue("세부요약:");

					for (String detail : detailedList) {
						XSSFRow detailRow = sheet.createRow(rowNum++);
						detailRow.createCell(0).setCellValue("- " + detail);
					}
				}
				rowNum++; // 블록 간 구분을 위한 빈 줄
			}
		}

		// 파일 출력 로직
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		workbook.close();

		ServletOutputStream out = response.getOutputStream();
		out.write(baos.toByteArray());

		response.setContentType("application/octet-stream");
		response.setHeader("filename_base64", Base64.getEncoder().encodeToString((subject + "_요약.xlsx").getBytes()));

		out.flush();
		out.close();
	}

	@Override
	public void createSummaryDocx(HttpServletResponse response, Map<String, Object> meetMap) throws IOException {
		String subject = meetMap.get("subject").toString();
		String summary = meetMap.get("summary").toString();
		String summary2 = meetMap.get("summary2").toString();
		String summary3 = meetMap.get("summary3").toString();

		XWPFDocument clsDoc = new XWPFDocument();

		List<String> order = Arrays.asList("주제", "안건", "세부요약", "주요결정사항", "향후일정 및 계획", "키워드");
		List<String> order2 = Arrays.asList("주제", "세부요약", "키워드");

		Map<String, List<String>> parsedSummary = parseSummary(summary, 1);
		Map<String, List<String>> parsedSummary2 = parseSummary2(summary2, 1);
		Map<String, List<Map<String, Object>>> parsedSummary3 = parseSummary3(summary3);

		createSection(clsDoc, "1. 일반요약", order, parsedSummary);
		createSection(clsDoc, "2. 발표요약", order2, parsedSummary2);
		createTimeSummarySection(clsDoc, "3. 시간별 요약", parsedSummary3);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		clsDoc.write(baos);
		clsDoc.close();

		ServletOutputStream out = response.getOutputStream();
		out.write(baos.toByteArray());
		baos.close();

		response.setContentType("application/octet-stream");
		response.setHeader("filename_base64",
				Base64.getEncoder().encodeToString((subject + "_요약.docx").getBytes(StandardCharsets.UTF_8)));

		out.flush();
		out.close();
	}

	private void createSection(XWPFDocument doc, String title, List<String> order,
			Map<String, List<String>> parsedSummary) {
		XWPFParagraph titleParagraph = doc.createParagraph();
		titleParagraph.setSpacingBetween(1.5);
		titleParagraph.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun titleRun = titleParagraph.createRun();
		titleRun.setText(title);
		titleRun.setFontFamily("맑은 고딕");
		titleRun.setFontSize(12);
		titleRun.setBold(true);
		titleRun.addBreak();

		for (String key : order) {
			XWPFParagraph paragraph = doc.createParagraph();
			paragraph.setSpacingBetween(1.5);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun keyRun = paragraph.createRun();
			keyRun.setText(key + ":");
			keyRun.setFontFamily("맑은 고딕");
			keyRun.setFontSize(10);
			keyRun.addBreak();

			List<String> values = parsedSummary.get(key);
			if (values != null) {
				for (String item : values) {
					XWPFRun valueRun = paragraph.createRun();
					valueRun.setText("- " + item);
					valueRun.setFontFamily("맑은 고딕");
					valueRun.setFontSize(10);
					valueRun.addBreak();
				}
			}
		}
	}

	private void createTimeSummarySection(XWPFDocument doc, String title,
			Map<String, List<Map<String, Object>>> parsedSummary3) {
		XWPFParagraph titleParagraph = doc.createParagraph();
		titleParagraph.setSpacingBetween(1.5);
		titleParagraph.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun titleRun = titleParagraph.createRun();
		titleRun.setText(title);
		titleRun.setFontFamily("맑은 고딕");
		titleRun.setFontSize(12);
		titleRun.setBold(true);
		titleRun.addBreak();

		for (Map.Entry<String, List<Map<String, Object>>> entry : parsedSummary3.entrySet()) {
			List<Map<String, Object>> blockList = entry.getValue();

			for (Map<String, Object> block : blockList) {
				String time = (String) block.get("time");
				String subjectText = (String) block.get("subject");
				@SuppressWarnings("unchecked")
				List<String> detailedList = (List<String>) block.get("detailed");

				if (time != null) {
					XWPFParagraph timeParagraph = doc.createParagraph();
					timeParagraph.setSpacingBetween(1.5);
					timeParagraph.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun timeRun = timeParagraph.createRun();
					timeRun.setText("시간: " + time);
					timeRun.setFontFamily("맑은 고딕");
					timeRun.setFontSize(10);
					timeRun.addBreak();

				}

				if (subjectText != null) {
					XWPFParagraph subjectParagraph = doc.createParagraph();
					subjectParagraph.setSpacingBetween(1.5);
					subjectParagraph.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun subjectRun = subjectParagraph.createRun();
					subjectRun.setText("주제: " + subjectText);
					subjectRun.setFontFamily("맑은 고딕");
					subjectRun.setFontSize(10);
					subjectRun.addBreak();
				}

				if (detailedList != null) {
					XWPFParagraph detailParagraph = doc.createParagraph();
					detailParagraph.setSpacingBetween(1.5);
					detailParagraph.setAlignment(ParagraphAlignment.LEFT);
					XWPFRun detailRun = detailParagraph.createRun();
					detailRun.setText("세부요약:");
					detailRun.setFontFamily("맑은 고딕");
					detailRun.setFontSize(10);
					detailRun.addBreak();

					for (String detail : detailedList) {
						XWPFRun detailItemRun = detailParagraph.createRun();
						detailItemRun.setText("- " + detail);
						detailItemRun.setFontFamily("맑은 고딕");
						detailItemRun.setFontSize(10);
						detailItemRun.addBreak();
					}
				}
			}
		}
	}

	@Override
	public void createSummaryHwp(HttpServletResponse response, Map<String, Object> meetMap) throws Exception {
		HwpFile hwpFile = new HwpFile();

		String subject = meetMap.get("subject").toString();
		String summary = meetMap.get("summary").toString();
		String summary2 = meetMap.get("summary2").toString();
		String summary3 = meetMap.get("summary3").toString();

		// summary 값을 파싱하여 각 항목을 Map으로 가져옴
		Map<String, List<String>> parsedSummary = parseSummary(summary, 1);

		Map<String, List<String>> parsedSummary2 = parseSummary2(summary2, 1);

		// 항목 순서 정의
		List<String> order = Arrays.asList("주제", "안건", "세부요약", "주요결정사항", "향후일정 및 계획", "키워드");

		List<String> order2 = Arrays.asList("주제", "세부요약", "키워드");

		// 기본요약 섹션 추가
		hwpFile.addBoldParagraphAll("1.일반 요약");
		for (String key : order) {
			List<String> valueList = parsedSummary.get(key);

			// 각 항목 제목 추가
			hwpFile.addParagraph(key + ":");

			// 항목에 해당하는 값을 추가
			if (valueList != null) {
				for (String item : valueList) {
					hwpFile.addParagraph("- " + item);
				}
			} else {
				hwpFile.addParagraph("- 값 없음"); // null인 경우 처리
			}

			// 항목 간 구분을 위해 빈 줄 추가
			hwpFile.addParagraph("");
		}

		hwpFile.addParagraph("");

		// 기본요약 섹션 추가
		hwpFile.addBoldParagraphAll("2.발표 요약");
		for (String key : order2) {
			List<String> valueList = parsedSummary2.get(key);

			// 각 항목 제목 추가
			hwpFile.addParagraph(key + ":");

			// 항목에 해당하는 값을 추가
			if (valueList != null) {
				for (String item : valueList) {
					hwpFile.addParagraph("- " + item);
				}
			} else {
				hwpFile.addParagraph("- 값 없음"); // null인 경우 처리
			}

			// 항목 간 구분을 위해 빈 줄 추가
			hwpFile.addParagraph("");
		}

		hwpFile.addParagraph("");

		// 시간요약 섹션 추가
		hwpFile.addBoldParagraphAll("3.시간별요약");
		Map<String, List<Map<String, Object>>> parsedSummary3 = parseSummary3(summary3);

		for (Map.Entry<String, List<Map<String, Object>>> entry : parsedSummary3.entrySet()) {
			List<Map<String, Object>> blockList = entry.getValue();

			for (Map<String, Object> block : blockList) {
				String time = (String) block.get("time");
				String subjectText = (String) block.get("subject");
				@SuppressWarnings("unchecked")
				List<String> detailedList = (List<String>) block.get("detailed");

				if (time != null) {
					hwpFile.addParagraph("시간: " + time);
				}

				if (subjectText != null) {
					hwpFile.addParagraph("주제: " + subjectText);
				}

				if (detailedList != null) {
					hwpFile.addParagraph("세부요약:");
					for (String detail : detailedList) {
						hwpFile.addParagraph("- " + detail);
					}
				}

				// 블록 간 구분을 위해 빈 줄 추가
				hwpFile.addParagraph("");
			}
		}

		// HWP 파일 출력
		response.setContentType("application/x-hwp");
		response.setHeader("filename_base64",
				Base64.getEncoder().encodeToString((subject + "_요약.hwp").getBytes(StandardCharsets.UTF_8)));
		response.setHeader("Access-Control-Allow-Headers", "filename_base64");

		ServletOutputStream out = response.getOutputStream();
		hwpFile.toStream(out);

		out.flush();
		out.close();
	}

	@Override
	public void createFileDownload(HttpServletResponse response, Map<String, Object> meetMap) throws Exception {

		String subject = meetMap.get("subject").toString();

		String fileNm = meetMap.get("file_new_nm").toString(); // 파일명 가져오기
		byte[] all = Files.readAllBytes(commonService.getUploadPath(fileNm)); // 파일 읽기

		// 파일 확장자에 따른 Content-Type 설정
		String fileExtension = fileNm.substring(fileNm.lastIndexOf(".") + 1).toLowerCase();

		String mimeType;
		switch (fileExtension) {
		case "ogg":
			mimeType = "audio/ogg";
			break;
		case "mp3":
			mimeType = "audio/mpeg";
			break;
		case "wav":
			mimeType = "audio/wav";
			break;
		default:
			mimeType = "application/octet-stream"; // 기본 값
		}

		// 파일명 인코딩: 주제(subject)와 확장자를 포함한 파일명 설정
		String encodedFileName = Base64.getEncoder().encodeToString((subject + "." + fileExtension).getBytes("UTF-8"));

		// 응답 헤더 설정
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + URLEncoder.encode(subject + "." + fileExtension, "UTF-8") + "\"");
		response.setHeader("filename_base64", encodedFileName); // Base64 인코딩된 파일명 설정
		response.setContentLength(all.length);

		// 파일 전송
		ServletOutputStream out = response.getOutputStream();
		out.write(all);
		out.flush();
		out.close();

	}

	@Override
	public List<MeetingVo> getMeetApi(MeetingVo vo) throws Exception {
		return meetingRepository.getMeetApi(vo);
	}

	@Override
	public List<MeetingVo> getMeetApiFromSeq(MeetingVo vo) throws Exception {
		return meetingRepository.getMeetApiFromSeq(vo);
	}

	@Override
	public List<MeetingVo> getMeetApiForce(MeetingVo vo) throws Exception {
		return meetingRepository.getMeetApiForce(vo);
	}

	@Transactional
	private void updateMeetApi(MeetingVo vo) throws Exception {
		vo.setStatus(MeetingLauncher.API_STATUS_NOT_FOUND);
		meetingRepository.updateMeetApi(vo);
	}
	
	@Override
	public List<Map<String, Object>> getMeettingRemoveCandiate(int term) throws Exception {

		return meetingRepository.getMeettingRemoveCandiate(term);
	}

	@Override
	public String summary(Integer seq, Integer summaryType) throws Exception {

		List<MeetingResultVo> meetingResultList = getMeetResultList(seq);

		StringBuilder resultString = new StringBuilder();

		StringBuilder speakerString = new StringBuilder();

		if (summaryType == 3) {

			for (MeetingResultVo meetingResult : meetingResultList) {

				String speakerName = meetingResult.getName();
				String startTime = meetingResult.getStartTimeFormat();
				String text = meetingResult.getText();

				int index = 0;
				while (index < text.length()) {

					String chunk = text.substring(index, Math.min(index + 500, text.length()));

					resultString.append("화자: ").append(speakerName).append("\n");
					resultString.append("시간: ").append(startTime).append("\n");
					resultString.append("내용: ").append(chunk).append("\n");
					resultString.append("\n");

					index += 500;
				}
			}

		} else {
			for (MeetingResultVo meetingResult : meetingResultList) {

				resultString.append("화자: ").append(meetingResult.getName()).append("\n");
				resultString.append("내용: ").append(meetingResult.getText()).append("\n");
				resultString.append("\n"); // 각 발언 사이에 빈 줄을 추가하여 구분

			}

		}

		String speakerResult = speakerString.toString();

		String reResult = resultString.toString();

		String summId = summaryToApi(speakerResult, reResult, summaryType);

		Map<String, Object> param = new HashMap<>();

		param.put("seq", seq);
		param.put("summId", summId);
		param.put("summaryType", summaryType);
		meetingRepository.updateSummaryId(param);

		return summId;

	}

	// summaryStatus조회
	@Override
	public Map<String, Object> summaryStatusApi(Integer seq, String summaryId, Integer summaryType) throws Exception {

		Instant start = Instant.now();

		log.debug("summary stats start   = {} data {}", start.toString());
		log.debug("summaryStatusApi seq: {}, summaryId: {}", seq, summaryId);

		String url = this.summaryUrl2 + summaryId;

		Map<String, Object> result = commonService.restGetData(url);

		Instant finish = Instant.now();
		log.debug("summary stats ended = {} data {}", finish, result);

		String status = (String) result.get("status");

		Map<String, Object> param = new HashMap<>();

		// 나중에 이전 status랑 비교해서 업데이트 쿼리 날리게끔
		param.put("seq", seq);
		param.put("summaryType", summaryType);

		if (status.equals("SUCCESS")) {
			String summary = (String) result.get("result");
			param.put("status", status);
			param.put("summary", summary);
			meetingRepository.updateSummarySuccess(param);
		} else if (status.equals("PENDING")) {
			param.put("status", status);
			meetingRepository.updateSummaryStatus(param);
		} else if (status.equals("STARTED")) {
			param.put("status", status);
			meetingRepository.updateSummaryStatus(param);
		} else {
			param.put("status", "FAILURE");
			meetingRepository.updateSummaryFAILUER(param);
		}

		return param;

	}

	private String summaryToApi(String speakerResult, String reResult, Integer summaryType) throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("speakers", speakerResult);
		map.put("content", reResult);

		Instant start = Instant.now();
		int min = Math.min(400, reResult.length());
		log.debug("summary started = {} content {}", start.toString(), reResult.substring(0, min));

		String url;

		if (summaryType == 3) {
			url = this.summaryUrl + "timestamp";
		} else if (summaryType == 2) {
			url = this.summaryUrl + "presentation";
		} else {
			url = this.summaryUrl + "nomarl";
		}

		Map<String, Object> result = commonService.restPostData(url, map);

		Instant finish = Instant.now();
		log.debug("summary ended   = {} data {}", finish.toString(), result.toString());

		String msg = (String) result.get("msg");
		if ("Summarize Success".equals(msg)) {
			return (String) result.get("body");
		} else {
			// summary 에서 에러 발생, rollback 하기 위하여 exception 을 return 함
			throw new RuntimeException("summary3 call error ");
		}
	}

	public Map<String, List<String>> parseSummary(String summary, int down) {
		Map<String, List<String>> summaryMap = new HashMap<>();

		if (summary == null || summary.isEmpty()) {
			summaryMap.put("subject", new ArrayList<>());
			summaryMap.put("agenda", new ArrayList<>());
			summaryMap.put("detailed", new ArrayList<>());
			summaryMap.put("majorDecision", new ArrayList<>());
			summaryMap.put("plan", new ArrayList<>());
			summaryMap.put("keyword", new ArrayList<>());
			return summaryMap;
		}

		summary = addDefaultValues(summary);

		if (down == 1) {
			summaryMap.put("주제", processSection(summary, "주제:", "안건:", false));
			summaryMap.put("안건", processSection(summary, "안건:", "세부요약:", true));
			summaryMap.put("세부요약", processSection(summary, "세부요약:", "주요결정사항:", true));
			summaryMap.put("주요결정사항", processSection(summary, "주요결정사항:", "향후일정:", true));
			summaryMap.put("향후일정 및 계획", processSection(summary, "향후일정:", "키워드:", true));
			summaryMap.put("키워드", processSection(summary, "키워드:", "", true));
		} else {
			if (summary.equals("요약하려는 글의 길이가 짧습니다.")) {
				summary = "주제: 안건: 세부요약: 주요결정사항: 향후일정: 키워드:";
			}

			summaryMap.put("subject", processSection(summary, "주제:", "안건:", false));
			summaryMap.put("agenda", processSection(summary, "안건:", "세부요약:", true));
			summaryMap.put("detailed", processSection(summary, "세부요약:", "주요결정사항:", true));
			summaryMap.put("majorDecision", processSection(summary, "주요결정사항:", "향후일정:", true));
			summaryMap.put("plan", processSection(summary, "향후일정:", "키워드:", true));
			summaryMap.put("keyword", processSection(summary, "키워드:", "", true));
		}

		return summaryMap;

	}

	private List<String> processSection(String data, String start, String end, boolean emptyReturn) {
		String value = extractValue(data, start, end);
		// '-'를 기준으로 나누되, 비어있지 않으면 리스트에 담기
		List<String> result = new ArrayList<>();

		if (!value.isEmpty()) {
			result.addAll(splitByDash(value));
		} else {
			result.add(start.replace(":", "") + " 없음"); // 값이 없을 경우 "없음" 추가
		}

		if (emptyReturn == true && String.join("", result).equals(data)) {
			result = new ArrayList<>();
		}

		return result;
	}

	// "-"를 기준으로 문자열을 분리해 리스트로 반환하는 메서드
	private List<String> splitByDash(String data) {
		return Arrays.stream(data.split("-")).map(String::trim).filter(s -> !s.isEmpty()) // 빈 문자열은 제외
				.collect(Collectors.toList()); // Collectors로 리스트를 반환
	}

	private String extractValue(String data, String start, String end) {
		int startIndex = data.indexOf(start);

		if (startIndex < 0) {
			return data.trim();
		} else {
			startIndex += start.length();
			int endIndex = data.indexOf(end);
			if (endIndex < 0 || startIndex > endIndex) {
				endIndex = data.length();
			}
			return data.substring(startIndex, endIndex).trim();
		}

	}

	private String addDefaultValues(String summary) {

		summary = summary.replaceAll("주제 :", "주제:");
		summary = summary.replaceAll("안건 :", "안건:");
		summary = summary.replaceAll("세부요약 :", "세부요약:");
		summary = summary.replaceAll("주요결정사항 :", "주요결정사항:");
		summary = summary.replaceAll("향후일정 :", "향후일정:");
		summary = summary.replaceAll("키워드 :", "키워드:");
		summary = summary.replaceAll("향후일정 및 계계획:", "향후일정 및 계획:");
		// 필드 확인 후 없으면 기본값 추가
		if (!summary.contains("주제:")) {
			summary += " 주제:";
		}
		if (!summary.contains("안건:")) {
			summary += " 안건:";
		}
		if (!summary.contains("세부요약:")) {
			summary += " 세부요약:";
		}
		if (!summary.contains("주요결정사항:")) {
			summary += " 주요결정사항:";
		}
		if (!summary.contains("향후일정:")) {
			summary += " 향후일정:";
		}
		if (!summary.contains("키워드:")) {
			summary += " 키워드:";
		}

		return summary;
	}

	public Map<String, List<String>> parseSummary2(String summary, int down) {
		Map<String, List<String>> summaryMap = new HashMap<>();

		if (summary == null || summary.isEmpty()) {
			summaryMap.put("subject", new ArrayList<>());
			summaryMap.put("detailed", new ArrayList<>());
			summaryMap.put("keyword", new ArrayList<>());
			return summaryMap;
		}

		summary = addDefaultValues2(summary);

		if (down == 1) {
			summaryMap.put("주제", processSection(summary, "주제:", "세부요약:", false));
			summaryMap.put("세부요약", processSection(summary, "세부요약:", "키워드:", true));
			summaryMap.put("키워드", processSection(summary, "키워드:", "", true));
		} else {
			if (summary.equals("요약하려는 글의 길이가 짧습니다.")) {
				summary = "주제: 세부요약: 키워드:";
			}

			summaryMap.put("subject", processSection(summary, "주제:", "세부요약:", false));
			summaryMap.put("detailed", processSection(summary, "세부요약:", "키워드:", true));
			summaryMap.put("keyword", processSection(summary, "키워드:", "", true));
		}

		return summaryMap;

	}

	private String addDefaultValues2(String summary) {

		summary = summary.replaceAll("주제 :", "주제:");

		summary = summary.replaceAll("세부요약 :", "세부요약:");

		summary = summary.replaceAll("키워드 :", "키워드:");

		// 필드 확인 후 없으면 기본값 추가
		if (!summary.contains("주제:")) {
			summary += " 주제:";
		}
		if (!summary.contains("세부요약:")) {
			summary += " 세부요약:";
		}

		if (!summary.contains("키워드:")) {
			summary += " 키워드:";
		}

		return summary;
	}

	public Map<String, List<Map<String, Object>>> parseSummary3(String summary) {
		Map<String, List<Map<String, Object>>> summaryMap = new HashMap<>();

		if (summary == null || summary.isEmpty()) {
			Map<String, Object> emptyBlock = new HashMap<>();
			emptyBlock.put("time", "");
			emptyBlock.put("subject", "");
			emptyBlock.put("detailed", Collections.singletonList("시간별 요약이 필요합니다.")); // 리스트로 설정
			summaryMap.put("blocks", Collections.singletonList(emptyBlock));
			return summaryMap;
		}

		if (summary.equals("요약하려는 글의 길이가 짧습니다.")) {
			summary = "주제: 시간: 세부요약: ";
		}

		summary = summary.replaceAll("주제 :", "주제:");
		summary = summary.replaceAll("시간 :", "시간:");
		summary = summary.replaceAll("세부요약 :", "세부요약:");

		String[] blocks = summary.split("시간:");
		List<Map<String, Object>> processedBlocks = new ArrayList<>();

		for (String block : blocks) {
			if (block.trim().isEmpty())
				continue;

			Map<String, Object> processedBlock = new HashMap<>();

			String time = extractValueblock(block, "", "주제:");
			processedBlock.put("time", (time == null || time.isEmpty()) ? "시간 없음" : time);

			String subject = extractValueblock(block, "주제:", "세부요약:");
			processedBlock.put("subject", (subject == null || subject.isEmpty()) ? "주제 없음" : subject);

			String detailed = extractValueblock(block, "세부요약:", "");
			List<String> detailsList;
			if (detailed.isEmpty()) {
				detailsList = new ArrayList<>();
			} else if (detailed.contains("-") || detailed.contains("*")) {
				detailsList = Arrays.stream(detailed.split("[*-]")).map(String::trim).filter(s -> !s.isEmpty())
						.collect(Collectors.toList());
			} else {
				detailsList = Collections.singletonList(detailed.trim());
			}

			processedBlock.put("detailed", detailsList);

			processedBlocks.add(processedBlock);
		}

		summaryMap.put("blocks", processedBlocks);
		return summaryMap;
	}

	private String extractValueblock(String data, String start, String end) {
		int startIndex = start.isEmpty() ? 0 : data.indexOf(start);
		if (startIndex < 0)
			return ""; // 시작 키워드가 없으면 빈 문자열 반환

		startIndex += start.isEmpty() ? 0 : start.length();
		int endIndex = end.isEmpty() ? data.length() : data.indexOf(end, startIndex);

		if (endIndex < 0 || startIndex > endIndex)
			endIndex = data.length();
		return data.substring(startIndex, endIndex).trim();
	}

	@Override
	public void summaryUpdate(String result, int meetSeq, int summaryType) throws Exception {

		meetingRepository.summaryUpdate(result, meetSeq, summaryType);

	}

	@Override
	public Map<String, Object> readMeetBySeq(int seq) throws Exception {
		return meetingRepository.readMeetBySeq(seq);
	}

}
