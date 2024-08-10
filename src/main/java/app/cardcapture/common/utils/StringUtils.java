package app.cardcapture.common.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public class StringUtils {

    private StringUtils() {
    }

    public static String makeUniqueFileName(String prefix) {
        return prefix + UUID.randomUUID() + LocalDateTime.now();
    }
}
