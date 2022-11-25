package cloud.makeronbean.course.course.controller;

import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.common.result.ResultCodeEnum;
import cloud.makeronbean.course.common.util.AuthContextHolder;
import cloud.makeronbean.course.course.service.CourseService;
import cloud.makeronbean.course.model.course.CourseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:43
 * @description TODO
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;


    /**
     * 查询学生对应课程信息
     * /course/list
     */
    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        String studentId = AuthContextHolder.getStudentId(request);
        if (StringUtils.isEmpty(studentId)) {
            return Result.build(null,ResultCodeEnum.LOGIN_AUTH);
        }
        List<CourseInfo> courseInfoList = courseService.list(Long.valueOf(studentId));
        if (courseInfoList == null) {
            return Result.build(null,ResultCodeEnum.BUSY);
        } else {
            return Result.ok(courseInfoList);
        }
    }
}
