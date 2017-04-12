package com.github.saschawiegleb.ek.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

final class Clock {

    static String dateNow() {
        LocalDate date = LocalDate.now();
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
