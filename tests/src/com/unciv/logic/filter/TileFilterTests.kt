package com.unciv.logic.filter

import com.unciv.logic.city.City
import com.unciv.logic.civilization.Civilization
import com.unciv.models.ruleset.Ruleset
import com.unciv.models.ruleset.unique.Unique
import com.unciv.models.ruleset.unique.UniqueTriggerActivation
import com.unciv.testing.BaseTestRunner
import com.unciv.testing.TestGame
import com.unciv.testing.runTestParcours
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(BaseTestRunner::class)
class TileFilterTests {
    private lateinit var game: TestGame
    private lateinit var civ: Civilization
    private lateinit var city: City
    private fun setupModdedGame(): Ruleset {
        game = TestGame()
        game.makeHexagonalMap(3)
        civ = game.addCiv()
        city = game.addCity(civ, game.tileMap[2,0])
        return game.ruleset
    }

    @Test
    fun testTileFilters() {
        setupModdedGame()
        UniqueTriggerActivation.triggerUnique(Unique("Turn this tile into a [Coast] tile"), civ, tile = game.tileMap[-3,0])
        UniqueTriggerActivation.triggerUnique(Unique("Turn this tile into a [Coast] tile"), civ, tile = game.tileMap[3,0])

        game.addCity(civ, game.tileMap[-2,0], initialPopulation = 9)

        runTestParcours("Tile matches filter", { test: String ->
            game.tileMap.tileList.count { it.matchesFilter(test, civ) }
        },
            "All", 37,
            "worked", 8,
            "Coastal", 6,
            "Coast", 2,
            "Land", 35,
            "Farm", 0,
        )
    }

    /** Catches #15959: civFilters on the tile owner must be relative to the observing civ */
    @Test
    fun ownerCivFilterIsRelativeToObservingCiv() {
        setupModdedGame()
        val otherCiv = game.addCiv()
        val ownedTile = city.getCenterTile()

        Assert.assertFalse(ownedTile.matchesFilter("Known", otherCiv))
        civ.diplomacyFunctions.makeCivilizationsMeet(otherCiv)
        Assert.assertTrue(ownedTile.matchesFilter("Known", otherCiv))
        Assert.assertFalse(ownedTile.matchesFilter("Hostile", otherCiv))

        otherCiv.getDiplomacyManager(civ)!!.declareWar()
        Assert.assertTrue(ownedTile.matchesFilter("Hostile", otherCiv))
        Assert.assertTrue(ownedTile.matchesFilter("Foreign", otherCiv))
        Assert.assertFalse(ownedTile.matchesFilter("Hostile", civ))
    }
}
