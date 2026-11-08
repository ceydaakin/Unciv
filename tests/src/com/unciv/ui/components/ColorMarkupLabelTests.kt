package com.unciv.ui.components

import com.unciv.UncivGame
import com.unciv.models.metadata.GameSettings
import com.unciv.testing.BaseTestRunner
import com.unciv.ui.components.fonts.FontRulesetIcons
import com.unciv.ui.components.widgets.ColorMarkupLabel
import com.unciv.ui.images.ImageGetter
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(BaseTestRunner::class)
class ColorMarkupLabelTests {
    // A city named after a nation (e.g. a captured City State's capital) must not get the nation portrait in notification text (#15965)
    private val nationName = "Geneva"
    private val nationIcon = ''
    private val text = "[$nationName] has grown!"

    @Before
    fun setUp() {
        UncivGame.Current = UncivGame()
        UncivGame.Current.settings = GameSettings()
        FontRulesetIcons.rulesetObjectNameToChar[nationName] = nationIcon
    }

    @After
    fun tearDown() {
        FontRulesetIcons.rulesetObjectNameToChar.remove(nationName)
    }

    @Test
    fun prepareTextInsertsRulesetIconsByDefault() {
        val prepared = ColorMarkupLabel.prepareText(text, ImageGetter.CHARCOAL, ImageGetter.CHARCOAL)
        Assert.assertTrue(nationIcon in prepared)
    }

    @Test
    fun prepareTextWithHideIconsOmitsRulesetIcons() {
        val prepared = ColorMarkupLabel.prepareText(text, ImageGetter.CHARCOAL, ImageGetter.CHARCOAL, hideIcons = true)
        Assert.assertFalse(nationIcon in prepared)
        Assert.assertTrue(nationName in prepared)
    }
}
