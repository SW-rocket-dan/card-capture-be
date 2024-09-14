package app.cardcapture.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeUtils {
    private TimeUtils() {
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toEpochMilli(Date date) {
        return date.toInstant().toEpochMilli();
    }

    public static long toEpochSecond(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    public static long toEpochSecond(Date date) {
        return date.toInstant().getEpochSecond();
    }

    public static boolean isCurrentTimeOver(LocalDateTime localDateTime) {
        return LocalDateTime.now().isAfter(localDateTime);
    }

    public static boolean isCurrentTimeOverInMilli(Date date) {
        return System.currentTimeMillis() > toEpochMilli(date);
    }

    public static boolean isCurrentTimeOverInMilli(LocalDateTime localDateTime) {
        return System.currentTimeMillis() > toEpochMilli(localDateTime);
    }

    public static boolean isCurrentTimeOverInSeconds(Date date) {
        return System.currentTimeMillis() / 1000 > toEpochSecond(date);
    }

    public static boolean isCurrentTimeOverInSeconds(LocalDateTime localDateTime) {
        return System.currentTimeMillis() / 1000 > toEpochSecond(localDateTime);
    }

    public static ZonedDateTime toSeoulZonedDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
    }
}
