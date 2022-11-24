package cloud.makeronbean.course.student.service;

import cloud.makeronbean.course.model.student.StudentInfo;
import cloud.makeronbean.course.model.student.StudentLogin;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:27
 * @description TODO
 */
public interface StudentService {
    /**
     * 登录
     */
    Map<String, Object> login(StudentLogin login, HttpServletRequest request);

    /**
     * 查询个人信息
     */
    StudentInfo detail(Long studentId);


    /**
     * 通过studentId获取classId
     */
    Long getClassIdByStudentId(Long studentId);
}
