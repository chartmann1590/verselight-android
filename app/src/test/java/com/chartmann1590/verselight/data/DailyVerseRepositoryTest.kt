package com.chartmann1590.verselight.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class DailyVerseRepositoryTest {
    @Test fun `daily verse is deterministic for a UTC day`() {
        val clock = Clock.fixed(Instant.parse("2026-08-19T23:59:59Z"), ZoneOffset.UTC)
        val repository = DailyVerseRepository(clock)
        assertEquals(repository.today(), repository.today())
        assertEquals("2026-08-19", repository.today().dayKey)
        assertTrue(repository.today().text.isNotBlank())
    }

    @Test fun `adjacent days rotate verses`() {
        val repository = DailyVerseRepository()
        val one = repository.forDate(java.time.LocalDate.of(2026, 8, 19))
        val two = repository.forDate(java.time.LocalDate.of(2026, 8, 20))
        assertTrue(one.reference != two.reference)
    }
}

