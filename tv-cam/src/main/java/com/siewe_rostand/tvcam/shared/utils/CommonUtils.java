package com.siewe_rostand.tvcam.shared.utils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author rostand
 * @project tvcam
 */
public class CommonUtils {
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_TIME_FORMAT_REGEX = "^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}$";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String REGEX_STRING_ONLY = "[a-zA-Z]*";
    public static final String REGEX_DIGIT_ONLY = "[0-9]*";
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
}
