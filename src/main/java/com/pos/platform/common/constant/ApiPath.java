package com.pos.platform.common.constant;

/**
 * API路径常量
 */
public class ApiPath {

    /** 小程序端基础路径 */
    public static final String MINI_BASE = "/api/v1/mini";
    
    /** 管理端基础路径 */
    public static final String ADMIN_BASE = "/api/v1/admin";

    /** 小程序端模块路径 */
    public static class Mini {
        public static final String AUTH = MINI_BASE + "/auth";
        public static final String POS = MINI_BASE + "/pos";
        public static final String POINTS = MINI_BASE + "/points";
        public static final String WITHDRAWAL = MINI_BASE + "/withdrawal";
        public static final String LOTTERY = MINI_BASE + "/lottery";
        public static final String CS = MINI_BASE + "/cs";
        public static final String ANNOUNCEMENT = MINI_BASE + "/announcements";
        public static final String ACTIVATION = MINI_BASE + "/activation";
        public static final String HOME = MINI_BASE + "/home";
        public static final String SMS = MINI_BASE + "/sms";
    }

    /** 管理端模块路径 */
    public static class Admin {
        public static final String AUTH = ADMIN_BASE + "/auth";
        public static final String DASHBOARD = ADMIN_BASE + "/dashboard";
        public static final String CUSTOMER = ADMIN_BASE + "/customers";
        public static final String MACHINE = ADMIN_BASE + "/machines";
        public static final String POINTS = ADMIN_BASE + "/points";
        public static final String WITHDRAWAL = ADMIN_BASE + "/withdrawals";
        public static final String ANNOUNCEMENT = ADMIN_BASE + "/announcements";
        public static final String LOTTERY = ADMIN_BASE + "/lottery";
        public static final String ACTIVATION = ADMIN_BASE + "/activation";
        public static final String FAQ = ADMIN_BASE + "/faq";
        public static final String CS = ADMIN_BASE + "/cs";
        public static final String FILE = ADMIN_BASE + "/files";
    }
}