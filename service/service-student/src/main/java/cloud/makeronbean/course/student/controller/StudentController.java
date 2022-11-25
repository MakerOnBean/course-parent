package cloud.makeronbean.course.student.controller;

import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.result.ResultCodeEnum;
import cloud.makeronbean.course.common.util.AuthContextHolder;
import cloud.makeronbean.course.common.util.MD5;
import cloud.makeronbean.course.model.student.StudentInfo;
import cloud.makeronbean.course.model.student.StudentLogin;
import cloud.makeronbean.course.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:16
 * @description TODO
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;


    /**
     * 登录
     * POST /student/login
     */
    @PostMapping("/login")
    public Result login(@RequestBody StudentLogin login, HttpServletRequest request) {

        Map<String,Object> result = studentService.login(login,request);
        if (result == null) {
            return Result.build(null,ResultCodeEnum.MISMATCH);
        }
        return Result.ok(result);
    }

    /**
     * 查询个人信息
     * token:92605d389d9840c79edd487f8967293d
     * GET /student/detail
     */
    @GetMapping("/detail")
    public Result detail(HttpServletRequest request) {
        String studentId = AuthContextHolder.getStudentId(request);
        StudentInfo studentInfo = studentService.detail(Long.valueOf(studentId));
        return Result.ok(studentInfo);
    }

}
