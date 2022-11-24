package cloud.makeronbean.course.model.student;

import cloud.makeronbean.course.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author makeronbean
 * @createTime 2022-11-24  20:17
 * @description TODO
 */
@Data
@TableName("class_info")
public class ClassInfo extends BaseEntity {
    private String name;
    private String xkCode;
}
