package com.example.backgroundagriculture.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // 密码规则：至少6位，必须包含数字和英文字母
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$");

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static final String PASSWORD_RULE_MESSAGE = "密码必须至少6位，且包含数字和英文字母";
} 