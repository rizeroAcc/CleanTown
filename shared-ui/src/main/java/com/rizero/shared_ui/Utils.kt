package com.rizero.shared_ui

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatDdMmYyHm(): String {
    return format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"))
}