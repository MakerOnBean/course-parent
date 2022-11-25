package cloud.makeronbean.course.common.result;

import lombok.Getter;

/**
 * @author makeronbean
 * @createTime 2022-11-23  13:15
 * @description TODO
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FULL(201, "课程已被选满"),
    RUN(202, "正在排队中"),
    FAIL(203, "失败"),
    ILLEGAL_REQUEST(204, "非法请求"),
    PERMISSION(205, "没有权限"),
    LOGIN_AUTH(206, "未登陆"),
    REPEAT(207, "重复选课"),
    BUSY(208, "系统繁忙"),
    LIMIT(209,"学生id已被限制访问"),
    MISMATCH(210,"登录账号或密码不正确")
    ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
