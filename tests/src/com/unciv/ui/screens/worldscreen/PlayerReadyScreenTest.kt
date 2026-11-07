package com.unciv.ui.screens.worldscreen

import com.unciv.testing.GdxTestRunner
import com.unciv.testing.TestGame
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GdxTestRunner::class)
class PlayerReadyScreenTest {

    private fun hotseatGame(): TestGame {
        val testGame = TestGame()
        testGame.addCiv(isPlayer = true)
        testGame.addCiv(isPlayer = true)
        return testGame
    }

    @Test
    fun hotseatGameShowsReadyScreenWhenLoaded() {
        val gameInfo = hotseatGame().gameInfo
        assertTrue(PlayerReadyScreen.isNeeded(gameInfo, isReloadForSameViewer = false))
    }

    @Test
    fun hotseatGameSkipsReadyScreenWhenSameViewerReloads() {
        // Changing the language rebuilds the WorldScreen for the player already looking at it (#15956)
        val gameInfo = hotseatGame().gameInfo
        assertFalse(PlayerReadyScreen.isNeeded(gameInfo, isReloadForSameViewer = true))
    }

    @Test
    fun singleHumanGameNeverShowsReadyScreen() {
        val testGame = TestGame()
        testGame.addCiv(isPlayer = true)
        testGame.addCiv()
        assertFalse(PlayerReadyScreen.isNeeded(testGame.gameInfo, isReloadForSameViewer = false))
    }

    @Test
    fun onlineMultiplayerGameNeverShowsReadyScreen() {
        val gameInfo = hotseatGame().gameInfo
        gameInfo.gameParameters.isOnlineMultiplayer = true
        assertFalse(PlayerReadyScreen.isNeeded(gameInfo, isReloadForSameViewer = false))
    }
}
