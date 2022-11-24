package cloud.makeronbean.course.common.exception;

import cloud.makeronbean.course.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author makeronbean
 * @createTime 2022-11-23  13:22
 * @description TODO
 */
public class CourseException extends RuntimeException{
    @ApiModelProperty(value = "异常状态码")
    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public CourseException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public CourseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "CourseException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
