package com.vol.solunote.comm.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import jakarta.servlet.ServletOutputStream;
import org.springframework.core.io.ClassPathResource;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.charshape.ParaCharShape;
import kr.dogfoot.hwplib.object.bodytext.paragraph.header.ParaHeader;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.LineSegItem;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.ParaLineSeg;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.object.docinfo.CharShape;
import kr.dogfoot.hwplib.object.docinfo.FaceName;
import kr.dogfoot.hwplib.object.docinfo.charshape.BorderType2;
import kr.dogfoot.hwplib.object.docinfo.charshape.OutterLineSort;
import kr.dogfoot.hwplib.object.docinfo.charshape.ShadowSort;
import kr.dogfoot.hwplib.object.docinfo.charshape.UnderLineSort;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.writer.HWPWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class HwpFile {
    
    private static final String BLANK_FILE = "blank.hwp";
    private static final int FONT_POINT = 10; // 보통 10pt가 기본
    
    private HWPFile hwpFile;
    private int charShapeIndexForNormal;
    private int charShapeIndexForBold;
    private int faceNameIndexForBatang;

    public HwpFile() throws Exception {
        // [수정] JAR 배포 환경에서도 작동하도록 getInputStream() 사용
        ClassPathResource resource = new ClassPathResource(BLANK_FILE);
        try (InputStream is = resource.getInputStream()) {
            this.hwpFile = HWPReader.fromInputStream(is);
        }
        
        faceNameIndexForBatang = createFaceNameForBatang();
        charShapeIndexForNormal = createCharShape(false);
        charShapeIndexForBold = createCharShape(true);
    }
    
    public void addParagraph(String text) {
        Paragraph p = this.hwpFile.getBodyText().getSectionList().get(0).addNewParagraph();
        setParaHeader(p);
        setParaText(p, text);
        setParaCharShape(p, -1, -1);
        setParaLineSeg(p);
    }
    
    public void addBoldParagraph(String text, int start, int length) {
        Paragraph p = this.hwpFile.getBodyText().getSectionList().get(0).addNewParagraph();
        setParaHeader(p);
        setParaText(p, text);
        setParaCharShape(p, start, length);
        setParaLineSeg(p);
    }
    
    public void addBoldParagraphAll(String text) {
        addBoldParagraph(text, 0, text.length());
    }   
    
    public void toStream(ServletOutputStream out) throws Exception {
        // [수정] ByteArrayOutputStream을 거치지 않고 바로 ServletOutputStream으로 출력 (메모리 효율)
        HWPWriter.toStream(this.hwpFile, out);
        out.flush();
    }

    private int createFaceNameForBatang() {
        FaceName fn;
        
        fn = this.hwpFile.getDocInfo().addNewHangulFaceName(); setFaceNameForBatang(fn);
        fn = this.hwpFile.getDocInfo().addNewEnglishFaceName(); setFaceNameForBatang(fn);
        fn = this.hwpFile.getDocInfo().addNewHanjaFaceName(); setFaceNameForBatang(fn);
        fn = this.hwpFile.getDocInfo().addNewJapaneseFaceName(); setFaceNameForBatang(fn);
        fn = this.hwpFile.getDocInfo().addNewEtcFaceName(); setFaceNameForBatang(fn);
        fn = this.hwpFile.getDocInfo().addNewSymbolFaceName(); setFaceNameForBatang(fn);
        fn = this.hwpFile.getDocInfo().addNewUserFaceName(); setFaceNameForBatang(fn);

        return this.hwpFile.getDocInfo().getHangulFaceNameList().size() - 1;
    }
    
    private void setFaceNameForBatang(FaceName fn) {
        fn.getProperty().setHasBaseFont(false);
        fn.getProperty().setHasFontInfo(false);
        fn.getProperty().setHasSubstituteFont(false);
        fn.setName("바탕");
    }

    private int createCharShape(boolean bold) {
        CharShape cs = this.hwpFile.getDocInfo().addNewCharShape();
        cs.getFaceNameIds().setForAll(faceNameIndexForBatang);

        cs.getRatios().setForAll((short) 100);
        cs.getCharSpaces().setForAll((byte) 0);
        cs.getRelativeSizes().setForAll((short) 100);
        cs.getCharOffsets().setForAll((byte) 0);
        cs.setBaseSize(ptToBaseSize(FONT_POINT));

        cs.getProperty().setBold(bold);
        cs.getProperty().setUnderLineSort(UnderLineSort.None);
        cs.getProperty().setUnderLineShape(BorderType2.Solid);
        cs.getProperty().setOutterLineSort(OutterLineSort.None);
        cs.getProperty().setShadowSort(ShadowSort.None);
        cs.getCharColor().setValue(0x00000000);
        cs.getShadeColor().setValue(-1);

        return this.hwpFile.getDocInfo().getCharShapeList().size() - 1;
    }

    private int ptToBaseSize(int pt) {
        return pt * 100;
    }

    private void setParaHeader(Paragraph p) {
        ParaHeader ph = p.getHeader();
        ph.setLastInList(true);
        ph.setParaShapeId(1); // 기본 문단 모양 사용
        ph.setStyleId((short) 1);
    }

    private void setParaText(Paragraph p, String text) {
        p.createText();
        ParaText pt = p.getText();
        try {
            pt.addString(text);
        } catch (UnsupportedEncodingException e) {
        	log.info(" UnsupportedEncodingException in setParaText");
        }
    }

    private void setParaCharShape(Paragraph p, int start, int length) {
        p.createCharShape();
        ParaCharShape pcs = p.getCharShape();
        
        // 문단 시작은 항상 Normal로 시작하거나, 바로 Bold로 시작하도록 제어
        if (start <= 0) {
            if (length > 0) {
                pcs.addParaCharShape(0, charShapeIndexForBold);
                pcs.addParaCharShape(length, charShapeIndexForNormal);
            } else {
                pcs.addParaCharShape(0, charShapeIndexForNormal);
            }
        } else {
            pcs.addParaCharShape(0, charShapeIndexForNormal);
            pcs.addParaCharShape(start, charShapeIndexForBold);
            if (length > 0) {
                pcs.addParaCharShape(start + length, charShapeIndexForNormal);
            }
        }
    }

    private void setParaLineSeg(Paragraph p) {
        p.createLineSeg();
        ParaLineSeg pls = p.getLineSeg();
        LineSegItem lsi = pls.addNewLineSegItem();

        lsi.setLineHeight(ptToLineHeight(16.0)); // 줄간격 160% 기준
        lsi.setTextPartHeight(ptToLineHeight(FONT_POINT));
        lsi.setDistanceBaseLineToLineVerticalPosition(ptToLineHeight(FONT_POINT * 0.85));
        lsi.setSegmentWidth((int) mmToHwp(150.0)); // A4 폭에 맞게 조정
        lsi.getTag().setFirstSegmentAtLine(true);
        lsi.getTag().setLastSegmentAtLine(true);
    }

    private int ptToLineHeight(double pt) {
        return (int) (pt * 100.0f);
    }

    private long mmToHwp(double mm) {
        return (long) (mm * 72000.0f / 254.0f + 0.5f);
    }
}