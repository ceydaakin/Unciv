package com.unciv.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.unciv.UncivGame
import com.unciv.dev.FontDesktop
import com.unciv.models.ruleset.Building
import com.unciv.models.tilesets.TileSetCache
import com.unciv.testing.GdxTestRunner
import com.unciv.testing.TestGame
import com.unciv.ui.components.fonts.Fonts
import com.unciv.ui.images.ImageGetter
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.ui.screens.civilopediascreen.CivilopediaCategories
import com.unciv.ui.screens.civilopediascreen.CivilopediaScreen
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.stubbing.Answer
import java.nio.IntBuffer

@RunWith(GdxTestRunner::class)
class CivilopediaScreenTests {
    private lateinit var game: TestGame
    private var screen: CivilopediaScreen? = null

    @Before
    fun setUp() {
        game = TestGame()
        Fonts.fontImplementation = FontDesktop()
        UncivGame.Current.settings.alternativeRendering = true
        if (!uiReady) {
            installShaderCapableGl()
            ImageGetter.setNewRuleset(game.ruleset)
            TileSetCache.loadTileSetConfigs()
            uiReady = true
        }
        BaseScreen.recreateBatch()
    }

    @After
    fun tearDown() {
        screen?.dispose()
    }

    /** #15893: a very long entry name widens every row; opening an entry must not scroll the list sideways */
    @Test
    fun selectingEntryKeepsEntryListScrolledToTheStart() {
        val longName = "Building with a very long name ".repeat(10).trim()
        game.ruleset.buildings[longName] = Building().apply { name = longName; ruleset = game.ruleset }

        val screen = CivilopediaScreen(game.ruleset, CivilopediaCategories.Building, "Granary")
        this.screen = screen

        val entryList = screen.stage.root.scrollPanes()
            .first { pane -> (pane.actor as? Group)?.children?.any { it.name == "Granary" } == true }
        Assert.assertTrue("Entry list should be wider than its viewport", entryList.maxX > 0f)
        Assert.assertEquals(0f, entryList.scrollX, 0.01f)
    }

    private fun Group.scrollPanes(): List<ScrollPane> =
        children.flatMap { child ->
            (if (child is ScrollPane) listOf(child) else emptyList()) +
                (if (child is Group) child.scrollPanes() else emptyList())
        }

    companion object {
        private var uiReady = false

        /** SpriteBatch compiles a shader while the screen's stage is created. The runner's GL mock reports failure. */
        private fun installShaderCapableGl() {
            val gl = Mockito.mock(GL20::class.java)
            Mockito.`when`(gl.glCreateShader(Mockito.anyInt())).thenReturn(1)
            Mockito.`when`(gl.glCreateProgram()).thenReturn(1)
            Mockito.`when`(gl.glGenBuffer()).thenReturn(1)
            Mockito.`when`(gl.glGenTexture()).thenReturn(1)
            Mockito.`when`(gl.glGetShaderInfoLog(Mockito.anyInt())).thenReturn("")
            Mockito.`when`(gl.glGetProgramInfoLog(Mockito.anyInt())).thenReturn("")
            val reportStatus = Answer<Any> { invocation ->
                val pname = invocation.getArgument<Int>(1)
                val params = invocation.getArgument<IntBuffer>(2)
                val value = when (pname) {
                    GL20.GL_COMPILE_STATUS, GL20.GL_LINK_STATUS -> GL20.GL_TRUE
                    else -> 0
                }
                params.put(0, value)
                null
            }
            Mockito.doAnswer(reportStatus).`when`(gl).glGetShaderiv(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())
            Mockito.doAnswer(reportStatus).`when`(gl).glGetProgramiv(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())
            Gdx.gl = gl
            Gdx.gl20 = gl
        }
    }
}
