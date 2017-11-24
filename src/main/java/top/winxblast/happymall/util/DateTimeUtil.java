package top.winxblast.happymall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间转换工具类
 * 利用joda-time帮助我们实现功能
 *
 * @author winxblast
 * @create 2017/11/08
 **/
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串格式到Date格式
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr, String formatStr) {
        DateTimeFormatter datetimeformatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = datetimeformatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Date格式到字符串格式
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date, String formatStr) {
        if(date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /**
     * 字符串格式到Date格式
     * 重载一个带默认格式的方法
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter datetimeformatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = datetimeformatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Date格式到字符串格式
     * 重载一个带默认格式的方法
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        if(date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    /**
     * 测试一下这个类的功能
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateTimeUtil.strToDate("2016-12-21 11:11:11","yyyy-MM-dd HH:mm:ss"));
    }

}
