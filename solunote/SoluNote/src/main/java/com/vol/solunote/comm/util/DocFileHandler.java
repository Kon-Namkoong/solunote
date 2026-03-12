package com.vol.solunote.comm.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import rcc.h2tlib.parser.H2TParser;
import rcc.h2tlib.parser.HWPMeta;

public class DocFileHandler {
	H2TParser h2TParser;

	public DocFileHandler() {
		h2TParser = new H2TParser();
	}

	public String getHWPText(String file) throws NotHWPFileException {
		StringBuilder sb = new StringBuilder();
		HWPMeta meta = new HWPMeta();
		if (!h2TParser.GetText(file, meta, sb))
			throw new NotHWPFileException(file);
		return sb.toString();
	}

	public String getDocxText(String fileName) {

		POITextExtractor extractor;

		String extractedText = null;
		try {
			InputStream fis = new FileInputStream(fileName);
			// if docx
			if (fileName.toLowerCase().endsWith(".docx")) {
				XWPFDocument doc = new XWPFDocument(fis);
				extractor = new XWPFWordExtractor(doc);
			} else {
				// if doc
				POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
				extractor = ExtractorFactory.createExtractor(fileSystem);
			}
			extractedText = extractor.getText();
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		return extractedText;
	}

	public String getPDFText(String fileName) {
		PDDocument pdDoc = null;
		PDFTextStripper pdfStripper;
		File file;
		String parsedText = null;
		
		try {
			file = new File(fileName);
			pdfStripper = new PDFTextStripper();
			pdDoc = PDDocument.load(file);
			parsedText = pdfStripper.getText(pdDoc);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return parsedText;
	}

	public String getReadText(String fileName) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return new String(encoded);
	}

	public class NotHWPFileException extends Exception {

		private static final long serialVersionUID = 1L;

		public NotHWPFileException(String filename) {
			super(filename + " is not a HWP file");
		}
	}

}
