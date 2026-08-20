package com.chartmann1590.verselight

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chartmann1590.verselight.moderation.OnDeviceSafetyClassifier
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
}
