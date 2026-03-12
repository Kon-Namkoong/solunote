package com.vol.solunote.comm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.vol.solunote.model.vo.comm.SearchVo;

public class DateUtil {
	/**
	 * <b>날짜 관련 유틸 클래스</b>
	 * <p>
	 * 날짜와 시간에 관련된 유틸 클래스
	 */
	private static Locale locale = new Locale("KOREA");

	private static DateFormat df = DateFormat.getDateTimeInstance(
			DateFormat.FULL, DateFormat.MEDIUM, DateUtil.getLocale());

	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		DateUtil.locale = locale;
	}

	public static DateFormat getDf() {
		return df;
	}

	public static void setDf(DateFormat df) {
		DateUtil.df = df;
	}

	private DateUtil() {
	}

	/**
	 * 나이(만)를 구한다.
	 * 
	 * @param fromYear
	 * @param fromMonth
	 * @param fromDate
	 * @param toYear
	 * @param toMonth
	 * @param toDate
	 * @return
	 */
	public static int getAge(int fromYear, int fromMonth, int fromDate,
			int toYear, int toMonth, int toDate) {
		int age = 0;
		if (toYear > fromYear) {
			age = toYear - fromYear;
			if (DateUtil.toDate(toYear, fromMonth, fromDate).getTime() > DateUtil
					.toDate(toYear, toMonth, toDate).getTime()) {
				age--;
			}
		}
		return age;
	}

	/**
	 * 나이(만)를 구한다.
	 * 
	 * @param birthYear
	 * @param birthMonth
	 * @param birthDate
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getAge(int birthYear, int birthMonth, int birthDate) {
		Date to = new Date(System.currentTimeMillis());
		return getAge(birthYear, birthMonth, birthDate, to.getYear() + 1900,
				to.getMonth() + 1, to.getDate());
	}

	/**
	 * 문자열날짜를 Date객체로 반환
	 * 
	 * @param str
	 *            문자열날짜
	 * @param pattern
	 *            DateFormat
	 * @param defaultDate
	 *            디폴트 Date
	 * @return
	 */
	public static java.util.Date toDate(String str, String pattern,
			Date defaultDate) {
		Date date = defaultDate;
		try {
			DateFormat dateFormat = new SimpleDateFormat(pattern);
			date = dateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 문자열을 Date객체로 반환.
	 * 
	 * @param str
	 *            yyyy-MM-dd HH:mm:ss 형식
	 * @return
	 */
	public static java.util.Date toDate(String str) {
		return toDate(str, "yyyy-MM-dd HH:mm:ss", new Date());
	}

	/**
	 * 문자열을 Date객체로 반환
	 * 
	 * @param str
	 *            yyyy-MM-dd HH:mm:ss 형식
	 * @param defaultDate
	 * @return
	 */
	public static java.util.Date toDate(String str, Date defaultDate) {
		return toDate(str, "yyyy-MM-dd HH:mm:ss", defaultDate);
	}

	/**
	 * Date객체로 반환
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static java.util.Date toDate(String year, String month, String day) {
		Date date = null;
		try {
			date = toDate(Integer.parseInt(year), Integer.parseInt(month),
					Integer.parseInt(day));
		} catch (NumberFormatException e) {
			date = new Date();
		}
		return date;
	}

	/**
	 * Date객체로 반환
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static java.util.Date toDate(int year, int month, int day, int hour,
			int minute, int second) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTime();
	}

	/**
	 * 입력받은 데이터를 java.util.Date형으로 변환한다.
	 * 
	 * @param year
	 *            년
	 * @param month
	 *            월
	 * @param day
	 *            일
	 * @param hour
	 *            시
	 * @param minute
	 *            분
	 * @return
	 */
	public static java.util.Date toDate(int year, int month, int day, int hour,
			int minute) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, 0);
		return calendar.getTime();
	}

	/**
	 * 입력받은 데이터를 java.util.Date형으로 변환한다.
	 * 
	 * @param year
	 *            년
	 * @param month
	 *            월
	 * @param day
	 *            일
	 * @return
	 */
	public static java.util.Date toDate(int year, int month, int day) {
		return toDate(year, month, day, 0, 0);
	}

	/**
	 * 시작일부터 오늘까지의 기간을 구한다.
	 * 
	 * @param year
	 *            시작한 년
	 * @param month
	 *            시작한 월
	 * @param day
	 *            시작한 일
	 * @param hour
	 *            시작한 시
	 * @param minute
	 *            시작한 분
	 * @return 기간의 1/1000초(ms)값
	 */
	public static long getPeriod(int year, int month, int day, int hour,
			int minute) {
		java.util.Calendar meet = java.util.Calendar.getInstance();
		meet.set(year, month - 1, day, hour, minute);
		return getPeriod(meet.getTime());
	}

	/**
	 * 시작일부터 오늘까지의 기간을 구한다.
	 * 
	 * @param year
	 *            시작한 년
	 * @param month
	 *            시작한 월
	 * @param day
	 *            시작한 일
	 * @return 기간의 1/1000초(ms)값
	 */
	public static long getPeriod(int year, int month, int day) {
		return getPeriod(year, month, day, 0, 0);
	}

	/**
	 * 시작일부터 오늘까지의 기간을 구한다.
	 * 
	 * @param year
	 *            시작한 년
	 * @param month
	 *            시작한 월
	 * @param day
	 *            시작한 일
	 * @return 기간의 일(day)값
	 */
	public static int getPeriodByDay(int year, int month, int day) {
		return (int) (getPeriod(year, month, day) / (24 * 60 * 60 * 1000));
	}

	/**
	 * 시작일부터 오늘까지의 기간을 구한다.
	 * 
	 * @param from
	 *            시작일
	 * @return 기간의 1/1000초(ms)
	 */
	public static long getPeriod(java.util.Date from) {
		return getPeriod(from, new java.util.Date());
	}

	/**
	 * 시작일부터 종료일까지의 기간을 구한다.
	 * 
	 * @param from
	 *            시작일
	 * @param to
	 *            종료일
	 * @return 기간의 1/1000초(ms)
	 */
	public static long getPeriod(java.util.Date from, java.util.Date to) {
		return to.getTime() - from.getTime();
	}

	/**
	 * 시작일부터 오늘까지의 기간을 일(day)로 반환한다.
	 * 
	 * @param from
	 *            시작일
	 * @return 기간의 일(day)수
	 */
	public static int getPeriodByDay(java.util.Date from) {
		return (int) (getPeriod(from) / (24 * 60 * 60 * 1000));
	}

	/**
	 * 시작일부터 종료일까지의 기간을 일(day)로 반환한다.
	 * 
	 * @param from
	 *            시작일
	 * @param to
	 *            종료일
	 * @return 기간의 일(day)수
	 */
	public static int getPeriodByDay(java.util.Date from, java.util.Date to) {
		return (int) (getPeriod(from, to) / (24 * 60 * 60 * 1000)); // 버그수정
	}

	/**
	 * 현재 해당하는 Locale의 날짜와 시간을 String으로 반환한다.
	 * 
	 * @return 날짜와 시간
	 */
	public static String getDateTimeString() {
		return df.format(new java.util.Date());
	}

	/**
	 * 날짜가 오늘인지 아닌지를 판별한다.
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date) {
		boolean result = false;
		if (date != null) {
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
					"yyyy-MM-dd", DateUtil.getLocale());
			result = formatter.format(date).equals(
					formatter.format(new java.util.Date()));
		}
		return result;
	}

	/**
	 * 현재 년을 반환한다.
	 * 
	 * @return int representation of current day with "yyyy".
	 */

	public static int getYear() {
		return getNumberByPattern("yyyy");
	}
	public static int getYear(String s_date) {
		int n_return = 0;
		
		try {
			if (s_date != null && !s_date.trim().equals("") && s_date.trim().length()>0) {
				String s_temp = s_date.trim().substring(0, 4);
				if (s_temp != null && !s_temp.trim().equals("") && s_temp.trim().length()>0) {
					n_return = Integer.parseInt(s_temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			n_return = 0;
		}
		
		return n_return;
	}

	/**
	 * 현재 달을 반환한다.
	 * 
	 * @return int representation of current day with "MM".
	 */
	public static int getMonth() {
		return getNumberByPattern("MM");
	}
	public static int getMonth(String s_date) {
		int n_return = 0;
		
		try {
			if (s_date != null && !s_date.trim().equals("") && s_date.trim().length()>0) {
				String s_temp = s_date.trim().substring(5, 7);
				if (s_temp != null && !s_temp.trim().equals("") && s_temp.trim().length()>0) {
					n_return = Integer.parseInt(s_temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			n_return = 0;
		}
		
		return n_return;
	}

	/**
	 * 현재 날을 반환한다.
	 * 
	 * @return int representation of current day with "dd".
	 */
	public static int getDay() {
		return getNumberByPattern("dd");
	}
	public static int getDay(String s_date) {
		int n_return = 0;
		
		try {
			if (s_date != null && !s_date.trim().equals("") && s_date.trim().length()>0) {
				String s_temp = s_date.trim().substring(8, 10);
				if (s_temp != null && !s_temp.trim().equals("") && s_temp.trim().length()>0) {
					n_return = Integer.parseInt(s_temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			n_return = 0;
		}
		
		return n_return;
	}

	/**
	 * pattern에 의한 날짜나 시간을 int형으로 반환한다.
	 * 
	 * @param pattern
	 *            "yyyy, MM, dd, HH, mm, ss and more"
	 * @return int representation of current day and time with your pattern.
	 */
	public static int getNumberByPattern(String pattern) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				pattern, DateUtil.getLocale());
		String dateString = formatter.format(new java.util.Date());
		return Integer.parseInt(dateString);
	}

	/**
	 * 현재 날짜를 yyyy-MM-dd 형식으로 반환한다
	 * 
	 * @return formatted string representation of current day with "yyyy-MM-dd".
	 */
	public static String getDateString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyy-MM-dd", DateUtil.getLocale());
		return formatter.format(new java.util.Date());
	}

	/**
	 * 현재 시간을 HH:mm:ss 형식으로 반환한다
	 * 
	 * @return formatted string representation of current time with "HH:mm:ss".
	 */
	public static String getTimeString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"HH:mm:ss", DateUtil.getLocale());
		return formatter.format(new java.util.Date());
	}

	/**
	 * 현재 날짜와 시간을 yyyy-MM-dd HH:mm:ss:SSS 형식으로 반환한다
	 * 
	 * @return formatted string representation of current time with
	 *         "yyyy-MM-dd HH:mm:ss:SSS".
	 */
	public static String getTimeStampString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SSS", DateUtil.getLocale());
		return formatter.format(new java.util.Date());
	}

	/**
	 * 현재 날짜와 시간을 yyyyMMddHHmmssSSS 형식으로 반환한다
	 * 
	 * @return formatted string representation of current time with
	 *         "yyyyMMddHHmmssSSS".
	 */
	public static String getUid() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyyMMddHHmmssSSS", DateUtil.getLocale());
		return formatter.format(new java.util.Date());
	}

	/**
	 * 현재 날짜나 시간을 pattern에 맞추어 반환한다.
	 * <p>
	 * 예) String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
	 * 
	 * @param pattern
	 *            "yyyy, MM, dd, HH, mm, ss and more"
	 * @return formatted string representation of current day and time with your
	 *         pattern.
	 */
	public static String getFormatString(String pattern) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				pattern, DateUtil.getLocale());
		String dateString = formatter.format(new java.util.Date());
		return dateString;
	}

	/**
	 * 현재 날짜나 시간을 pattern에 맞추어 반환한다.
	 * <p>
	 * 예) String time = DateTime.getFormatString("yyyy-MM-dd HH:mm:ss");
	 * 
	 * @param pattern
	 *            "yyyy, MM, dd, HH, mm, ss and more"
	 * @param date
	 * @return
	 */
	public static String getFormatString(String pattern, java.util.Date date) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				pattern, DateUtil.getLocale());
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 현재 날짜를 yyyyMMdd 형식으로 반환한다
	 * 
	 * @return formatted string representation of current day with "yyyyMMdd".
	 */
	public static String getShortDateString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyyMMdd", DateUtil.getLocale());
		return formatter.format(new java.util.Date());
	}

	/**
	 * 날짜를 yyyyMMdd 형식으로 반환한다
	 * 
	 * @return formatted string representation of current day with "yyyyMMdd".
	 */
	public static String getShortDateString(Date date) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyyMMdd", DateUtil.getLocale());
		return formatter.format(date);
	}

	/**
	 * 현재 시간을 HHmmss 형식으로 반환한다
	 * 
	 * @return formatted string representation of current time with "HHmmss".
	 */
	public static String getShortTimeString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"HHmmss", DateUtil.getLocale());
		return formatter.format(new java.util.Date());
	}

	/**
	 * 시간을 HHmmss 형식으로 반환한다
	 * 
	 * @return formatted string representation of current time with "HHmmss".
	 */
	public static String getShortTimeString(Date date) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"HHmmss", DateUtil.getLocale());
		return formatter.format(date);
	}

	/**
	 * check date string validation with an user defined format.
	 * 
	 * @param s
	 *            date string you want to check.
	 * @param format
	 *            string representation of the date format. For example,
	 *            "yyyy-MM-dd".
	 */
	public static void check(String s, String format)
			throws java.text.ParseException {
		if (s == null)
			throw new NullPointerException("date string to check is null");
		if (format == null)
			throw new NullPointerException(
					"format string to check date is null");

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				format, DateUtil.getLocale());
		java.util.Date date = null;
		try {
			date = formatter.parse(s);
		} catch (java.text.ParseException e) {
			throw new java.text.ParseException(e.getMessage()
					+ " with format \"" + format + "\"", e.getErrorOffset());
		}

		if (!formatter.format(date).equals(s))
			throw new java.text.ParseException("Out of bound date:\"" + s
					+ "\" with format \"" + format + "\"", 0);
	}

	/**
	 * 날수를 더한 날짜를 리턴
	 * 
	 * @param date
	 *            날짜 yyyymmdd
	 * @param addDay
	 *            추가일수
	 * @return yyyyMMdd
	 */
	public static String getDateAdd(String date, int addDay) {
		if (date.length() != 8) {
			date = getFormatString("yyyyMMdd");
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(date
				.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1,
				Integer.parseInt(date.substring(6, 8)));
		cal.add(Calendar.DATE, addDay);
		return formatter.format(cal.getTime());
	}

	/**
	 * 달수를 더한 날짜를 리턴
	 * 
	 * @param date
	 *            날짜 yyyymmdd
	 * @param addMonth
	 *            추가월수
	 * @return yyyyMMdd
	 */
	public static String getDateAddByMonth(String date, int addMonth) {
		if (date.length() != 8) {
			date = getFormatString("yyyyMMdd");
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(date
				.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1,
				Integer.parseInt(date.substring(6, 8)));
		cal.add(Calendar.MONTH, addMonth);
		return formatter.format(cal.getTime());
	}

	/**
	 * 특정월의 마지막 날
	 * 
	 * @param dateYM
	 *            yyyymm
	 * @return 마지막날짜 yyyymmdd
	 */
	public static String getLastDayOfMonth(String dateYM) {
		// 마지막날 구하기
		String nextYMD = getDateAddByMonth(dateYM + "01", 1); // 다음달
																// 1일
		return getDateAdd(nextYMD, -1); // 선택한 월의 마지막날
	}

	/**
	 * 특정일의 요일을 반환
	 * 
	 * @param inputDate
	 *            yyyyMMdd
	 * @return 1~7 (일~토)
	 */
	public static int getDayOfWeek(String inputDate) {

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date myDate;
		try {
			myDate = df.parse(inputDate);
		} catch (ParseException e) {
			return 0;
		}
		Calendar cld = Calendar.getInstance();
		cld.setTime(myDate);

		return cld.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 날짜에 달을 더함
	 * 
	 * @param date
	 * @param addMonth
	 * @return
	 */
	public static Date getDateAddMonth(Date date, int addMonth) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, addMonth);
		return cal.getTime();
	}

	/**
	 * 날수를 더한 날짜를 리턴
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param addDay
	 * @return yyyyMMdd
	 */
	public static String getDateAdd(String year, String month, String day,
			int addDay) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(year),
				Integer.parseInt(month) - 1, Integer.parseInt(day));
		cal.add(Calendar.DATE, addDay);
		return formatter.format(cal.getTime());
	}

	/**
	 * 오늘의 week정보를 리턴
	 * 
	 * @return 1(일),..,7(토)
	 */
	public static int getCurWeek() {
		Calendar rightNow = Calendar.getInstance();
		return rightNow.get(Calendar.DAY_OF_WEEK); // 일~토:1~7

	}

	/**
	 * 해당일의 요일정보 리턴
	 * 
	 * @param sY
	 * @param sM
	 * @param sD
	 * @return 1(일),..,7(토)
	 */
	public static int getSelWeek(String sY, String sM, String sD) {
		GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(sY),
				Integer.parseInt(sM) - 1, Integer.parseInt(sD));
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 선택한 날이 속한 주의 모든 날짜를 가져옴
	 * 
	 * @param sY
	 * @param sM
	 * @param sD
	 * @return String[7]
	 */
	public static String[] selWeekDayRange(String sY, String sM, String sD) {
		String[] sa = new String[7];

		// int i = getCurWeek(); // 1~7
		int i = getSelWeek(sY, sM, sD);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(sY),
				Integer.parseInt(sM) - 1, Integer.parseInt(sD));
		cal.add(Calendar.DAY_OF_MONTH, -i + 1); // week First Day

		sa[0] = formatter.format(cal.getTime());
		for (int j = 1; j <= 6; j++) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			sa[j] = formatter.format(cal.getTime());
		}

		return sa;
	}

	/**
	 * <p>
	 * DateNum 값에 따른 날짜 포멧으로 현재 Date return.
	 * </p>
	 * 
	 * <blockquote>
	 * 
	 * <p>
	 * 예) nowDate(0) = "yyyyMMddHHmmss"
	 * </p>
	 * <p>
	 * 예) nowDate(1) = "yyyy-MM-dd HH:mm:ss"
	 * </p>
	 * <p>
	 * 예) nowDate(2) = "yyyy/MM/dd HH:mm:ss"
	 * </p>
	 * <p>
	 * 예) nowDate(3) = "yyyyMMdd"
	 * </p>
	 * <p>
	 * 예) nowDate(4) = "yyyy-MM-dd"
	 * </p>
	 * <p>
	 * 예) nowDate(5) = "yyyy"
	 * </p>
	 * <p>
	 * 예) nowDate(6) = "MM"
	 * </p>
	 * <p>
	 * 예) nowDate(7) = "dd"
	 * </p>
	 * <p>
	 * 예) nowDate(8) = "HH:mm"
	 * </p>
	 * <p>
	 * 예) nowDate(9) = "HH:mm:ss"
	 * </p>
	 * 
	 * </blockquote>
	 * 
	 * @param DateNum -
	 *            날짜 포멧을 구분하는 구분자
	 * 
	 * @return String - 구분자에 따른 날짜 포멧으로 현재 Date return
	 * 
	 */
	public static String nowDate(int DateNum) {
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		SimpleDateFormat sf = null;
		if (DateNum == 0) {
			sf = new SimpleDateFormat("yyyyMMddHHmmss");
		} else if (DateNum == 1) {
			sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (DateNum == 2) {
			sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		} else if (DateNum == 3) {
			sf = new SimpleDateFormat("yyyyMMdd");
		} else if (DateNum == 4) {
			sf = new SimpleDateFormat("yyyy-MM-dd");
		} else if (DateNum == 5) {
			sf = new SimpleDateFormat("yyyy");
		} else if (DateNum == 6) {
			sf = new SimpleDateFormat("MM");
		} else if (DateNum == 7) {
			sf = new SimpleDateFormat("dd");
		} else if (DateNum == 8) {
			sf = new SimpleDateFormat("HH");
		} else if (DateNum == 9) {
			sf = new SimpleDateFormat("mm");
		} else if (DateNum == 10) {
			sf = new SimpleDateFormat("HH:mm");
		} else if (DateNum == 11) {
			sf = new SimpleDateFormat("HH:mm:ss");
		} else if (DateNum == 12) {
			sf = new SimpleDateFormat("yyMMddHHmmsss");
		} else if (DateNum == 13) {
			sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		} else if (DateNum == 14) {
			sf = new SimpleDateFormat("yy");
		} else if (DateNum == 15) {
			sf = new SimpleDateFormat("ss");
		}

		return sf.format(date);
	}

	/**
	 * <p>
	 * 오늘의 요일 가져오기
	 * </p>
	 * 
	 * @return String - 오늘의 요일 return
	 * 
	 */
	public static String nowDayOfWeek() {

		GregorianCalendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_WEEK);

		String days = "";

		if (day == 1)
			days = "일";
		else if (day == 2)
			days = "월";
		else if (day == 3)
			days = "화";
		else if (day == 4)
			days = "수";
		else if (day == 5)
			days = "목";
		else if (day == 6)
			days = "금";
		else if (day == 7)
			days = "토";

		return days;
	}
	
	/**
	 * <p>
	 * 현재 날짜에 따른 전달 날짜(yyyy-MM-dd)
	 * </p>
	 * 
	 * <blockquote>
	 * 
	 * <p>
	 * 예) nowDate(1) = 하루전
	 * </p>
	 * <p>
	 * 예) nowDate(2) = 일주일전
	 * </p>
	 * <p>
	 * 예) nowDate(3) = 한달전
	 * </p>
	 * <p>
	 * 예) nowDate(4) = 하루후
	 * </p>
	 * <p>
	 * 예) nowDate(5) = 일주일후
	 * </p>
	 * <p>
	 * 예) nowDate(6) = 한달후
	 * </p>
	 * 
	 * </blockquote>
	 * 
	 * @param 
	 * 
	 * @return String - 구분자에 따른 날짜 포멧으로 현재 Date return
	 * 
	 */
	public String calDate(int Type){ 
        //날짜 계산에 필요한 변수 선언 
        Calendar cal = Calendar.getInstance();                 
        String days = ""; 
        String month = ""; 
        String year = ""; 
                                  
        if(Type == 1){// 하루전 날짜 
	        cal.add(Calendar.DATE, -1);
	    }else if(Type == 2){// 일주일전 날짜 
	    	cal.add(Calendar.DATE, -7);  
	    }else if(Type == 3){// 한달전 날짜 
	    	cal.add(Calendar.MONTH, -1);
	    }else if(Type == 4){// 하루후 날짜 
	    	cal.add(Calendar.DATE, 1); 
	    }else if(Type == 5){// 일주일후 날짜 
	    	cal.add(Calendar.DATE, 7); 
	    }else if(Type == 6){// 한달후 날짜 
	    	cal.add(Calendar.MONTH, 1);
	    }
            
        // Step2. 날짜 저장 (일-월-년 순) 
        days = String.valueOf( cal.get(Calendar.DATE) ); 
        month = String.valueOf( cal.get(Calendar.MONTH) + 1 ); 
        year = String.valueOf( cal.get(Calendar.YEAR) ); 

                     
        // Step3-1.Days를 두자리로 바꿈 [예 : 1 -> 01] 
        if( days.length() == 1 ){ 
        	days = "0" + days; 
        }// End if 
             
        // Step3-2.Month를 두자리로 바꿈 [예 : 1 -> 01]                        
        if( month.length() == 1 ){ 
        	month = "0" + month;
        }

        // Step4. 날짜 반환 
        return year + "-" + month + "-" + days; 

	}// End calDate 
	
	/**
	 * <p>
	 * MILLISECOND 구하기
	 * </p>
	 * 
	 * @return String
	 * 
	 */
	public String getMillisecond(){
		Calendar c = Calendar.getInstance();
		
		String strTime = nowDate(15);
		String str_mil  = "" ;
		int mil = c.get(Calendar.MILLISECOND);
		
		if( mil < 10 )
			str_mil = "00" + mil;
		else if( mil < 100 )
			str_mil = "0" + mil;
		else
			str_mil = "" + mil;

		return strTime + str_mil;
	}
	
	/**
	 * <p>
	 * 파라미터의 해당하는 년월의 전달을 구한다.(리포트 출력용으로 사용)
	 * </p>
	 * 
	 * @return String
	 * 
	 */
	public static String getBeforeYearMonthByYM(String yearMonth, int minVal){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(yearMonth.substring(0,4));
		int month = Integer.parseInt(yearMonth.substring(4,6));

		cal.set(year, month-minVal, 0);

		String beforeYear = dateFormat.format(cal.getTime()).substring(0,4); 
		String beforeMonth = dateFormat.format(cal.getTime()).substring(5,7); 
		String retStr = beforeYear + "-" + beforeMonth;

		return retStr;
	}
	
	/**
	 * <p>
	 * 현재 년월의 전달을 구한다.(리포트 출력용으로 사용)
	 * </p>
	 * 
	 * @return String
	 * 
	 */
	public static String getBeforeYearMonthByYM(int minVal){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, -minVal);  

		String beforeYear = dateFormat.format(cal.getTime()).substring(0,4); 
		String beforeMonth = dateFormat.format(cal.getTime()).substring(5,7); 

		String retStr = beforeYear + "-" + beforeMonth;

		return retStr;
	}
	
	/**
	 * <p>
	 * 인수로 yyyyMMddHHmmss형태의 일시를 받아 yyyy-MM-dd형태의 String으로 return
	 * </p>
	 * 
	 * @return String
	 * 
	 */
	public static String dateDashFormat(String dateTime){
		String day = null;
		day = dateTime.substring(0, 4)+"-"+dateTime.substring(4, 6)+"-"+dateTime.substring(6, 8);
		return day;
	}
	
	/**
	 * <p>
	 * 인수로 yyyyMMddHHmmss형태의 일시를 받아 yyyy-MM-dd형태의 String으로 return
	 * </p>
	 * 
	 * @return String
	 * 
	 */
	public static String[] dateArrFormat(String dateTime){
		String[] day = new String[3];

		day[0] = dateTime.substring(0,4);
		day[1] = dateTime.substring(4,6);
		day[2] = dateTime.substring(6,8);

		return day;
	}
	
	public static double getNumberMilliSecondsByStringTime(String sTime) {
		double n_return = 0;
		
		if (sTime != null && !sTime.trim().equals("") && sTime.trim().length()>0) {
			String s_hour = sTime.substring(0, 2);
			String s_min = sTime.substring(3, 5);
			String s_sec = sTime.substring(6, 8);
			String s_mil = sTime.substring(9, 12);
			
			int n_hour=0, n_min=0, n_sec=0, n_mil=0;
			if (s_hour != null && !s_hour.trim().equals("") && s_hour.trim().length()>0) {
				n_hour = Integer.parseInt(s_hour);
			}			
			if (s_min != null && !s_min.trim().equals("") && s_min.trim().length()>0) {
				n_min = Integer.parseInt(s_min);
			}			
			if (s_sec != null && !s_sec.trim().equals("") && s_sec.trim().length()>0) {
				n_sec = Integer.parseInt(s_sec);
			}			
			if (s_mil != null && !s_mil.trim().equals("") && s_mil.trim().length()>0) {
				n_mil = Integer.parseInt(s_mil);
			}			
			
			n_return = ((double)n_hour * 60 * 60) + ((double)n_min * 60) + (double)n_sec + (double)n_mil/(double)1000;
		}
		
		return n_return;
	}

	
	public static SearchVo setSearchTerm(String getSearchTermType) {
		SearchVo search = new SearchVo();
		
		Calendar c = Calendar.getInstance();
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String today = DateUtil.nowDate(4);
		
		if("dayTerm".equals(getSearchTermType)) {
			search.setSearchStartDate(today);
			search.setSearchEndDate(today);
		} else if("weekTerm".equals(getSearchTermType)) {
			c.add(Calendar.DATE, -7);
			Date date = c.getTime();
			search.setSearchStartDate(sf.format(date));
			search.setSearchEndDate(today);
		} else if("monthTerm".equals(getSearchTermType)) {
			c.add(Calendar.MONTH, -1);
			Date date = c.getTime();
			search.setSearchStartDate(sf.format(date));
			search.setSearchEndDate(today);
		} else if("yearTerm".equals(getSearchTermType)) {
			c.add(Calendar.YEAR, -1);
			Date date = c.getTime();
			search.setSearchStartDate(sf.format(date));
			search.setSearchEndDate(today);
		} else {
			if(search != null && search.getSearchStartDate() == null || "".equals(search.getSearchStartDate()) 
					&& search.getSearchEndDate() == null || "".equals(search.getSearchEndDate())) {
				c.add(Calendar.MONTH, -1);
				c.add(Calendar.DATE, +1);
				Date date = c.getTime();
				search.setSearchStartDate(sf.format(date));
				search.setSearchEndDate(today);
			} 
		}
		
		return search;
	}
	
	
	public static String gmtToKst(String fromDate) {

		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
		fromFormat.setTimeZone(TimeZone.getTimeZone("GMT")); 

		Date date = null;
		try {
		    date = fromFormat.parse(fromDate);
		} catch (ParseException e) {
		    e.printStackTrace();
		}

		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return toFormat.format(date); 
	}
	
	
	
	/**
	 * String writeDate = "2018-07-14T17:45:55.9483536";		
	 * LocalDateTime ldt = gmtToKstLdt(writeDate);
	   String format = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	 * @param fromDate
	 * @return
	 */
	public static LocalDateTime gmtToKstLdt(String fromDate) {
		
		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
		fromFormat.setTimeZone(TimeZone.getTimeZone("GMT")); 
		
		Date date = null;
		try {
			date = fromFormat.parse(fromDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}