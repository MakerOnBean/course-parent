package cloud.makeronbean.course.student.mapper;

import cloud.makeronbean.course.model.student.StudentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:47
 * @description TODO
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
    /**
     * 根据学生id查询xkCode
     */
    String selectXkCode(Long studentId);

    /**
     * 根据学生id查询所有学生信息
     */
    StudentInfo selectStudentById(Long studentId);
}
