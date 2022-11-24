package cloud.makeronbean.course.course.service;

import cloud.makeronbean.course.model.activity.XkRecode;
import cloud.makeronbean.course.model.course.CourseInfo;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;

import java.util.List;
import java.util.Map;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:47
 * @description TODO
 */
public interface CourseService {

    /**
     * 查询学生对应课程信息
     */
    List<CourseInfo> list(Long studentId);


    /**
     * 查询当日所有可选课信息
     */
    List<CourseSelectableKind> getAllSelectable();

    /**
     * 处理选课消息
     */
    void handleXk(XkRecode xkRecode);
}
