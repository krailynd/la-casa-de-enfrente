package app.opposite

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import app.opposite.data.DemoSeed
import app.opposite.data.Settings
import app.opposite.ui.LocalRu
import app.opposite.ui.Onboarding
import app.opposite.ui.Overlay
import app.opposite.ui.Root
import app.opposite.ui.Sheet
import app.opposite.ui.Still
import app.opposite.ui.Tab
import app.opposite.ui.TreeBitmap
import app.opposite.ui.theme.OppositeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

/** Every main screen over the demo season, drawn to PNGs (run with `-Prender`), in both languages. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
abstract class ScreensBase {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    abstract val lang: String
    private val out by lazy { File(System.getProperty("render.out") ?: "build/art", "screens-$lang").apply { mkdirs() } }
    private val today = LocalDate.now()
    private val evening = today.atTime(20, 30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @Before fun still() { Still.on = true }

    private val app get() = ApplicationProvider.getApplicationContext<OppositeApp>()

    private fun settings(change: (Settings) -> Settings = { it }): Settings {
        app.store.update { change(Settings(onboarded = true, lat = 55.75, lon = 37.62, season = DemoSeed.season(today))) }
        return app.store.now
    }

    private fun shot(name: String, content: @Composable () -> Unit) {
        rule.mainClock.autoAdvance = false
        rule.setContent { OppositeTheme { CompositionLocalProvider(LocalRu provides (lang == "ru")) { content() } } }
        rule.mainClock.advanceTimeBy(1_500)
        rule.waitForIdle()
        rule.mainClock.advanceTimeBy(600)
        rule.runOnUiThread {
            val view = rule.activity.window.decorView
            val image = android.graphics.Bitmap.createBitmap(view.width, view.height, android.graphics.Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(image))
            File(out, "$name.png").outputStream().use { image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    private fun screen(name: String, tab: Tab = Tab.HOUSE, overlay: Overlay? = null, sheet: Sheet? = null, change: (Settings) -> Settings = { it }) {
        val s = settings(change)
        shot(name) { Root(s, evening, tab, overlay, sheet) }
    }

    @Test fun welcome() = shot("0-welcome") { Onboarding(Settings()) {} }
    @Test fun house() = screen("1-house")
    @Test fun task() = screen("2-task", sheet = Sheet.Task(10))
    @Test fun room() = screen("3-room", overlay = Overlay.Room(6))
    @Test fun roomPetr() = screen("3b-room-petr", overlay = Overlay.Room(22)) { it.copy(season = it.season!!.copy(lit = it.season.lit + (22 to 1L))) }
    @Test fun notebook() = screen("4-notebook", Tab.NOTES)
    @Test fun dossier() = screen("5-dossier", Tab.NOTES, sheet = Sheet.Dossier("vera"))
    @Test fun tree() { TreeBitmap.get(); screen("6-tree", Tab.TREE) }
    @Test fun gifts() = screen("7-gifts", overlay = Overlay.Gifts)
    @Test fun settingsScreen() = screen("10-settings", overlay = Overlay.Settings)
    @Test fun giftRoom() = screen("8-gift-room", overlay = Overlay.Room(3))
    @Test fun finale() = screen("9-finale", overlay = Overlay.Finale) { it.copy(season = it.season!!.copy(solved = 1L, lit = (1..24).associateWith { 1L })) }
}

class RenderScreensRu : ScreensBase() { override val lang = "ru" }
class RenderScreensEn : ScreensBase() { override val lang = "en" }
