package com.hmall.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.hmall.common.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum UserStatus {
    FROZEN(0, "禁止使用"), NORMAL(1, "已激活");

    @EnumValue
    private final int value;

    private final String desc;

    UserStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserStatus of(int value) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.value == value) {
                return userStatus;
            }
        }
        throw new BadRequestException("账户状态错误");
    }
}