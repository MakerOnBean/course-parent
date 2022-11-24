package cloud.makeronbean.course.common.util;

//import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取登录用户信息类
 * @author makeronbean
 */
public class AuthContextHolder {

    /**
     * 获取当前登录用户id
     */
    public static String getStudentId(HttpServletRequest request) {
        String studentId = request.getHeader("studentId");
        return StringUtils.isEmpty(studentId) ? "" : studentId;
    }

    /**
     * 获取当前登录用户选课id
     */
    public static String getXkCode(HttpServletRequest request) {
        String xkCode = request.getHeader("xkCode");
        return StringUtils.isEmpty(xkCode) ? "" : xkCode;
    }
}
