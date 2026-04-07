package com.vol.solunote.comm.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {
	
	public static DecimalFormat dFormat = new DecimalFormat("##.##");
	public static DecimalFormat nFormat = new DecimalFormat("###.###");
	public static DecimalFormat sFormat = new DecimalFormat("###,###");
	
	
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List merge(List list0, List list1) {
        if (list0 == null) return list1;
        if (list1 == null) return list0;
        
        List list = new ArrayList(list0);
        for (int i = 0; i < list1.size(); i++) {
            list.add(list1.get(i));
        }
        return list;
    }
    
    public static boolean isNull(String str) {
        return (str == null || str.trim().equals(""));
    }
    
    public static boolean isNotNull(String str) {
    	return (!CommonUtil.isNull(str));
    }
    
    public static boolean isHave(int num, int[] numArray) {
        boolean result = false;
        if (numArray != null) {
            for (int i = 0; i < numArray.length; i++) {
                if (numArray[i] == num) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
	/**
	 * 문자열로 구성될 날자를 조합해서 반환한다.
     * @param toYear
     * @param toMonth
     * @param toDay
     * @param string
     * @return
     */
	public static String getMergedStringDate(String year, String month, String day, String time) {
        StringBuffer result = new StringBuffer();
        result.append(year);
        if (month.length() == 1) {
            result.append('0');
        }
        result.append(month);
        if (day.length() == 1) {
            result.append('0');
        }
        result.append(day);
        result.append(time);
        
        return result.toString();
    }
    
    /**
     * html 태그 을 모두 제거
     * @param param
     * @return
     */
    public static String delHtmlTag(String param){
        Pattern p = Pattern.compile("\\<(\\/?)(\\w+)*([^<>]*)>");
        Matcher m = p.matcher(param);
        param = m.replaceAll("").trim();
        return param;
    }

    /**
     * 세자리마다 콤마찍기
     * @param str
     * @return
     */
	public static String comma(String str){
        String temp = reverseString(str);
        String result = "";
 
        for(int i = 0 ; i < temp.length() ; i += 3) {
            if(i + 3 < temp.length()) {
                result += temp.substring(i, i + 3) + ",";
            }
            else {
                result += temp.substring(i);
            }
        }
 
        return reverseString(result);
    }
 
	public static String reverseString(String s){
        return new StringBuffer(s).reverse().toString();
    }
	
    /**
     * 오늘날짜구하기
     * @param str
     * @return
     */
	public static String getToday(){
		 Date now = new Date(); 
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
 
        return sdf.format(now);
    }
	/**
     * 오늘날짜구하기 포맷 원하는 모양으로(vary)
     * @param str
     * @return
     */
	public static String getToday(String formatString){
		 Date now = new Date();
		 SimpleDateFormat sdf = null;
		 if("".equals(CommonUtil.nvl(formatString))){
			 sdf = new SimpleDateFormat("yyyy-MM-dd");
		 }else{
			 sdf = new SimpleDateFormat(formatString);
		 }
       return sdf.format(now);
	}
	/**
     * 오늘 기준으로 특정 기간후의 날짜 구하기(vary)
     * @param str
     * @return
     */
	public static String getDate(int iDay){
		return CommonUtil.getDate(iDay, null);
	}
	/**
     * 오늘 기준으로 특정 기간후의 날짜 구하기 포맷 원하는 모양으로(vary)
     * @param str
     * @return
     */
	public static String getDate(int iDay, String formatString){
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, iDay);
		Date returnday = today.getTime();
		SimpleDateFormat sdf = null;
		if("".equals(CommonUtil.nvl(formatString))){
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			sdf = new SimpleDateFormat(formatString);
		}
		return sdf.format(returnday);
	}
	/**
     * 오늘 기준으로 특정 기간후의 주 구하기 포맷 원하는 모양으로(vary)
     * @param str
     * @return
     */
	public static String getWeek(int iWeek, String formatString){
		Calendar today = Calendar.getInstance();
		today.add(Calendar.WEDNESDAY, iWeek);
		Date returnday = today.getTime();
		SimpleDateFormat sdf = null;
		if("".equals(CommonUtil.nvl(formatString))){
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			sdf = new SimpleDateFormat(formatString);
		}
		return sdf.format(returnday);
	}
	/**
     * 오늘 기준으로 특정 기간후의 달 구하기 포맷 원하는 모양으로(vary)
     * @param str
     * @return
     */
	public static String getMonth(int iMonth, String formatString){
		Calendar today = Calendar.getInstance();
		today.add(Calendar.MONTH, iMonth);
		Date returnday = today.getTime();
		SimpleDateFormat sdf = null;
		if("".equals(CommonUtil.nvl(formatString))){
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			sdf = new SimpleDateFormat(formatString);
		}
		return sdf.format(returnday);
	}
	//(vary)
	public static boolean isEmpty(Object arg){
		boolean result = false;
		if(arg == null || String.valueOf(arg).trim().equals("")){
			result = true;
		}
		return result;
	}
	//(vary)
	public static String nvl(Object arg, String ch){
		if(arg == null) return ch;
		String str = String.valueOf(arg);
		if(str.trim().equals("") || str.trim().equalsIgnoreCase("null")) return ch;
		return str;
	}
	//(vary)
	public static String nvl(Object arg){
		return nvl(arg, "");
	}
	//(vary)
	public static String nvlTrim(Object arg){
		return nvl(arg, "").trim();
	}
	//(vary)
	public static String nvlyn(Object arg){
		return nvl(arg, "N");
	}
	//(vary)
	public static boolean isNotEmpty(Object arg){
		return !isEmpty(arg);
	}
	//(vary)
	@SuppressWarnings("rawtypes")
	public static Map getFirstMapFromList(List _list){
		if(_list != null){
			if(_list.size() > 0)
				return (Map) _list.get(0);
			else{
				return null;
			}
		}else
			return	null;
	}
	//(vary)
	public static boolean isNotEmpty(Object arg,Object arg2){
		boolean returnValue = true;
		returnValue = !isEmpty(arg);
		returnValue = !isEmpty(arg2);
		return returnValue;
	}
	
	//(vary)
	public static int nvl(Object arg, int i){

		if(arg == null) return i;		
		int rtVal = i;
		String tempS = "";
		try{
			tempS = nvl(arg);
			if(!"".equals(tempS)){
				rtVal = Integer.valueOf(tempS);
			}
		} catch (NumberFormatException e) {
			log.error("String is not numeric");
		} catch(Exception e){
			log.error("Exception during converting numeric string to integer");
		}
		return	rtVal;
	}
	
	//(vary)
	public static boolean nvl(Object arg, boolean i){
		if(arg == null) return i;
		
		boolean rtVal = i;
		String tempS = "";
		try{
			tempS = nvl(arg);
			if(!"".equals(tempS)){
				rtVal = Boolean.valueOf(tempS);
			}
		} catch (IllegalArgumentException e) {
			log.error("String is not numeric");
		} catch(Exception e){
			log.error("Exception during converting numeric string to integer");
		}
		return	rtVal;
	}
	
	//(vary)
	public static long nvl(Object arg, long i){
		if(arg == null) return i;
		
		String tempS = "";
		long	rtVal = i;
		try{
			tempS = nvl(arg);
			if(!"".equals(tempS)){
				rtVal = Long.valueOf(tempS);
			}
		} catch (NumberFormatException e) {
			log.error("String is not numeric");
		} catch(Exception e){
			log.error("Exception during converting numeric string to long");
		}
		return	rtVal;
	}
	//(vary)
	public static float nvlFloat(Object arg, float i){

		if(arg == null) return i;

		float returnValue = i;
		try{
			returnValue = Float.valueOf(nvl(arg, "0"));
		}
		catch(NumberFormatException e){
			log.error("String is not float format");
		}		
		catch(Exception e){
			log.error("Exception during converting float string to float");
		}
		return returnValue;
	}

	//(vary)
	public static Double nvlDouble(Object arg, Double i){
		if(arg == null) return i;
		
		Double returnValue = i;
		try{
			returnValue = Double.valueOf(nvl(arg, "0"));
		}
		catch(NumberFormatException e){
			log.error("String is not double format");
		}		
		catch(Exception e){
			log.error("Exception during converting double string to double");
		}		
		return returnValue;
	}
	
	//(vary)
	public static int nvlInt(Object arg){
		return nvl(arg, 0);
	}
	public static Double nvlDouble(Object arg){
		return nvlDouble(arg, 0.0);
	}
	
	public static Date fnGetStringDateToDate(String s_date)
	{
		Date d_return = null;
		
		try {
			d_return = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			d_return = format.parse(s_date);
		
		} catch (ParseException ex) {
			d_return = null;
		}		
		return d_return;
	}
	
	public static int fnGetStringDateToCompare(String s_start, String s_end) 
	{
		int n_days = -1000000000;
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date day1 = null;
			Date day2 = null;
			
			day1 = format.parse(s_start+" 00:00:00");
			day2 = format.parse(s_end+" 00:00:00");
			
			long diff = day2.getTime() - day1.getTime();
			n_days = (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
			log.info("Parse Exception in fnGetStringDateToCompare");			
		} catch (Exception e) {
			log.info("Exception Occured in fnGetStringDateToCompare");
		}

		return Math.abs(n_days);
	}

	public static int fnGetStringDateToCompareN(String s_start, String s_end) 
	{
		int n_days = -1000000000;
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date day1 = null;
			Date day2 = null;
			
			day1 = format.parse(s_start+" 00:00:00");
			day2 = format.parse(s_end+" 00:00:00");
			
			long diff = day2.getTime() - day1.getTime();
			n_days = (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			
		} catch (ParseException	e) {
			log.info("Parse Exception in fnGetStringDateToCompareN");	
		} catch (Exception e) {
			log.info("Exception Occured in fnGetStringDateToCompareN");
		}

		return n_days;
	}
	
	@SuppressWarnings("rawtypes")
	public static int getSize(List list){
		if(list == null) return 0;
		return list.size();
	}
	
	public static String checkFile(String filePath, String realFile){
		File CHECK_FILE = new File(filePath);
		if(!CHECK_FILE.exists()) CHECK_FILE.mkdir();
		int pos = 1;
		int count = 0;
		do {
			CHECK_FILE = new File ( filePath + "\\" + realFile );
			if(CHECK_FILE.exists()){
				String fileNm  = realFile.substring( 0, realFile.lastIndexOf(".") );      // 파일명
				String fileExt = realFile.substring( realFile.lastIndexOf(".") + 1, realFile.length() ); // 확장자
				String len = fileNm.substring(fileNm.length() - pos);
				try{
					count = Integer.parseInt(len);
					fileNm = fileNm.substring(0, fileNm.length() -pos);
				}catch(NumberFormatException ex){
					count = 0; 
				}
				count++;
				fileNm = fileNm + count;
				realFile = fileNm + "." + fileExt;
				pos = ("" + count ).length();
			}
		}while ( CHECK_FILE.exists() );
	    return realFile;
	}

	public static String checkFileExt(String filePath, String realFile){
		String s_fileext = "";
		File CHECK_FILE = new File(filePath);
		if(!CHECK_FILE.exists()) CHECK_FILE.mkdir();
		int pos = 1;
		int count = 0;
		do {
			CHECK_FILE = new File ( filePath + "\\" + realFile );
			if(CHECK_FILE.exists()){
				String fileNm  = realFile.substring( 0, realFile.lastIndexOf(".") );      // 파일명
				String fileExt = realFile.substring( realFile.lastIndexOf(".") + 1, realFile.length() ); // 확장자
				s_fileext = fileExt;
				String len = fileNm.substring(fileNm.length() - pos);
				try{
					count = Integer.parseInt(len);
					fileNm = fileNm.substring(0, fileNm.length() -pos);
				}catch(NumberFormatException ex){
					count = 0; 
				}
				count++;
				fileNm = fileNm + count;
				realFile = fileNm + "." + fileExt;
				pos = ("" + count ).length();
			}
		}while ( CHECK_FILE.exists() );
	    return s_fileext;
	}
	
	
	/**
	 * 문자열(8859_1)을 지정된 character encoding 으로 변환한다.
	 * @param s 대상 문자열
	 * @param charset encoding charset
	 */
	public static String encode(String s, String charset) 	{
		if (charset == null || "8859_1".equals(charset)) return s;
		String out = null;
		if (s == null ) return null;
		
		try { 
			out = new String(s.getBytes("8859_1"), charset);
		} 	catch(UnsupportedEncodingException ue) {
			out = new String(s);
		}
		return out;
	}
	
	public static int consi(String consi){
		return Integer.parseInt(consi);
	}

    /**
     * <pre>
     * 주어진 size내에서 0으로 채워진 String을 리턴한다.
     * </pre>
     *
     * @param num 원래 숫자
     * @param size 0을 넣어 만들 문자열의 길이
     * @return String 주어진 size 앞부분에 0을 채운 문자열
     */
    public static String getZeroBaseString(int num,int size)
    {
        return getZeroBaseString(String.valueOf(num),size);
    }

    /**
     * <pre>
     * 주어진 size내에서 0으로 채워진 String을 리턴한다.
     * </pre>
     *
     * @param num 원래 숫자 문자열
     * @param size 0을 넣어 만들 문자열의 길이
     * @return String 주어진 size 앞부분에 0을 채운 문자열
     */
    public static String getZeroBaseString(String num,int size)
    {
      String zeroBase = "";

      if (num.length() >= size)
          return num;

      for(int index=0; index<(size-num.length()); ++index) {
          zeroBase += "0";
      }

      return zeroBase+num;
    }
    
    public static String getIntToTimeString(int nTime) {
    	String s_time = "00:00:00";
    	
    	int hh = 0;
    	int mm = 0;
    	int ss = 0;
    	
    	if (nTime >= 0) {
    		s_time = "";
    		
    		hh = nTime / (60*60);
    		int m_temp = nTime % (60*60);
    		mm = m_temp / 60;
    		int s_temp = m_temp % 60;
    		ss = s_temp;
    		
    		if (hh < 10)
    			s_time += "0"+hh+":";
    		else
    			s_time += hh+":";
    		
    		if (mm < 10)
    			s_time += "0"+mm+":";
    		else
    			s_time += mm+":";
    		
    		if (ss < 10)
    			s_time += "0"+ss;
    		else
    			s_time += ss+"";
    	}
    	
    	return s_time;
    }

    /**
     * <pre>
     * 페이징 화면을 구성한다.
     * </pre>
     *
     */
	public static String getPaging(int n_page, int n_total_data, int n_pagecount, int n_paging_depth) {
		StringBuffer sb = new StringBuffer();

		int n_tot_page = 0;
		if (n_total_data % n_pagecount > 0) {
			n_tot_page = n_total_data / n_pagecount + 1;
		} else {
			n_tot_page = n_total_data / n_pagecount;
		}

		if (n_tot_page > 0) {
			sb.append("<a id='page_data' ref='1'>처음</a>&nbsp;");

			if (n_tot_page > n_paging_depth) {
				int n_prev = 0, n_now = 0, n_loop = 0, n_next = 0; 
			
				n_prev = ((n_page-1) / n_paging_depth) * n_paging_depth - (n_paging_depth-1);
				n_now = ((n_page-1) / n_paging_depth) * n_paging_depth + 1;
				n_next = ((n_page-1) / n_paging_depth) * n_paging_depth + (n_paging_depth+1);
			
				if (n_next > n_tot_page) {
					n_loop = n_tot_page;
				} else {
					n_loop = n_next-1;
				}
			
				if (n_prev > 0) {
					sb.append("&nbsp;<a id='page_data' ref='"+n_prev+"'>◀</a>&nbsp;");
				}

				for (int i=n_now ; i<=n_loop ; i++) { 
					if (i == n_page) {
						sb.append("&nbsp;<strong><a id='page_data' ref='"+i+"' style='color:#FF0000'>"+i+"</a></strong>&nbsp;");
					} else {
						sb.append("&nbsp;<a id='page_data' ref='"+i+"'>"+i+"</a>&nbsp;");
					}
				} 
				if (n_next <= n_tot_page) {
						sb.append("&nbsp;<a id='page_data' ref='"+n_next+"'>▶</a>&nbsp;");
				}
			} else {
				for (int i=1 ; i<=n_tot_page ; i++) { 
					if (i == n_page) {
						sb.append("&nbsp;<strong><a id='page_data' ref='"+i+"' style='color:#FF0000'>"+i+"</a></strong>&nbsp;");
					} else {
						sb.append("&nbsp;<a id='page_data' ref='"+i+"'>"+i+"</a>&nbsp;");
					}
				}
			}
			sb.append("<a id='page_data' ref='"+n_tot_page+"'>끝</a>");
		}

		String pagelist = sb.toString();
		sb.setLength(0);
		return pagelist;
	}
	


	public static String convDuration(String value) {
		
		long v = value == null ? 0 : Double.valueOf(value).longValue();
		return DurationFormatUtils.formatDuration(v, "HH:mm:ss");
	}
	
	// https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
	// http://help.dottoro.com/lapuadlp.php
	public static String getMimeType(String fileName) {
		String contType = null;
		
		String ext = FilenameUtils.getExtension(fileName);
		if ( ext != null ) {
			ext = ext.toLowerCase();
		}
		switch( ext ) {
			case "wav" : contType = "audio/wav"; break;
			case "mp3" : contType = "audio/mpeg"; break;
			case "aac" : contType = "audio/aac"; break;
			case "m4a" : contType = "audio/m4a"; break;
			case "mp4" : contType = "vidio/mp4"; break;
			case "mpeg" : contType = "vidio/mpeg"; break;
//			case "avi" : contType = "video/x-msvideo"; break;
			case "avi" : contType = "video/avi"; break;
			case "webm" : contType = "video/webm"; break;
			case "ogg" : contType = "audio/ogg"; break;
			default : contType = ""; break;
		}
		
		return contType;
	}
}
