package com.wsn.cow.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {
    
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    
    /**
     * 格式化日期时间
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
        return sdf.format(date);
    }
    
    /**
     * 格式化日期
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }
    
    /**
     * 解析日期时间字符串
     */
    public static Date parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * 解析日期字符串
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * 获取今天开始时间（00:00:00）
     */
    public static Date getTodayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 获取今天结束时间（23:59:59）
     */
    public static Date getTodayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    /**
     * 获取指定天数前的日期
     */
    public static Date getDaysBefore(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }
    
    /**
     * 获取指定小时前的日期
     */
    public static Date getHoursBefore(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -hours);
        return calendar.getTime();
    }
    
    /**
     * 判断两个日期是否是同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * 计算两个日期之间的天数差
     */
    public static int daysBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        long diff = end.getTime() - start.getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }
}
