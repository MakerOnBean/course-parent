package cloud.makeronbean.course.course.controller;

import cloud.makeronbean.course.course.service.CourseService;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author makeronbean
 * @createTime 2022-11-23  20:22
 * @description TODO
 */
@RestController
@RequestMapping("/api/course")
public class ApiCourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 查询当日所有可选课信息
     * /api/course/getAllSelectable
     */
    @GetMapping("/getAllSelectable")
    public List<CourseSelectableKind> getAllSelectable() {

        return courseService.getAllSelectable();
    }
}
