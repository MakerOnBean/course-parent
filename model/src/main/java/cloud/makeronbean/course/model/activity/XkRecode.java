package cloud.makeronbean.course.model.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author makeronbean
 * @createTime 2022-11-24  14:04
 * @description TODO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XkRecode implements Serializable {
    private Long studentId;
    private Long courseSelectableId;
    private String xkCode;
}
