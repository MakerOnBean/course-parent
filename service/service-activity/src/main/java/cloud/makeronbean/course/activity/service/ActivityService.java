package cloud.makeronbean.course.activity.service;

import cloud.makeronbean.course.common.result.Result;
import cloud.makeronbean.course.model.activity.XkRecode;
import cloud.makeronbean.course.model.course.CourseSelectable;
import cloud.makeronbean.course.model.course.CourseSelectableKind;

import java.util.List;

/**
 * @author makeronbean
 * @createTime 2022-11-23  22:16
 * @description TODO
 */
public interface ActivityService {

    /**
     * 导入选课数据到redis
     */
    void importToRedis(List<CourseSelectableKind> courseSelectableKindList);


    /**
     * 根据选课码查询选课类别
     */
    List<CourseSelectableKind> getKindList(String xkCode);


    /**
     * 获取具体可选课程列表
     */
    List<CourseSelectable> getDetailList(String xkCode, Long kindId, String studentId);


    /**
     * 选择具体的课程
     */
    Result select(Long courseSelectableId, Long studentId, String xkCode, Long kindId);


    /**
     * 数据回滚
     */
    void rollback(XkRecode xkRecode);

    /**
     * 查询选课是否成功
     */
    Result isSuccess(Long courseSelectableId, String xkCode, Long studentId);

    /**
     * 清除过期的选课数据
     */
    void removeCache();

}
