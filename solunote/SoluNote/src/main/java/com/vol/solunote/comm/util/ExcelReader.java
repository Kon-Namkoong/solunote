package com.vol.solunote.comm.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelReader {
    /**
     * XLS 파일을 분석하여 List<WordDic13Vo> 객체로 반환
     * @param filePath
     * @return
     */
    @SuppressWarnings("resource")
    public List<Map<String, String>> xlsToVoList(String filePath) {
        // 반환할 객체를 생성
        List<Map<String, String>> list = new ArrayList<>();
        
        FileInputStream fis = null;
        HSSFWorkbook workbook = null;
        
        
        try {
			fis= new FileInputStream(filePath);
			// HSSFWorkbook은 엑셀파일 전체 내용을 담고 있는 객체
			workbook = new HSSFWorkbook(fis);
			
			// 탐색에 사용할 Sheet, Row, Cell 객체
			HSSFSheet sheet;
			HSSFRow   row;
			HSSFCell  cell;
			Map<String, String> vo = null;
			
			sheet = workbook.getSheetAt(0);

			java.util.Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row = (HSSFRow) rows.next();
				
				int rowIdx = row.getRowNum();
				if(rowIdx <= 2) continue;
				
				java.util.Iterator<Cell> cells = row.cellIterator();
				
				boolean aFlag = false;
				while (cells.hasNext()) {
					cell = (HSSFCell) cells.next();
					String cellValue;
					if (cell.getCellType() == CellType.STRING) {
						cellValue = cell.getStringCellValue();
					} else if (cell.getCellType() == CellType.NUMERIC) {
						cellValue = String.valueOf(cell.getNumericCellValue());
					} else {
						cellValue = null;
					}
					
					if(cellValue == null || "".equals(cellValue)) 
						continue;

					int colIdx = cell.getColumnIndex();
					
					vo =new HashMap<String,String>();
					
					if(colIdx == 0) {
						vo.put("word", cellValue);
					} else if(colIdx == 1)  {
						vo.put("wordSynonym",cellValue);
					}
					
					aFlag = true;
				}
				
				if(aFlag) list.add(vo);
				
			}
			
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			log.info("FileNotFoundException in xlsToVoList");
			return null;
		} catch (IOException e) {			
			// Auto-generated catch block
			log.info("IOException in xlsToVoList");
			return null;
		} finally {
			try {
                // 사용한 자원은 finally에서 해제
                if( workbook!= null) workbook.close();
                if( fis!= null) fis.close();                
            } catch (IOException e) {
                // Auto-generated catch block            	
    			log.info("IOException in xlsToVoList");
            }
		}
		return list;
    }
        
    /**
     * XLSX 파일을 분석하여 List<CustomerVo> 객체로 반환
     * @param filePath
     * @return
     */
    public List<Map<String, String>> xlsxToVoList(String filePath) {
        // 반환할 객체를 생성
        List<Map<String, String>> list = new ArrayList<>();
        
        FileInputStream fis = null;
        XSSFWorkbook workbook = null;
            
        try {
			fis= new FileInputStream(filePath);
			// HSSFWorkbook은 엑셀파일 전체 내용을 담고 있는 객체
			workbook = new XSSFWorkbook(fis);
			
			// 탐색에 사용할 Sheet, Row, Cell 객체
			XSSFSheet sheet;
			XSSFRow   row;
			XSSFCell  cell;
			Map<String, String> vo = null;
			
			sheet = workbook.getSheetAt(0);
			java.util.Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row = (XSSFRow) rows.next();
				int rowIdx = row.getRowNum();
				if(rowIdx <= 2) continue;
				
				java.util.Iterator<Cell> cells = row.cellIterator();
				
				boolean aFlag = false;
				while (cells.hasNext()) {
					cell = (XSSFCell) cells.next();
					String cellValue = null;
					if (cell.getCellType() == CellType.STRING) {
						cellValue = cell.getStringCellValue();
					} else if (cell.getCellType() == CellType.NUMERIC) {
						cellValue = String.valueOf(cell.getNumericCellValue());
					} else {
						//
					}
					
					if(cellValue == null || "".equals(cellValue)) 
						continue;

					int colIdx = cell.getColumnIndex();
					vo = new HashMap<String,String>();
					
					if(colIdx == 0) {
						vo.put("word",cellValue);
					} else if(colIdx == 1) {
						vo.put("wordSynonym", cellValue);
					}
					
					aFlag = true;
				}
				
				if(aFlag) list.add(vo);
			}
				
			
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			log.info("FileNotFoundException in xlsToVoList");
			return null;
		} catch (IOException e) {
			// Auto-generated catch block
			log.info("IOException in xlsToVoList");
			return null;
		} finally {
			try {
                // 사용한 자원은 finally에서 해제
                if( workbook!= null) workbook.close();
                if( fis!= null) fis.close();
                
            } catch (IOException e) {
                // Auto-generated catch block
    			log.info("IOException in xlsToVoList");
            }
		}
        return list;
    }

}

