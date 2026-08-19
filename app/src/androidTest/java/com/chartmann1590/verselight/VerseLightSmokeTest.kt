package com.chartmann1590.verselight

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VerseLightSmokeTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun guestCanNavigateCoreExperience() {
        compose.onNodeWithText("Today’s light").assertExists()
        compose.onNodeWithText("Community").performClick()
        compose.onNodeWithText("Community reflections").assertExists()
        compose.onNodeWithText("My Journey").performClick()
        compose.onNodeWithText("Sign in").assertExists()
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithText("Profile & peace").assertExists()
    }
}
