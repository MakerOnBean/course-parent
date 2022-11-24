package cloud.makeronbean.course.student.controller;

import cloud.makeronbean.course.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:32
 * @description TODO
 */
@RestController
@RequestMapping("/api/student")
public class ApiStudentController {

    @Autowired
    private StudentService studentService;


    /**
     * 通过studentId获取classId
     * /api/student/getClassId/{studentId}
     */
    @GetMapping("/getClassId/{studentId}")
    public Long getClassId(@PathVariable Long studentId) {
        return studentService.getClassIdByStudentId(studentId);
    }
}
