package com.example.franchise.domain.constants;

public class Constants {
    public static final int NAME_MAX_LENGTH = 80;
    public static final int STOCK_MIN = 0;

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
