package cloud.makeronbean.course.course.mapper;

import cloud.makeronbean.course.model.course.CourseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makeronbean
 * @createTime 2022-11-23  15:52
 * @description TODO
 */
@Mapper
public interface CourseInfoMapper extends BaseMapper<CourseInfo> {
    /**
     * 查询学生对应课程信息
     */
    List<CourseInfo> selectCourseInfoList(@Param("classId") Long classId, @Param("studentId") Long studentId);
}
