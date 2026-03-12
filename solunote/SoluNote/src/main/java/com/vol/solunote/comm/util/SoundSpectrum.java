package com.vol.solunote.comm.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.UnsupportedAudioFileException;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import com.vol.solunote.comm.model.SoundException;
import com.vol.solunote.comm.vo.SoundFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoundSpectrum {
	
	private static final double PADDING = 1.0D; 
	
	private static final int CHART_HEIGHT = 30;
//	private static final int CHART_HEIGHT = 40;
	private static final int CHART_WIDTH = 150;
	
//	private static final String legendFormat = "%f ~ %f [%s]";
	
	private static final Color edgeColor = new Color(0x99, 0x99, 0x99, 0xFF);        // gray
	private static final Color backColor = new Color(0xff, 0xff, 0x66, 0xFF);   // yellow
	private static final Color gapColor = new Color(0x66, 0xFF, 0xFF, 0xFF);   // cyan
	
	private SoundFile soundFile;

	private double edgeStart;

	private double edgeEnd;
	
	public SoundSpectrum(File file) throws UnsupportedAudioFileException, IOException, SoundException {
		this.soundFile = new SoundFile(file);;
	}
	
	public SoundSpectrum(SoundFile soundFile) {
		this.soundFile = soundFile;
	}


	private XYDataset createDataset(SoundFile soundFile, double minStart, double maxEnd) throws SoundException {
        XYSeries series1 = new XYSeries("");
        
       
		int lower = (int) (edgeStart / soundFile.getSecPerSample());
		int upper = (int) (edgeEnd  / soundFile.getSecPerSample());
    	int itemCount = upper - lower;
       
//    	log.debug("create graphStart = {}, minStart = {},  maxEnd={}, graphEnd={}, lower = {}, upper = {}, itemCount = {})", edgeStart, minStart, maxEnd, edgeEnd,  lower, upper, itemCount);
    
    	for( int i = 0 ; i < itemCount; i++ ) {
        	double x =  (i + lower) * soundFile.getSecPerSample();
     	   double y  = (double) soundFile.getLeftSample(i + lower);
        	
        	series1.add(x, y);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        
        return dataset;
	}
	
	public void writeWave(OutputStream outputStream, double prevEnd, double start, double end, double nextStart) throws SoundException, IOException {

		double chartStart = 0.0D;
		double chartEnd = 0.0D;
		
	    this.edgeStart = start - PADDING;
        if ( this.edgeStart < 0 ) {
        	this.edgeStart = 0;
        }
        // prevEnd - edgeStart - start   ======= 
        if ( prevEnd < edgeStart ) {
        	chartStart = prevEnd;
        } else {
        	// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
        	chartStart = edgeStart;
        }
        
        this.edgeEnd = end + PADDING;
        double duration = soundFile.getFrameLength() * soundFile.getSecPerSample();
        if ( duration < this.edgeEnd ) {
        	this.edgeEnd = duration;
        }
        // end - edgeEnd - nextStart
        if ( edgeEnd < nextStart ) {
        	chartEnd = nextStart;
        } {
        	chartEnd = edgeEnd;
        }
        
        XYDataset dataset = createDataset(this.soundFile, chartStart, chartEnd);
        JFreeChart chart = createChart(dataset);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelsVisible(false);
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setTickLabelsVisible(false);
	        
        // prevEnd - edgeStart - start   ======= 
        if ( prevEnd < edgeStart ) {
        	addMarker(gapColor, plot, chartStart, edgeStart);
        	addMarker(edgeColor, plot, edgeStart, start);
        } else {
        	// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
        	addMarker(edgeColor, plot, chartStart, prevEnd);
        	addMarker(gapColor, plot, prevEnd, start);
        }
        
        addMarker(backColor, plot, start, end);
        
        // end - edgeEnd - nextStart
        if ( edgeEnd < nextStart ) {
        	chartEnd = nextStart;
        	addMarker(edgeColor, plot, end, edgeEnd);
        	addMarker(gapColor, plot, edgeEnd, nextStart);
        } {
        	chartEnd = edgeEnd;
        	addMarker(gapColor, plot, end, nextStart);
        	addMarker(edgeColor, plot, nextStart, edgeEnd);
        	
        }
        
        log.debug("prevEnd = {}, start = {}, end = {}, nextStart = {},  edgeStart = {}, edgeEnd = {}", prevEnd, start, end, nextStart, edgeStart, edgeEnd);
        
		ChartUtils.writeChartAsPNG(outputStream, chart, CHART_WIDTH, CHART_HEIGHT);
	}
	
	public byte[] writeWaveSvg(double prevEnd, double start, double end, double nextStart) throws SoundException, IOException {
		
		double chartStart = 0.0D;
		double chartEnd = 0.0D;
		
		this.edgeStart = start - PADDING;
		if ( this.edgeStart < 0 ) {
			this.edgeStart = 0;
		}
		// prevEnd - edgeStart - start   ======= 
		if ( prevEnd < edgeStart ) {
			chartStart = prevEnd;
		} else {
			// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
			chartStart = edgeStart;
		}
		
		this.edgeEnd = end + PADDING;
		double duration = soundFile.getFrameLength() * soundFile.getSecPerSample();
		if ( duration < this.edgeEnd ) {
			this.edgeEnd = duration;
		}
		// end - edgeEnd - nextStart
		if ( edgeEnd < nextStart ) {
			chartEnd = nextStart;
		} {
			chartEnd = edgeEnd;
		}
		
		XYDataset dataset = createDataset(this.soundFile, chartStart, chartEnd);
		JFreeChart chart = createChart(dataset);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setTickLabelsVisible(false);
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setTickLabelsVisible(false);
		
		// prevEnd - edgeStart - start   ======= 
		if ( prevEnd < edgeStart ) {
			addMarker(gapColor, plot, chartStart, edgeStart);
			addMarker(edgeColor, plot, edgeStart, start);
		} else {
			// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
			addMarker(edgeColor, plot, chartStart, prevEnd);
			addMarker(gapColor, plot, prevEnd, start);
		}
		
		addMarker(backColor, plot, start, end);
		
		// end - edgeEnd - nextStart
		if ( edgeEnd < nextStart ) {
			chartEnd = nextStart;
			addMarker(edgeColor, plot, end, edgeEnd);
			addMarker(gapColor, plot, edgeEnd, nextStart);
		} {
			chartEnd = edgeEnd;
			addMarker(gapColor, plot, end, nextStart);
			addMarker(edgeColor, plot, nextStart, edgeEnd);
			
		}
		
//		log.debug("prevEnd = {}, start = {}, end = {}, nextStart = {},  edgeStart = {}, edgeEnd = {}", prevEnd, start, end, nextStart, edgeStart, edgeEnd);
		
		byte[] byteArray = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ChartUtils.writeChartAsPNG(byteStream, chart, CHART_WIDTH, CHART_HEIGHT);

		try {
			byteStream.flush();
			byteArray = byteStream.toByteArray();
		} finally {
			if (byteStream != null) {
				byteStream.close();
			}
		}

		return byteArray;
	}
	
	public byte[] blankWaveSvg(double prevEnd, double start, double end, double nextStart) throws SoundException, IOException {
		
		double chartStart = 0.0D;
		double chartEnd = 0.0D;
		
		this.edgeStart = start - PADDING;
		if ( this.edgeStart < 0 ) {
			this.edgeStart = 0;
		}
		// prevEnd - edgeStart - start   ======= 
		if ( prevEnd < edgeStart ) {
			chartStart = prevEnd;
		} else {
			// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
			chartStart = edgeStart;
		}
		
		this.edgeEnd = end + PADDING;
		double duration = soundFile.getFrameLength() * soundFile.getSecPerSample();
		if ( duration < this.edgeEnd ) {
			this.edgeEnd = duration;
		}
		// end - edgeEnd - nextStart
		if ( edgeEnd < nextStart ) {
			chartEnd = nextStart;
		} {
			chartEnd = edgeEnd;
		}
		
		XYDataset dataset = createDataset(this.soundFile, chartStart, chartEnd);
		JFreeChart chart = createChart(dataset);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(Color.WHITE);
		plot.setOutlineStroke(new BasicStroke(1.0f));
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		
		// sets paint color for each series
//		renderer.setSeriesPaint(0, Color.RED);
//		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(0, Color.WHITE);
		
		// sets thickness for series (using strokes)
		renderer.setSeriesStroke(0, new BasicStroke(0.1f));
		
		plot.setRenderer(renderer);
		
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setTickLabelsVisible(false);
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setTickLabelsVisible(false);
		
		byte[] byteArray = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ChartUtils.writeChartAsPNG(byteStream, chart, CHART_WIDTH, CHART_HEIGHT);
		
		try {
			byteStream.flush();
			byteArray = byteStream.toByteArray();
		} finally {
			if (byteStream != null) {
				byteStream.close();
			}
		}
		
		return byteArray;
	}
	
//	byte[] exportChartAsSVG(JFreeChart chart, Rectangle2D bounds) throws    IOException {
//	      // Get a DOMImplementation and create an XML document
//	      DOMImplementation domImpl =
//	          GenericDOMImplementation.getDOMImplementation();
//	      Document document = domImpl.createDocument(null, "svg", null);
//
//	      // Create an instance of the SVG Generator
//	      SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
//
//	      // draw the chart in the SVG generator
//	      chart.draw(svgGenerator, bounds);
//
//	      // Write svg file
////	      OutputStream outputStream = new FileOutputStream(svgFile);
//	      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	       
//	      Writer out = new OutputStreamWriter(outputStream, "UTF-8");
//	      svgGenerator.stream(out, true /* use css */);                     
//	      outputStream.flush();
//	      
//	      byte[] byteArray = outputStream.toByteArray();
//	      outputStream.close();
//	      
//	      return byteArray;	      
//	      
//	    }    


	
	public void drawWave(double prevEnd, double start, double end, double nextStart, String text,  String outdir, String basename) throws SoundException, IOException {

		double chartStart = 0.0D;
		double chartEnd = 0.0D;
		
	    this.edgeStart = start - PADDING;
        if ( this.edgeStart < 0 ) {
        	this.edgeStart = 0;
        }
        // prevEnd - edgeStart - start   ======= 
        if ( prevEnd < edgeStart ) {
        	chartStart = prevEnd;
        } else {
        	// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
        	chartStart = edgeStart;
        }
        
        this.edgeEnd = end + PADDING;
        double duration = soundFile.getFrameLength() * soundFile.getSecPerSample();
        if ( duration < this.edgeEnd ) {
        	this.edgeEnd = duration;
        }
        // end - edgeEnd - nextStart
        if ( edgeEnd < nextStart ) {
        	chartEnd = nextStart;
        } {
        	chartEnd = edgeEnd;
        }
        
        XYDataset dataset = createDataset(this.soundFile, chartStart, chartEnd);
        JFreeChart chart = createChart(dataset);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        Font font = plot.getDomainAxis().getTickLabelFont();
        Font font1 = plot.getRangeAxis().getTickLabelFont();
        System.out.println("font  = " + font);
        System.out.println("font1 = " + font1);
        
        Font xfont = new Font("Arial", Font.PLAIN,8);
//        Font yfont = new Font("Arial", Font.PLAIN, 8); 
//        Font yfont = new Font("Dialog", Font.PLAIN, 8); 
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(xfont);
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setTickLabelFont(xfont);
//        plot.getDomainAxis().setTickLabelFont(font););
	        
        // prevEnd - edgeStart - start   ======= 
        if ( prevEnd < edgeStart ) {
        	addMarker(gapColor, plot, chartStart, edgeStart);
        	addMarker(edgeColor, plot, edgeStart, start);
        } else {
        	// edgeStart - prevEnd - start   ======= end  - nextStart - edgeEnd
        	addMarker(edgeColor, plot, chartStart, prevEnd);
        	addMarker(gapColor, plot, prevEnd, start);
        }
        
        addMarker(backColor, plot, start, end);
        
        // end - edgeEnd - nextStart
        if ( edgeEnd < nextStart ) {
        	chartEnd = nextStart;
        	addMarker(edgeColor, plot, end, edgeEnd);
        	addMarker(gapColor, plot, edgeEnd, nextStart);
        } {
        	chartEnd = edgeEnd;
        	addMarker(gapColor, plot, end, nextStart);
        	addMarker(edgeColor, plot, nextStart, edgeEnd);
        	
        }
        
        log.debug("prevEnd = {}, start = {}, end = {}, nextStart = {},  edgeStart = {}, edgeEnd = {}", prevEnd, start, end, nextStart, edgeStart, edgeEnd);
        
//		addLegend(chart, String.format(legendFormat,  start, end, text));
		
		int width = 800; /* Width of the image */
		int height = 60; /* Height of the image */
		File lineChartpng = new File(outdir, basename + "_" + String.format("%.5f", start) + ".png");
		ChartUtils.saveChartAsPNG(lineChartpng, chart, width, height);
	}
	
//	public void drawSoundDiff(double start, double end, float[] adjust, String text, String modText, boolean isSame, String outdir, String basename, int firstNon) throws SoundException, IOException {
//		
//		double minStart = Math.min(adjust[0], start);
//		double maxStart = Math.max(adjust[0], start);
//		
//		double minEnd = Math.min(adjust[1], end);
//		double maxEnd = Math.max(adjust[1], end);
//		
//		XYDataset dataset = createDataset(this.soundFile, minStart, maxEnd, maxStart, minEnd);
//		JFreeChart chart = createChart(dataset, Double.toString(start));
//		XYPlot plot = (XYPlot) chart.getPlot();
//		plot.setBackgroundPaint(Color.WHITE);
//		
//		Color colorStart = start - adjust[0] >= 0 ? adjustPositive : adjustNegative;
//		Color colorEnd = end - adjust[1] >= 0 ? adjustPositive : adjustNegative;
//		
//		addMarker(edgeColor, plot, edgeStart, minStart);
//		
//		addMarker(colorStart, plot, minStart, maxStart);
//		addMarker(colorEnd, plot, minEnd, maxEnd);
//		
//		addMarker(edgeColor, plot, maxEnd, edgeEnd);
//		
//		String same = isSame ? "same" : "DIFFERENT";
//		String gap = String.format("%f ~ %f [%d]", adjust[0] - start, adjust[1] - end, firstNon);
//		addLegend(chart, String.format(legendFormat,  start, end, text, same));
//		addLegend(chart, String.format(legendFormat,  adjust[0], adjust[1], modText, gap));
//		
//		int width = 1640; /* Width of the image */
//		int height = 480; /* Height of the image */
//		File lineChartpng = new File(outdir, basename + "_" + String.format("%.5f", start) + ".png");
//		ChartUtils.saveChartAsPNG(lineChartpng, chart, width, height);
//	}



	private void addMarker(Color color, XYPlot plot, double markerStart, double markerEnd) {
	
		if (markerStart < 0.0D) {
			markerStart = 0.0D;
		}

		if (markerEnd > markerStart) {
			Marker marker = new IntervalMarker(markerStart, markerEnd);
//			marker.setPaint(new Color(0xDD, 0xFF, 0xDD, 0x80));
			marker.setPaint(color);
			marker.setAlpha(0.8f);
			plot.addDomainMarker(marker, Layer.BACKGROUND);
		}
	}


	private JFreeChart createChart(XYDataset dataset) {
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				null,
				null,
				null,
				dataset,
				PlotOrientation.VERTICAL,
				false,
				false,
				false
				);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
//            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
//            renderer.setDefaultShapesVisible(true);
//            renderer.setDefaultShapesFilled(true);
//            renderer.setBaseShapesVisible(true);
//            renderer.setBaseShapesFilled(true);
		
		return chart;
	}

//
//	public void drawSpectrum(Map<String, Object> resultMap, String name) throws IOException, SoundException {
//		
//		List<Map<String, Object>> list = (List <Map <String, Object>>)resultMap.get("stt_result");
//		
//		XYDataset dataset = createDataset(this.soundFile, 0.0D, this.soundFile.getFrameLength() * this.soundFile.getSecPerSample(), 0.0D, 0.0D );
//		JFreeChart chart = createChart(dataset, name);
//		XYPlot plot = (XYPlot) chart.getPlot();
//		
//		Color adjustColor = new Color(0x66, 0xFF, 0xFF, 0x80);
//		
//		for (Map<String, Object> map : list) {
//			
//			double start = (double) map.get("start");
//			double end = (double) map.get("end");
//		
//			addMarker(adjustColor, plot, start, end);
//		}
//		
//		int width = 1640; /* Width of the image */
//		int height = 480; /* Height of the image */
//		File lineChartpng = new File("d:/tmp/br/" + name + ".png");
//		ChartUtils.saveChartAsPNG(lineChartpng, chart, width, height);
//	
//		
//
//		
//	}

}
