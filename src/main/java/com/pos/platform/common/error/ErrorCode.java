package com.pos.platform.common.error;

/**
 * 错误码枚举
 * 格式：模块编号 + 业务编号
 * 0=成功，10000=通用错误，20000=用户模块，30000=积分模块，40000=提现模块，50000=机具模块，60000=抽奖模块，70000=公告模块，80000=管理端模块
 */
public enum ErrorCode {
    // 成功
    SUCCESS(0, "请求成功"),

    // ========== 通用错误 (10xxx) ==========
    PARAM_VALID_ERROR(10001, "参数校验失败"),
    NOT_LOGIN(10002, "未登录或登录已过期"),
    NO_PERMISSION(10003, "无权限访问"),
    FREQUENT_REQUEST(10004, "请求过于频繁"),
    SERVER_ERROR(10005, "服务器内部错误"),
    DATA_NOT_FOUND(10006, "数据不存在"),
    DATA_EXISTS(10007, "数据已存在"),
    NOT_FOUND(10008, "数据不存在"),
    PARAM_INVALID(10009, "参数无效"),
    BALANCE_INSUFFICIENT(10010, "余额不足"),

    // ========== 用户模块错误 (20xxx) ==========
    PHONE_FORMAT_ERROR(20001, "手机号格式错误"),
    VERIFY_CODE_ERROR(20002, "验证码错误或已过期"),
    VERIFY_CODE_FREQUENT(20003, "验证码发送太频繁"),
    USER_EXISTS(20004, "用户已存在"),
    USER_DISABLED(20005, "用户已禁用"),
    POS_APPLICATION_EXISTS(20006, "已提交过POS机领取申请"),

    // ========== 积分模块错误 (30xxx) ==========
    POINTS_INSUFFICIENT(30001, "积分不足"),
    EXCHANGE_AMOUNT_INVALID(30002, "兑换金额必须大于0"),
    EXCHANGE_RATE_NOT_CONFIG(30003, "兑换比例未配置"),
    POINTS_IMPORT_FAILED(30004, "积分导入失败"),
    POINTS_ADJUST_FAILED(30005, "积分调整失败"),

    // ========== 提现模块错误 (40xxx) ==========
    BALANCE_BELOW_MIN_WITHDRAW(40001, "余额不足提现门槛"),
    WITHDRAW_AMOUNT_EXCEED(40002, "提现金额超出可提现余额"),
    WITHDRAW_RULE_NOT_CONFIG(40003, "提现规则未配置"),
    WITHDRAW_PENDING_EXISTS(40004, "有待审核的提现申请"),
    WITHDRAW_REJECTED(40005, "提现审核被拒绝"),

    // ========== 机具模块错误 (50xxx) ==========
    MACHINE_NOT_EXISTS(50001, "机具编码不存在"),
    MACHINE_ALREADY_BIND(50002, "机具已绑定其他用户"),
    MACHINE_NOT_BIND(50003, "机具未绑定"),
    MACHINE_IMPORT_FORMAT_ERROR(50004, "机具导入数据格式错误"),
    MACHINE_TRANSACTION_IMPORT_FAILED(50005, "机具交易量导入失败"),

    // ========== 抽奖模块错误 (60xxx) ==========
    LOTTERY_NOT_ENABLED(60001, "抽奖活动未开启"),
    LOTTERY_CHANCES_INSUFFICIENT(60002, "抽奖次数不足"),
    LOTTERY_DAILY_LIMIT(60003, "已达每日抽奖上限"),
    LOTTERY_PRIZE_EMPTY(60004, "奖品已发完"),

    // ========== 公告模块错误 (70xxx) ==========
    ANNOUNCEMENT_NOT_FOUND(70001, "公告不存在"),
    ANNOUNCEMENT_OFFLINE(70002, "公告已下架"),

    // ========== 管理端错误 (80xxx) ==========
    ADMIN_LOGIN_FAILED(80001, "管理员账号或密码错误"),
    ADMIN_DISABLED(80002, "管理员账号已禁用"),
    ADMIN_NO_PERMISSION(80003, "操作权限不足"),
    IMPORT_FILE_FORMAT_ERROR(80004, "导入文件格式错误"),
    IMPORT_DATA_VALID_ERROR(80005, "导入数据校验失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}