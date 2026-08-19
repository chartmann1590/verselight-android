package com.chartmann1590.verselight.moderation

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnDeviceSafetyClassifierTest {
    private val classifier = OnDeviceSafetyClassifier(context = null)

    @Test fun `allows gracious disagreement`() = runBlocking {
        assertTrue(classifier.classify("I read this differently, but thank you for sharing.").allowed)
    }

    @Test fun `blocks a direct threat`() = runBlocking {
        assertFalse(classifier.classify("You should die and I will kill you").allowed)
    }

    @Test fun `normalizes common profanity obfuscation`() = runBlocking {
        assertFalse(classifier.classify("you are a f.u.c.k.i.n.g loser").allowed)
    }
}
