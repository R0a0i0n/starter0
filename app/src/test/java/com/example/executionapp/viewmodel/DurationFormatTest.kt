package com.example.executionapp.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class DurationFormatTest {

    // Helper method matching the one in MainViewModel for testability
    private fun formatDuration(millis: Long): String {
        if (millis <= 0) return "总用时 0秒"
        
        val totalSeconds = millis / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = (totalSeconds / (60 * 60))
        
        val sb = StringBuilder()
        if (hours > 0) sb.append("${hours}时")
        if (minutes > 0) sb.append("${minutes}分")
        if (seconds > 0 || sb.isEmpty()) sb.append("${seconds}秒")
        
        return "总用时 ${sb.toString()}"
    }

    @Test
    fun `test format duration edge cases`() {
        // Edge cases
        assertEquals("总用时 0秒", formatDuration(-1000L))
        assertEquals("总用时 0秒", formatDuration(0L))
        assertEquals("总用时 0秒", formatDuration(999L)) // less than a second
    }

    @Test
    fun `test format duration single units`() {
        // Only seconds
        assertEquals("总用时 15秒", formatDuration(15_000L))
        
        // Only minutes
        assertEquals("总用时 1分", formatDuration(60_000L))
        assertEquals("总用时 5分", formatDuration(300_000L))
        
        // Only hours
        assertEquals("总用时 1时", formatDuration(3_600_000L))
        assertEquals("总用时 2时", formatDuration(7_200_000L))
    }

    @Test
    fun `test format duration mixed units`() {
        // Minutes and seconds
        assertEquals("总用时 1分30秒", formatDuration(90_000L))
        
        // Hours and minutes
        assertEquals("总用时 1时15分", formatDuration(4_500_000L))
        
        // Hours and seconds (skipping minutes)
        assertEquals("总用时 2时5秒", formatDuration(7_205_000L))
        
        // Hours, minutes and seconds
        assertEquals("总用时 2时3分4秒", formatDuration(7_384_000L))
    }
}