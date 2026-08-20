package com.chartmann1590.verselight

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chartmann1590.verselight.moderation.OnDeviceSafetyClassifier
import com.chartmann1590.verselight.translation.OnDeviceTranslationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VerseLightSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun userCanNavigateCoreExperience() {
        if (compose.onAllNodesWithText("Welcome to VerseLight").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithText("Choose my language").performClick()
            compose.onNodeWithText("Continue in English").performScrollTo().performClick()
            compose.onNodeWithText("Continue as guest").performScrollTo().performClick()
            compose.waitUntil(8_000) { compose.onAllNodesWithText("Community").fetchSemanticsNodes().isNotEmpty() }
        }
        compose.onNodeWithText("Today’s light").assertExists()
        compose.onNodeWithText("Community").performClick()
        compose.onNodeWithText("Community reflections").assertExists()
        compose.onNodeWithText("My Journey").performClick()
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithText("Profile & peace").assertExists()
    }

    @Test fun onDeviceModerationIsOperational() = runBlocking {
        val classifier = OnDeviceSafetyClassifier(InstrumentationRegistry.getInstrumentation().targetContext)
        val status = classifier.engineStatus()
        println("VERSELIGHT_ON_DEVICE_AI=$status")
        assertTrue(status in setOf("gemini-nano-available", "gemini-nano-downloadable", "gemini-nano-downloading", "embedded-only"))
        assertFalse(classifier.classify("you are a f.u.c.k.i.n.g loser").allowed)
        assertFalse(classifier.classify("I will kill you").allowed)
    }

    @Test fun spanishTranslationModelWorksOnDevice() = runBlocking {
        val translation = OnDeviceTranslationRepository()
        val prepared = translation.prepareLanguage("es")
        assertTrue("Spanish ML Kit model failed: ${prepared.exceptionOrNull()?.message}", prepared.isSuccess)
        val translated = translation.translateUi("Today's light", "es")
        println("VERSELIGHT_ON_DEVICE_TRANSLATION=$translated")
        assertTrue(translated.isNotBlank() && translated != "Today's light")
    }
}
