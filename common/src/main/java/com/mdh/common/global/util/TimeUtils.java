package com.mdh.common.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class TimeUtils {
    public static LocalDateTime roundToNearestNanos(LocalDateTime dateTime, int scale) {
        var nanoSeconds = new BigDecimal(dateTime.getNano());
        var divisor = new BigDecimal(1_000_000_000).divide(BigDecimal.TEN.pow(scale));
        var roundedNanoSeconds = nanoSeconds.divide(divisor, 0, RoundingMode.HALF_UP).multiply(divisor);

        return dateTime.withNano(roundedNanoSeconds.intValue());
    }
}