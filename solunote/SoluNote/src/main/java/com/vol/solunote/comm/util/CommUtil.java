package com.vol.solunote.comm.util;

import java.text.DecimalFormat;

public class CommUtil { 
	
	
	public String getFileSize(long size) {
	    String hrSize = null;

	    try {
		    double b = size;
		    double k = size/1024.0;
		    double m = ((size/1024.0)/1024.0);
		    double g = (((size/1024.0)/1024.0)/1024.0);
		    double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);
	
		    DecimalFormat dec = new DecimalFormat("0.0");
	
		    if ( t>1 ) {
		        hrSize = dec.format(t).concat(" TB");
		    } else if ( g>1 ) {
		        hrSize = dec.format(g).concat(" GB");
		    } else if ( m>1 ) {
		        hrSize = dec.format(m).concat(" MB");
		    } else if ( k>1 ) {
		        hrSize = dec.format(k).concat(" KB");
		    } else {
		        hrSize = dec.format(b).concat(" Byte");
		    }
	    } catch (Exception e) {
	//		e.printStackTrace();
	    	hrSize = "0.0 Byte";
		}
	
	    return hrSize;
	}
	
	
	public String[] getHddSize(long size) {
		
		String sizeStr[] =new String[2];
		String sizeGubun[] = {"Byte", "KB", "MB", "GB", "TB"};
		 
		try {
			
		    double k = size/1024.0;
		    double m = ((size/1024.0)/1024.0);
		    double g = (((size/1024.0)/1024.0)/1024.0);
		    double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);
	
		    DecimalFormat dec = new DecimalFormat("0.0");
	
		    if ( t>1 ) {
		        sizeStr[0] = dec.format(t);
				sizeStr[1] = sizeGubun[4];
		    } else if ( g>1 ) {
		        sizeStr[0] = dec.format(g);
				sizeStr[1] = sizeGubun[3];
		    } else if ( m>1 ) {
		        sizeStr[0] = dec.format(m);
				sizeStr[1] = sizeGubun[2];
		    } else if ( k>1 ) {
		        sizeStr[0] = dec.format(k);
				sizeStr[1] = sizeGubun[1];
		    } else {
		        sizeStr[0] = dec.format(k);
				sizeStr[1] = sizeGubun[0];
		    }

			return sizeStr;
		} catch (Exception e) {
			 sizeStr[0] = "0.0";
			 sizeStr[1] = "Byte";
			 
			return sizeStr;
				
		}
				
	}
}
