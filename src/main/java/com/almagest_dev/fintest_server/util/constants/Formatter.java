package com.almagest_dev.fintest_server.util.constants;

import java.time.format.DateTimeFormatter;

// 날짜/시간 포맷터 상수 정의 클래스
public class Formatter {
    // API 응답용 날짜/시간 포맷터 (예: 20240101123015999)
    public static DateTimeFormatter API_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS");
    // 날짜 포맷터 (예: 20240101)
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    // 시간 포맷터 (예: 123015)
    public static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hhmmss");
}
