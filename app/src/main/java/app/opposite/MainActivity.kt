package app.opposite

import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.opposite.ui.LocalRu
import app.opposite.ui.Root
import app.opposite.ui.SoundManager
import app.opposite.ui.theme.OppositeTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val t = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(t), navigationBarStyle = SystemBarStyle.dark(t))

        // Request notification permission for Android 13+ (Media player controls)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        val ru = resources.configuration.locales[0].language == "ru"
        setContent {
            val settings by store.state.collectAsStateWithLifecycle()
            OppositeTheme {
                CompositionLocalProvider(LocalRu provides ru) {
                    Root(settings)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.stop()
    }
}
