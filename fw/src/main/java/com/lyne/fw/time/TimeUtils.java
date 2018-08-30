package com.lyne.fw.time;

/**
 * Created by liht  on 2016/9/26 15:09.
 * Desc:
 */
import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static long ONE_DAY = 1000L * 3600 * 24;

    public static boolean isSameDay(long time1, long time2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String day1 = sdf.format(new Date(time1));
        String day2 = sdf.format(new Date(time2));
        return day1.equals(day2);
    }

    public static boolean isToday(long time){
        return isSameDay(time, System.currentTimeMillis());
    }

    public static boolean isTomorrow(long time){
        return isToday(time - ONE_DAY);
    }

    public static String formatTimeNormal(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal2(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal3(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.M.d", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal4(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal5(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal6(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal7(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal8(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal9(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNorma20(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal10(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日 H:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal11(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal12(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal13(long when){
        SimpleDateFormat sdf;
        if (isToday(when)){
            sdf = new SimpleDateFormat("今天 H:mm", Locale.CHINA);
        }else if(isToday(when - ONE_DAY)){
            sdf = new SimpleDateFormat("明天 H:mm", Locale.CHINA);
        /*}else if(isToday(when - ONE_DAY * 2)){
            sdf = new SimpleDateFormat("后天 H:mm");*/
        }else {
            sdf = new SimpleDateFormat("M月d日 H:mm", Locale.CHINA);
        }
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal14(long when){
        SimpleDateFormat sdf;
        if (isToday(when)){
            sdf = new SimpleDateFormat("今天 H:mm(E)", Locale.CHINA);
        }else if(isToday(when - ONE_DAY)){
            sdf = new SimpleDateFormat("明天 H:mm(E)", Locale.CHINA);
        /*}else if(isToday(when - ONE_DAY * 2)){
            sdf = new SimpleDateFormat("后天 H:mm");*/
        }else {
            sdf = new SimpleDateFormat("M月d日 H:mm(E)", Locale.CHINA);
        }
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal15(long when){
        SimpleDateFormat sdf;
        if (isToday(when)){
            sdf = new SimpleDateFormat("今天 H:mm", Locale.CHINA);
        }else if(isToday(when + ONE_DAY)){
            sdf = new SimpleDateFormat("昨天", Locale.CHINA);
        }else {
            sdf = new SimpleDateFormat("M月d日", Locale.CHINA);
        }
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal16(long when){
        SimpleDateFormat sdf;
        if (isToday(when)){
            sdf = new SimpleDateFormat("H:mm", Locale.CHINA);
        }else if(isToday(when + ONE_DAY)){
            sdf = new SimpleDateFormat("昨天 H:mm", Locale.CHINA);
        }else {
            sdf = new SimpleDateFormat("M月d日 H:mm", Locale.CHINA);
        }
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal17(long when){
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("M月d日(E) H:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal18(long when){
        SimpleDateFormat sdf;
        if (isToday(when)){
            return "今天";
        }else if(isTomorrow(when)){
            return "明天";
        }else {
            sdf = new SimpleDateFormat("E", Locale.CHINA);
        }
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal19(long when){
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("H:mm", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTimeNormal20(long when){
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static int getTimeDistance(long start, long end){
        Date startDate = new Date(start);
        Date endDate = new Date(end);
        return startDate.getDay() - endDate.getDay();
    }

    public static int getDateDistance(long start, long end){
        int distance = (int) ((end - start) / (1000 * 60 * 60 * 24L));
        return distance;
    }

    public static String formatCoverTime(long startTime, long stopTime){
        int coverMinutes;
        long coverTime = stopTime - startTime;
        coverMinutes = (int)(coverTime/(60*1000));
        coverMinutes = coverMinutes < 1 ? 1 : coverMinutes;

        if (coverMinutes < 60){
            return coverMinutes +"分钟前";
        }else if (coverMinutes < 60 * 24){
            return coverMinutes / 60 + "小时前";
        }else if (coverMinutes < 60 * 24 * 30){
            return coverMinutes / (60 * 24) + "天前";
        }else {
            return "越过30天";
        }
    }


    public static String formatCoverTime2(long startTime, long stopTime){
        int coverMinutes;
        long coverTime = stopTime - startTime;
        coverMinutes = (int)(coverTime/(60*1000));
        coverMinutes = coverMinutes < 1 ? 1 : coverMinutes;
        if (coverMinutes < 60){
            return coverMinutes +"分钟前";
        }else if (coverMinutes < 60 * 24){
            return coverMinutes / 60 + "小时前";
        }else if (coverMinutes < 60 * 24 * 30){
            return coverMinutes / (60 * 24) + "天前";
        }else if (coverMinutes < 60 * 24 * 365) {
            return coverMinutes / (60 * 24 * 30) + "月前";
        }else {
            return coverMinutes / (60 * 24 * 365) + "年前";
        }
    }

    public static String formatDuration(long duration){
        DecimalFormat format = new DecimalFormat("00");
        int coverSeconds = 0;
        coverSeconds = (int)(duration/1000);
        if(coverSeconds <= 1){
            return "00:01" ;
        }else if(coverSeconds < 60){
            return "00:" + format.format(coverSeconds);
        }else if(coverSeconds < 60*60){
            return format.format(coverSeconds/60) + ":" +format.format(coverSeconds%60);
        }else{
            int coverMinutes = coverSeconds/60;
            return format.format(coverMinutes/60) + ":" + format.format(coverMinutes%60) + ":" + format.format(coverSeconds%60);
        }
    }

    public static String formatDuration2(long deadLine){
        String time = "";
        long minute = (deadLine - System.currentTimeMillis()) / (1000L * 60);
        long hour = minute / 60L;
        long day = hour / 24L;
        if (day >= 30){
            time = "超过30天";
        }else if (day > 0){
            time = "还剩" + day + "天";
        }else if (hour > 0){
            time = "还剩" + hour + "小时";
        }else{
            minute = minute < 1 ? 1 : minute;
            time = "还剩" + minute + "分钟";
        }
        return time;
    }

    public static String formatDurationMinute(long duration){
        int coverDays = (int) (duration / (1000L * 3600 * 24));
        int coverHours = (int) ((duration / (1000L * 3600)) % 24);
        int coverMinutes = (int) ((duration / (1000L * 60)) % 60);

        if (coverDays == 0 && coverHours == 0 && coverMinutes == 0){
            return "1分钟";
        }else {
            StringBuilder sb = new StringBuilder();
            if (coverDays > 0){
                sb.append(coverDays);
                sb.append("天");
            }

            if (coverDays > 0 || coverHours > 0){
                sb.append(coverHours);
                sb.append("小时");
            }

            sb.append(coverMinutes);
            sb.append("分钟");

            return sb.toString();

        }
    }

    public static String formatPhotoDate(String path){
        File file = new File(path);
        if(file.exists()){
            long time = file.lastModified();
            return formatTimeNormal2(time);
        }
        return "1970-01-01";
    }

    public static String getWeekOfDate(long time) {

        String[] weekDays = { "Sun.", "Mon.", "Tues.", "Wed.", "Thur.", "Fri.", "Sat." };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String formatTime(Context context, long when, boolean showTimeIfNotToday) {

        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        StringBuilder patternBuilder = new StringBuilder("");

        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            patternBuilder.append("yyyy年M月d日");
            if(showTimeIfNotToday){
                patternBuilder.append(" ").append("H:mm");
            }
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            patternBuilder.append("M月d日");
            if(showTimeIfNotToday){
                patternBuilder.append(" ").append("H:mm");
            }
        } else {
            // Otherwise, if the message is from today, show the time.
            patternBuilder.append("H:mm");
        }

        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        SimpleDateFormat sdf = new SimpleDateFormat(patternBuilder.toString(), Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static String formatTime2(Context context, long when, boolean showTimeIfNotToday) {

        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        StringBuilder patternBuilder = new StringBuilder("");

        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            patternBuilder.append("yyyy年M月d日");
            if(showTimeIfNotToday){
                patternBuilder.append(" ").append(systemFormatTime(context, when));
            }
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            if(then.yearDay+1 == now.yearDay){
                patternBuilder.append("昨天");
            }else{
                patternBuilder.append("M月d日");
            }
            if(showTimeIfNotToday){
                patternBuilder.append(" ").append(systemFormatTime(context, when));
            }
        } else {
            // Otherwise, if the message is from today, show the time.
            patternBuilder.append(systemFormatTime(context, when));
        }

        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        SimpleDateFormat sdf = new SimpleDateFormat(patternBuilder.toString(), Locale.CHINA);
        return sdf.format(new Date(when));
    }


    public static String formatTime3(Context context, long when, boolean showTimeIfNotToday) {

        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        StringBuilder patternBuilder = new StringBuilder("");

        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            patternBuilder.append("yyyy/M/d");
            if(showTimeIfNotToday){
                patternBuilder.append(" ").append(systemFormatTime(context, when));
            }
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            if(then.yearDay+1 == now.yearDay){
                patternBuilder.append("昨天");
            }else{
                patternBuilder.append("M/d");
            }
            if(showTimeIfNotToday){
                patternBuilder.append(" ").append(systemFormatTime(context, when));
            }
        } else {
            // Otherwise, if the message is from today, show the time.
            patternBuilder.append(systemFormatTime(context, when));
        }

        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        SimpleDateFormat sdf = new SimpleDateFormat(patternBuilder.toString(), Locale.CHINA);
        return sdf.format(new Date(when));
    }

    public static boolean isSameDay(Date day1, Date day2){
        return    day1.getYear()==day2.getYear()
                &&day1.getMonth()==day2.getMonth()
                &&day1.getDate()==day2.getDate();

    }
    public static boolean isSameDay(Calendar day1, Calendar day2){
        return    day1.get(Calendar.YEAR)==day2.get(Calendar.YEAR)
                &&day1.get(Calendar.MONTH)==day2.get(Calendar.MONTH)
                &&day1.get(Calendar.DAY_OF_MONTH)==day2.get(Calendar.DAY_OF_MONTH);

    }
    /**
     * 判断day1 是不是day2的昨天
     * @param day1
     * @return
     */
    public static boolean isYesterdayOf(Date day1, Date day2){
        Calendar d1= Calendar.getInstance();
        d1.setTime(day1);
        Calendar d2= Calendar.getInstance();
        d2.setTime(day2);
        d2.roll(Calendar.DAY_OF_MONTH,-1);
        return isSameDay(d1,d2);
    }


    public static String systemFormatTime(Context context, long when){
        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(context.getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        String time = "\'"+dateFormat.format(new Date(when))+"\'";
        if (calendar.get(Calendar.HOUR_OF_DAY) < 6 && calendar.get(Calendar.HOUR_OF_DAY) > 0){
            time = time.replace("上午", "凌晨");
        }
        return time;
    }

    /**
     * 获取今天往后一周的日期（几月几号）
     */
    public static List<String> getSevendate() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay="";
        boolean isNextMonth = false;
        int nextMonthDays = 1;
        for (int i = 0; i < 7; i++) {
            if(!isNextMonth){
                mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + i);// 获取当前日份的日期号码
            }else{
                mDay = String.valueOf(nextMonthDays);
            }
            if( Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear), c.get(Calendar.MONTH) + 1) ){
                isNextMonth = true;
                mMonth = String.valueOf(c.get(Calendar.MONTH) + 2);
                nextMonthDays++;
                mDay = "1";
            }
            String date = mYear + "-" + (Integer.parseInt(mMonth) < 10 ? "0" + mMonth: mMonth) + "-" + mDay;
//            String date = mMonth + "月" + mDay + "日";
            dates.add(date);
        }
        return dates;
    }

    /**得到当年当月的最大日期**/
    public static int MaxDayFromDay_OF_MONTH(int year,int month){
        Calendar time= Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR,year);
        time.set(Calendar.MONTH,month-1);//注意,Calendar对象默认一月为0
        int day=time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }

    /**
     * 根据当前日期获得是星期几
     *
     * @return
     */
    public static String getWeek(String time) {
        String Week = "";


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
            if( format.parse(time).equals(format.parse(format.format(new Date()))) ){
                return Week += "今天";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "周日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "周一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "周二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "周三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "周四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "周五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "周六";
        }
        return Week;
    }


}
