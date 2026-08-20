package com.chartmann1590.verselight.translation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OnDeviceTranslationRepositoryTest {
    @Test fun `supported languages are unique and include major language families`() {
        val languages = OnDeviceTranslationRepository.supportedLanguages
        assertEquals(languages.size, languages.map { it.tag }.distinct().size)
        assertEquals("en", languages.first().tag)
        assertTrue(setOf("ar", "bn", "zh", "es", "fr", "hi", "ja", "ko", "ru", "ur").all { tag -> languages.any { it.tag == tag } })
    }
}
