/**
 *
 */
package com.vrseen.vrstore.util;

import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static final String[] WEEK_STR = new String[]{"", "星期一", "星期二",
            "星期三", "星期四", "星期五", "星期六", "星期天"};

    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    /**
     * 一小时的毫秒数
     */
    public static final long HOUR = MINUTE * 60;
    /**
     * 一天的毫秒数
     */
    public static final long DAY = HOUR * 24;
    /**
     * 一星期的毫秒数
     */
    public static final long WEEK = DAY * 7;

    /**
     * 一个月的毫秒数
     */
    public static final long MONTH = 30 * DAY;
    /**
     * 半年的毫秒数
     */
    public static final long HALF_YEAR = 6 * MONTH;
    /**
     * 一年的毫秒数
     */
    public static final long YEAR = DAY * 365; // or 366 ???

    final static public String FULL_ST_FORMAT = "yyyy-MM-dd HH:mm:ss";
    final static public String FULL_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    final static public String FULL_JSON_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    final static public String FULL_J_FORMAT = "yyyy/MM/dd HH:mm:ss";
    final static public String CURRENCY_ST_FORMAT = "yyyy-MM-dd HH:mm";
    final static public String CURRENCY_J_FORMAT = "yyyy/MM/dd HH:mm";
    final static public String DATA_FORMAT = "yyyyMMddHHmmss";
    final static public String FULL_DATA_FORMAT = "yyyyMMddHHmmssSSS";
    final static public String ST_FORMAT = "yyyy-MM-dd HH:mm";
    final static public String ST_CN_FORMAT = "yyyy年MM月dd日 HH:mm";
    final static public String CN_FORMAT = "yy年MM月dd日HH:mm";
    final static public String CN_FULL_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
    final static public String DAY_FORMAT = "yyyy-MM-dd";
    final static public String SHORT_DATE_FORMAT = "yy-MM-dd";
    final static public String YEAR_FORMAT = "yyyy";
    final static public String CN_YEAR_FORMAT = "yyyy年";
    final static public String MOUTH_DATE_TIME = "MM 月dd 日 HH:mm";
    final static public String MOUTH_DATE = "MM月dd日";
    final static public String CN_MOUTH = "yyyy年MM月";
    final static public String MOUTH_FORMAT = "yyyy-MM";
    final static public String TIME_FORMAT = "HH:mm";
    final static public String CHINESE_MOUTH_FORMAT = "yyyy年MM月";
    final static public String CHINESE_YYYYMM = "yyyy年MM月";
    final static public String CHINESE_YYYYMMDD = "yyyy年MM月dd日";


    public static String getTimeOfHHMMSS(long time)
    {
        String sh = "";
        String sm = "";
        String ss = "";
        long hour = time / (60 * 60 * 1000);
        long minute = (time - hour*60*60*1000)/(60*1000);
        long second = (time - hour*60*60*1000 - minute*60*1000)/1000;
        if(second >= 60 )
        {
            second = second % 60;

            minute+=second/60;
        }

        if(minute >= 60)
        {
            minute = minute %60;

            hour += minute/60;
        }

        if(hour <10)
        {
            sh = "0" + String.valueOf(hour);

        }else
        {
            sh = String.valueOf(hour);

        }
        if(minute <10)
        {
            sm = "0" + String.valueOf(minute);

        }else
        {
            sm = String.valueOf(minute);

        }
        if(second <10)
        {
            ss = "0" + String.valueOf(second);

        }else
        {
            ss = String.valueOf(second);

        }

        return (sh + ":" + sm+ ":" + ss);

    }

    /**
     * 将秒格式化为00:00形式
     *
     * @param sec
     * @return
     */
    public static String formatSecond(long sec) {
        StringBuffer buf = new StringBuffer();
        buf.append(sec / 600);
        buf.append(sec % 600 / 60);
        buf.append(":");
        buf.append(sec % 60 / 10);
        buf.append(sec % 10);
        return buf.toString();
    }

    /**
     * 将毫秒格式化为00:00:00形式
     *
     * @param mill
     * @return
     */
    public static String formatMillisecond(long mill) {
        long hour = (mill / (60 * 60 * 1000));
        long minute = ((mill - hour * 60 * 60 * 1000) / (60 * 1000));
        long seconds = ((mill - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000);
        if (seconds >= 60) {
            seconds = seconds % 60;
            minute += seconds / 60;
        }
        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }
        String sh = "";
        String sm = "";
        String ss = "";
        if (hour < 10) {
            sh = "0" + String.valueOf(hour);
        } else {
            sh = String.valueOf(hour);
        }
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (seconds < 10) {
            ss = "0" + String.valueOf(seconds);
        } else {
            ss = String.valueOf(seconds);
        }

        return sh + ":" + sm + ":" + ss;
    }

    /**
     * 格式化时间间隔
     *
     * @param timeDiff
     * @return 返回如 刚刚；xx秒前；xx分前；xx小时前；xx天前；xx月前；半年前；xx年前；
     */
    public static String formatTimeDiff(Long timeDiff) {
        if (timeDiff / YEAR > 0) {
            return timeDiff / YEAR + "年前";
        }
        if (timeDiff / HALF_YEAR > 0) {
            return "半年前";
        }
        if (timeDiff / MONTH > 0) {
            return timeDiff / MONTH + "个月前";
        }
        if (timeDiff / WEEK > 0) {
            return timeDiff / WEEK + "星期前";
        }
        if (timeDiff / DAY > 0) {
            return timeDiff / DAY + "天前";
        }
        if (timeDiff / HOUR > 0) {
            return timeDiff / HOUR + "小时前";
        }
        if (timeDiff / 60 / 1000 > 0) {
            return timeDiff / 60 / 1000 + "分钟前";
        }
        if (timeDiff / 1000 < 10) {
            return "刚刚";
        }

        return timeDiff / 1000 + "秒前";
    }

    /**
     * 格式化最近消息时间，当天的消息只返回时间，昨天的信息返回"昨天",当前星期内的消息返回星期，其他返回年月日14-04-04
     *
     * @param date
     * @return
     */
    public static String formatLastMessageTime(Date date) {
        if (date == null) {
            return "";
        }
        if (isInToday(date)) {
            return format(TIME_FORMAT, date);
        } else if (compareDay(date, getTodayEndTime()) < 2) {
            return "昨天";
        } else if (isInThisWeek(date)) {
            int past = getDAY_OF_WEEK(date);
            return WEEK_STR[past];
        } else {
            return format(SHORT_DATE_FORMAT, date);
        }
    }

    /**
     * 格式化拍卖列表日期时间，当天值返回时间，第二天的信息返回“明日 时间”，
     * 其他时间返回“X月X日 XX:XX”
     * @param date
     * @return
     */
//    public static String formatAuctionCarStartTime(long publicAtTime) {
//        final DateTime dateTime = new DateTime(publicAtTime);
//
//        final int daysBetween = getNumberOfDayAfterNow(publicAtTime);
//
//        if (daysBetween == 0) {
//            return dateTime.toString(TIME_FORMAT);
//        } else if (daysBetween == 1) {
//            return dateTime.toString("明日 HH:mm");
//        } else {
//            return dateTime.toString("MM-dd HH:mm");
//        }
//    }
//
//    /**
//     * futureTime的那天是今天之后的第几天
//     * @param futureTime 未来的某个时间
//     * @return 间隔
//     */
//    private static int getNumberOfDayAfterNow(long futureTime) {
//        final LocalDate start = new LocalDate();
//        final LocalDate end = new LocalDate(futureTime);
//        return Days.daysBetween(start, end).getDays();
//    }

    /**
     * 是否在当前星期，第一天为星期一
     *
     * @param date
     * @return
     */
    public static boolean isInThisWeek(Date date) {
        boolean in = true;
        int now = getDAY_OF_WEEK();
        in = compareDay(date, getTodayEndTime()) - now < 0;

        return in;
    }

    /**
     * @param date 日期
     * @param time 时间
     * @return Calendar 合并日期和时间
     */
    public static Calendar mergeDateTime(Date date, Time time) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        if (time != null) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, temp.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, temp.get(Calendar.MILLISECOND));
        }
        return cal;
    }

    /**
     * 用给定的格式的字符串返回日期对象
     *
     * @param
     * @return
     */
    public static Date covertStrToDate(String date, String format)
            throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date1 = dateFormat.parse(date);
        return date1;
    }

    /**
     * 判断输入的字符串格式的时间信息是否正确有效
     *
     * @param date
     * @param format
     * @return
     */
    public static boolean isDateStrValid(String date, String format)
            throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        date = date.trim();
        Date d = dateFormat.parse(date);
        String parsedDate = dateFormat.format(d);
        return date.equals(parsedDate);
    }

    /**
     * 得到标准的日期
     *
     * @return String
     */
    public static String getDateST() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.CHINA);
        return dateFormat.format(new Date());
    }

    /**
     * 得到标准的日期 时间
     *
     * @return String
     */
    public static String getDateTimeST() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FULL_ST_FORMAT);
        return dateFormat.format(new Date());
    }

    /**
     * @param format 格式
     * @return String 格式化日期时间
     */
    public static String format(String format) {
        return format(format, new Date());
    }

    /**
     * 得到指定时间的时间日期格式
     *
     * @param date   指定的时间
     * @param format 时间日期格式
     * @return
     */
    public static String getFormatDateTime(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * @param format 格式
     * @param date   日期
     * @return String 格式化日期时间
     */
    public static String format(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(date);
    }

    /**
     * 得到年
     *
     * @return int
     */
    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 得到月
     *
     * @return int
     */
    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到号数
     *
     * @return int
     */
    public static int getDate() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 得到号数
     *
     * @return int
     */
    public static int getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    public static int getHour() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.HOUR);
    }

    public static int getMinute() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecond() {
        Calendar calendar = Calendar.getInstance();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 得到今天是月中的第几天
     *
     * @return int
     */
    public static int getDAY_OF_MONTH() {
        return getDAY_OF_MONTH(new Date());
    }

    /**
     * @param trialTime 日期
     * @return 得到月中的第几天
     */
    public static int getDAY_OF_MONTH(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return int 得到今天周中的第几天,7 为星期天
     */
    public static int getDAY_OF_WEEK() {
        return getDAY_OF_WEEK(new Date());
    }

    /**
     * @param trialTime 日期
     * @return 得到周中的第几天 ,7 为星期天
     */
    public static int getDAY_OF_WEEK(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : calendar
                .get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * @return int 得到今天早上下午
     */
    public static int getAM_PM() {
        return getAM_PM(new Date());
    }

    /**
     * AM = 0 PM = 1;
     *
     * @param trialTime 日期
     * @return int 得到早上下午
     */
    public static int getAM_PM(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime);
        return calendar.get(Calendar.AM_PM);
    }

    /**
     * @param trialTime 日期
     * @return 得到月分的最大天数
     */
    public static int getCountMonthDay(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); // 放入你的日期
        return calendar.getMaximum(Calendar.DATE);
    }

    /**
     * @param date 日期
     * @return 取得当前日期所在周的第一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * @param date 日期
     * @return 取得当前日期所在周的最后一天
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        c.add(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * @param date 日期
     * @return 取得当前日期是第几周
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * @param year 年
     * @return 得到某一年周的总数
     */
    public static int getCountWeekOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        return getWeekOfYear(c.getTime());
    }

    /**
     * @param trialTime 日期
     * @return 得到一个月开始的时间日期
     */
    public static Date getBeginMonthDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); // 放入你的日期
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param trialTime 日期
     * @return 得到一个月结束的时间日期
     */
    public static Date getEndMonthDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); // 放入你的日期
        calendar.set(Calendar.DATE,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * @param trialTime 日期
     * @return 得到一个年开始的时间日期
     */
    public static Date getBeginYearDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); // 放入你的日期
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param year 年份
     * @return 得到一个年开始的时间日期
     */
    public static Date getBeginYearDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param trialTime 日期
     * @return 得到一年结束的时间日期
     */
    public static Date getEndYearDate(Date trialTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trialTime); // 放入你的日期
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
        calendar.add(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * @param year 年份
     * @return 得到一年结束的时间日期
     */
    public static Date getEndYearDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
        calendar.add(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 得到变化年的日期
     *
     * @param itmp 偏移量
     * @return Date 需要偏移的日期
     */
    public static Date getYear(int itmp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, itmp);
        return calendar.getTime();
    }

    /**
     * @param itmp 偏移量
     * @param date 需要偏移的日期
     * @return 得到变化年的月
     */
    public static Date getMonth(int itmp, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, itmp);
        return calendar.getTime();
    }


    /**
     * @param itmp 偏移量
     * @return Date 得到变化号数的日期
     */
    public static Date getDate(int itmp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, itmp);
        return calendar.getTime();
    }

    /**
     * @param itmp 偏移量
     * @param date 初始日期
     * @return Date 得到变化号数的日期
     */
    public static Date getDate(int itmp, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, itmp);
        return calendar.getTime();
    }

    /**
     * @param yeas  年
     * @param month 月
     * @param date  日
     * @return 日期时间，得到偏移时间
     */
    public static Date getMoveDate(int yeas, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, yeas);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, date);
        return calendar.getTime();
    }

    /**
     * @param startDate 起始时间
     * @param yeas      年
     * @param month     月
     * @param date      日
     * @param hour      小时
     * @param min       分钟
     * @param sec       秒
     * @return
     */
    public static Date getMoveDate(Date startDate, int yeas, int month,
                                   int date, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, yeas);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, date);
        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, min);
        calendar.add(Calendar.SECOND, sec);
        return calendar.getTime();
    }

    /**
     * @param date 日期
     * @return 得到这天开始的时间
     */
    public static Date getBeginDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date 日期
     * @return 得到当前时间的开始小时时间，如2012-09-09 13:33:33 返回2012-09-09 13:00:00
     */
    public static Date getBeginHourDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date 日期
     * @return 得到这天最后结束的时间
     */
    public static Date getEndDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getBeginDateTime(date));
        calendar.set(Calendar.AM_PM, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 判断指定的时间是否是今天
     *
     * @param date 指定的时间
     * @return true:是今天,false:非今天
     */
    public static boolean isInToday(Date date) {
        boolean flag = false;
        Date now = new Date();
        String fullFormat = getFormatDateTime(now, DateUtils.DAY_FORMAT);
        String beginString = fullFormat + " 00:00:00";
        String endString = fullFormat + " 23:59:59";
        DateFormat df = new SimpleDateFormat(DateUtils.FULL_ST_FORMAT);
        try {
            Date beginTime = df.parse(beginString);
            Date endTime = df.parse(endString);
            flag = date.before(endTime) && date.after(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 得到今天的最后结束时间
     *
     * @return 今天的最后时间
     */
    public static Date getTodayEndTime() {
        String endString = format(DateUtils.DAY_FORMAT, new Date())
                + " 23:59:59";
        DateFormat df = new SimpleDateFormat(DateUtils.FULL_ST_FORMAT);
        Date endTime = new Date();
        try {
            endTime = df.parse(endString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    /**
     * 得到今天的开始时间
     *
     * @return 今天的开始时间
     */
    public static Date getTodayBeginTime() {
        String endString = format(DateUtils.DAY_FORMAT, new Date())
                + " 00:00:00";
        DateFormat df = new SimpleDateFormat(DateUtils.FULL_ST_FORMAT);
        Date endTime = new Date();
        try {
            endTime = df.parse(endString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    /**
     * 比较较两个日期,返回天数差,小于24小时不算一天；只计算日期使用compareDate
     *
     * @param beginDate 开始日期时间
     * @param endDate   结束日期时间
     * @return int
     */
    public static long compareDay(Date beginDate, Date endDate) {
        Calendar endDateYears = new GregorianCalendar();
        endDateYears.setTime(endDate);
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        long diffMillis = endDateYears.getTimeInMillis()
                - beginYears.getTimeInMillis();
        return diffMillis / DAY;
    }

    /**
     * 判断是否属于这个日期范围
     *
     * @param date
     * @param beginDate
     * @param endDate
     * @return 0:在两个时间中; -1:在开始时间之前; 1:在结束时间之后
     */
    public static Integer inDate(Date date, Date beginDate, Date endDate) {
        if (date.after(beginDate) && date.before(endDate)) {
            return 0;
        } else if (date.after(endDate)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 返回日期(只计算日期)差，date减去inDate
     *
     * @param date
     * @param inDate
     * @return 0:今天; 负数:在开始时间之前; 正数:在结束时间之后
     */
    public static Integer compareDate(Date date, Date inDate) {
        long diff = getBeginDateTime(date).getTime() - getBeginDateTime(inDate).getTime();
        return (int) (diff / DAY);
    }

    /**
     * 判断当前时间是否属于这个日期范围
     *
     * @param beginDate
     * @param endDate
     * @return 0:在两个时间中; -1:在开始时间之前; 1:在结束时间之后
     */
    public static Integer inDate(Date beginDate, Date endDate) {
        Date currentDate = getDate(0);
        if (currentDate.after(beginDate) && currentDate.before(endDate)) {
            return 0;
        } else if (currentDate.after(endDate)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 比较指定日期与当前日期
     *
     * @param beginDate 开始日期时间
     * @return 当前日期-指定日期的天数差
     */
    public static long compareDay(Date beginDate) {
        return compareDay(beginDate, new Date());
    }

    public static long compareHour(Date beginDate) {
        return compareHour(beginDate, new Date());
    }

    public static long compareHour(Date beginDate, Date endDate) {
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        long diffMillis = endDate.getTime() - beginYears.getTimeInMillis();
        return diffMillis / (60 * 60 * 1000);
    }

    /**
     * 比较当前系统时间与参数时间的差，结果为系统时间-参数时间
     *
     * @param beginDate
     * @return
     */
    public static long compareMillis(Date beginDate) {
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        return System.currentTimeMillis() - beginYears.getTimeInMillis();
    }

    /**
     * 计算两个时间的差，结果为endDate-beginDate
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static long compareMillis(Date beginDate, Date endDate) {
        Calendar beginYears = new GregorianCalendar();
        beginYears.setTime(beginDate);
        Calendar endYears = new GregorianCalendar();
        endYears.setTime(endDate);
        return endYears.getTimeInMillis() - beginYears.getTimeInMillis();
    }

    /**
     * 判断是否属于这个日期范围,txweb 标题中判断是否 是新的
     *
     * @param date 创建日期
     * @param iday 天数
     * @return boolean
     */
    static public boolean inDate(Date date, int iday) {
        return compareDay(date) < iday;
    }

    /**
     * 返回一个String类型的他们之间的时间差 只到小时 例如:38:15:00(三十八小时15分) 如果获取的当前日期在后面 则返回0
     *
     * @param date 比较的实际
     * @return 字符串
     */
    public static String minus(Date date) {
        Date now = new Date();
        if (now.after(date)) {
            return "0";
        } else {
            long time = date.getTime() - now.getTime();
            int hour = (int) (time / (60 * 60 * 1000));
            int minute = (int) ((time % (60 * 60 * 1000)) / (60 * 1000));
            int second = (int) (((time % (60 * 60 * 1000)) % (60 * 1000)) / 1000 + 1);
            if (second == 60) {
                second = 0;
                minute += 1;
            }
            if (minute == 60) {
                minute = 0;
                hour += 1;
            }
            return "" + (hour < 10 ? ("0" + hour) : hour) + ":"
                    + (minute < 10 ? ("0" + minute) : minute) + ":"
                    + (second < 10 ? ("0" + second) : second);
        }
    }

    /**
     * 返回时长
     */
    public static String getTimeLength(long time) {
        StringBuffer timeLengthBuffer = new StringBuffer();
        long year = time / (365 * 24 * 3600);
        time = time % (365 * 24 * 3600);
        long month = time / (30 * 24 * 3600);
        time = time % (30 * 24 * 3600);
        long day = time / (24 * 3600);
        time = time % (24 * 3600);
        long hour = time / 3600;
        time = time % 3600;
        long min = time / 60;
        time = time % 60;
        long sec = time;
        timeLengthBuffer.append(year == 0 ? "" : year + "年");
        timeLengthBuffer.append(month == 0 ? "" : year + "月");
        timeLengthBuffer.append(day == 0 ? "" : year + "天");
        timeLengthBuffer.append(hour == 0 ? "" : hour + "小时");
        timeLengthBuffer.append(min == 0 ? "0分" : min + "分钟");
        timeLengthBuffer.append(sec == 0 ? "0秒" : sec + "秒");
        return timeLengthBuffer.toString();
    }

    /**
     * 得到年
     *
     * @return int
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 得到月
     *
     * @return int
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取yyyy-MM的月份
     * @param dateStr
     * @return
     */
    public static int getMonth(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date date = sdf.parse(dateStr);
            return getMonth(date);
        } catch (ParseException e) {
            Log.e("DateUtils", "parse dateStr error");
            e.printStackTrace();
        }
        return -1;
    }

    public static String getAndFormatMonth(String dateStr) {
        int month = getMonth(dateStr);
        String monthStr = "";
        switch (month) {
            case 1:
                monthStr = "一";
                break;
            case 2:
                monthStr = "二";
                break;
            case 3:
                monthStr = "三";
                break;
            case 4:
                monthStr = "四";
                break;
            case 5:
                monthStr = "五";
                break;
            case 6:
                monthStr = "六";
                break;
            case 7:
                monthStr = "七";
                break;
            case 8:
                monthStr = "八";
                break;
            case 9:
                monthStr = "九";
                break;
            case 10:
                monthStr = "十";
                break;
            case 11:
                monthStr = "十一";
                break;
            case 12:
                monthStr = "十二";
                break;
        }
        return monthStr;
    }

    public static String getDisTimeFormat(long time) {
        StringBuilder sb = new StringBuilder();
        long between = time / 1000;
        long hour1 = between % (24 * 3600) / 3600;
        if (hour1 < 10) {
            sb.append(0);
        }
        sb.append(hour1);
        sb.append(":");
        long minute1 = between % 3600 / 60;
        if (minute1 < 10) {
            sb.append(0);
        }
        sb.append(minute1);
        sb.append(":");
        long second1 = between % 60;
        if (second1 < 10) {
            sb.append(0);
        }
        sb.append(second1);
        return sb.toString();

    }

    /**
     * 若是今天 则返回今天 HH:mm  昨天则是 昨天HH:mm 昨天以前都是 MM月dd日
     *
     * @param time
     * @return
     */
    public static String getTimeString(String time) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 24);  //若今天是7月10日，则这个设置为 7月11日 00:00:00  若set(Calendar.HOUR_OF_DAY, 0) 则为 7月10日 00:00:00
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        long todayStartTime = todayStart.getTime().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat todayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat beforeOneDayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat beforeMoreDayFormat = new SimpleDateFormat("MM月dd日");
        try {
//            Date date = sdf.parse(time);
            long times = Long.valueOf(time);
            long between = (todayStartTime - times) / 1000;
            long day = between / (24 * 3600);
            if (day > 1) {
                return beforeMoreDayFormat.format(new Date(times));
            } else if (day == 1) {
                String timeResult = "昨天" + beforeOneDayFormat.format(new Date(times));
                return timeResult;
            } else {
                String timeResult = "今天" + todayFormat.format(new Date(times));
                return timeResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * D
     * 通过long值得到Date值
     *
     * @param time
     * @return
     */
    public static Date getDateFromLong(long time) {
        Date date = new Date(time);
        return date;
    }


    /**
     * 通过long值得到时间格式为
     *
     * @param time
     * @return
     */
    public static String getTimeFromLong(long time) {
        return formatTimeFromLong(time, "yyyy-MM-dd HH:mm:ss");
    }


    public static String formatTimeFromLong(long time, String format){
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        return formatter.format(date);
    }

    /**
     * 根据时区按格式显示时间
     * @param time 时间
     * @param format 格式
     * @param timeZone 时区
     * @return 返回
     */
//    public static String formatTimeWithTimeZone(final long time, final String format, final String timeZone){
//        DateTime dt = new DateTime(time)
//                .withZone(DateTimeZone.forID(timeZone));
//        // Format for input
//        DateTimeFormatter dtf = DateTimeFormat.forPattern(format);
//        return dt.toString(dtf);
//    }

//    public static String formatTimeWithGMTTimeZone(final long time, final String format) {
//        return formatTimeWithTimeZone(time, format, "GMT");
//    }

}
