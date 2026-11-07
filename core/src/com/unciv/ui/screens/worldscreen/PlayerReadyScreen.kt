package com.unciv.ui.screens.worldscreen

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.Constants
import com.unciv.logic.GameInfo
import com.unciv.logic.civilization.PlayerType
import com.unciv.ui.components.extensions.toLabel
import com.unciv.ui.components.input.KeyboardBinding
import com.unciv.ui.components.input.keyShortcuts
import com.unciv.ui.components.input.onActivation
import com.unciv.ui.screens.basescreen.BaseScreen

class PlayerReadyScreen(worldScreen: WorldScreen) : BaseScreen() {
    init {
        val table = Table()
        table.touchable = Touchable.enabled
        val curCiv = worldScreen.selectedGameView.civView
        table.background = skinStrings.getUiBackground(
            "PlayerReadyScreen/Background",
            tintColor = curCiv.getOuterColor()
        )

        table.add("[${curCiv.civName}] ready?".toLabel(curCiv.getInnerColor(), Constants.headingFontSize))

        table.onActivation {
            game.replaceCurrentScreen{ worldScreen }
        }
        // Doing this separately instead of passing the binding to onActivation avoids the tooltip
        table.keyShortcuts.add(KeyboardBinding.NextTurnAlternate)
        table.setFillParent(true)
        stage.addActor(table)
    }

    companion object {
        /** Hotseat games hide the map between turns so players don't see each other's empire.
         *  Not needed when the WorldScreen is only rebuilt for the player already viewing it,
         *  e.g. after changing the language or undoing a move (#15956). */
        fun isNeeded(gameInfo: GameInfo, isReloadForSameViewer: Boolean): Boolean {
            val moreThanOnePlayer = gameInfo.civilizations.count { it.playerType == PlayerType.Human } > 1
            val isSingleplayer = !gameInfo.gameParameters.isOnlineMultiplayer
            return moreThanOnePlayer && isSingleplayer && !isReloadForSameViewer
        }
    }
}
