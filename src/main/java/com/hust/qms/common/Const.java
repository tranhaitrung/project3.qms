package com.hust.qms.common;

public class Const {
    public class Role {
        static final public String ADMIN = "ADMIN";
        static final public String EMPLOYEE = "EMPLOYEE";
        static final public String CUSTOMER = "CUSTOMER";
        static final public String MANAGER = "MANAGER";
    }

    public class Status {
        static final public String ACTIVE = "ACTIVE";
        static final public String INACTIVE = "INACTIVE";
        static final public String BLOCK = "BLOCK";
        static final public String EXPIRED = "EXPIRED";
        static final public String DELETED = "DELETED";
    }

    public class StatusVerifyCode {
        static final public String ACTIVE = "ACTIVE";
        static final public String EXPIRED = "EXPIRED";
    }

    public class TypeVeriy {
        static final public String SMS = "SMS";
        static final public String EMAIL = "EMAIL";
    }

    public class ServiceCode {
        static final public String TRANSFER = "TRANSFER"; //Chuyển tiền
        static final public String PARCEL = "PARCEL"; //Chuyển bưu phẩm
        static final public String FRAGILE = "FRAGILE"; //Chuyển hàng dễ vỡ
    }

    public class StatusUserService {
        static final public String RESERVE = "RESERVE"; //Đặt số khi chưa có quầy hoạt động
        static final public String ACTIVE = "ACTIVE"; // Đang xử lý tại quầy
        static final public String MISSED = "MISSED"; //Đã đăt nhỡ
        static final public String WAITING = "WAITING"; //Đang đợi gọi tại quầy
        static final public String DONE = "DONE"; //Hoàn thành
    }
}
