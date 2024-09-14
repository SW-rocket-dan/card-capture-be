package app.cardcapture.common.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public class StringUtils {

    private StringUtils() {
    }

    public static String makeUniqueFileName() {
        return UUID.randomUUID() + LocalDateTime.now().toString();
    }
}
