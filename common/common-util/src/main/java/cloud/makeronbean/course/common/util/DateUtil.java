package cloud.makeronbean.course.common.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期操作工具类
 * @author makeronbean
 */
public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 格式化日期
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

}
