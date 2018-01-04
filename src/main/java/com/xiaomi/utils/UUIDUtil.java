package com.xiaomi.utils;

import java.util.UUID;

public class UUIDUtil {
    public UUIDUtil() {
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    public static void main(String[] args) {
        String code = "000100030001";
        System.out.println(code.startsWith("00010003"));
        System.out.println(code.startsWith("000100030001"));
    }
}
