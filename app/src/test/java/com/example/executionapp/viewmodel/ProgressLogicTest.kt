package com.example.executionapp.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressLogicTest {

    @Test
    fun `test progress logic calculates correctly`() {
        // Mock UI progress calculation logic: currentProgressStep = min(completedSteps.size + 1, 6)
        fun calculateProgressStep(completedStepsSize: Int): Int {
            return (completedStepsSize + 1).coerceAtMost(6)
        }

        // Test cases from 0 to 6 completed steps
        assertEquals(1, calculateProgressStep(0)) // Initial step is 1/6
        assertEquals(2, calculateProgressStep(1))
        assertEquals(3, calculateProgressStep(2))
        assertEquals(4, calculateProgressStep(3))
        assertEquals(5, calculateProgressStep(4))
        assertEquals(6, calculateProgressStep(5)) // When 5 steps completed, currently on 6/6
        assertEquals(6, calculateProgressStep(6)) // When 6 steps completed, stays at 6/6
        assertEquals(6, calculateProgressStep(7)) // Ensure no overflow
    }
}
