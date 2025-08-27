package com.example.studdybuddy.utils

fun calculateDurationInMinutes(startTime: String, endTime: String): Int {
    val startParts = startTime.split("[: ]")
    val startHour = startParts[0].toInt()
    val startMinute = startParts[1].toInt()
    val startAmPm = startParts[2]

    val endParts = endTime.split("[: ]")
    val endHour = endParts[0].toInt()
    val endMinute = endParts[1].toInt()
    val endAmPm = endParts[2]

    val startHour24 = if (startAmPm == "PM" && startHour != 12) startHour + 12 else if (startAmPm == "AM" && startHour == 12) 0 else startHour
    val endHour24 = if (endAmPm == "PM" && endHour != 12) endHour + 12 else if (endAmPm == "AM" && endHour == 12) 0 else endHour

    val startTotalMinutes = startHour24 * 60 + startMinute
    val endTotalMinutes = endHour24 * 60 + endMinute

    return endTotalMinutes - startTotalMinutes
}
