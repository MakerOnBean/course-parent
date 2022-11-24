package cloud.makeronbean.course.model.student;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author makeronbean
 * @createTime 2022-11-23  14:19
 * @description TODO
 */
@Data
@TableName("student_login")
public class StudentLogin extends BaseEntity {
    private Long studentId;
    private String loginName;
    private String loginPass;
}
