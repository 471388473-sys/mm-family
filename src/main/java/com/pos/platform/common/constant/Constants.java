package com.pos.platform.common.constant;

import java.util.regex.Pattern;

/**
 * 通用常量
 */
public class Constants {

    /** 手机号正则 */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /** 用户状态 */
    public static class UserStatus {
        public static final int ENABLED = 1;
        public static final int DISABLED = 0;
    }

    /** 管理员角色 */
    public static class AdminRole {
        public static final String ADMIN = "admin";
        public static final String SERVICE = "service";
    }

    /** POS申请状态 */
    public static class PosApplicationStatus {
        public static final int SUBMITTED = 1;
        public static final int PROCESSED = 2;
    }

    /** 机具状态 */
    public static class MachineStatus {
        public static final int UNBOUND = 0;
        public static final int BOUND = 1;
    }

    /** 提现状态 */
    public static class WithdrawalStatus {
        public static final int PENDING = 0;
        public static final int APPROVED = 1;
        public static final int REJECTED = 2;
        public static final int PAID = 3;
    }

    /** 积分变动类型 */
    public static class PointsChangeType {
        public static final String IMPORT = "import";
        public static final String EXCHANGE = "exchange";
        public static final String LOTTERY = "lottery";
        public static final String ADJUST = "adjust";
    }

    /** 抽奖状态 */
    public static class LotteryStatus {
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }

    /** 公告类型 */
    public static class AnnouncementType {
        public static final String IMPORTANT = "important";
        public static final String NOTICE = "notice";
        public static final String ACTIVITY = "activity";
    }

    /** 公告状态 */
    public static class AnnouncementStatus {
        public static final int DRAFT = 0;
        public static final int PUBLISHED = 1;
    }

    /** 系统配置键 */
    public static class ConfigKey {
        public static final String EXCHANGE_RATE = "exchange_rate";
        public static final String MIN_WITHDRAW = "min_withdraw";
        public static final String FIXED_FEE = "fixed_fee";
        public static final String FEE_RATE = "fee_rate";
        public static final String CS_WECHAT_ID = "cs_wechat_id";
        public static final String CS_PHONE = "cs_phone";
        public static final String CS_WORK_TIME = "cs_work_time";
    }

    /** 短信场景 */
    public static class SmsScene {
        public static final String POS_APPLY = "pos_apply";
        public static final String LOGIN = "login";
        public static final String OTHER = "other";
    }

    /** 短信状态 */
    public static class SmsStatus {
        public static final int UNUSED = 0;
        public static final int USED = 1;
        public static final int EXPIRED = 2;
    }

    /** 导入类型 */
    public static class ImportType {
        public static final String MACHINE = "machine";
        public static final String TRANSACTION = "transaction";
        public static final String POINTS = "points";
    }

    /** 导入状态 */
    public static class ImportStatus {
        public static final int PROCESSING = 0;
        public static final int COMPLETED = 1;
        public static final int FAILED = 2;
    }
}