package app.opposite

import android.app.Application
import android.content.Context
import app.opposite.data.DemoSeed
import app.opposite.data.Settings
import app.opposite.data.Store
import app.opposite.domain.Sun
import app.opposite.work.Dusk
import java.time.LocalDate
import java.time.ZoneId

class OppositeApp : Application() {
    lateinit var store: Store
        private set

    override fun onCreate() {
        super.onCreate()
        store = Store(this)
        if (BuildConfig.DEMO) DemoSeed.seedIfEmpty(store)
        Dusk.schedule(this)
    }
}

val Context.store: Store get() = (applicationContext as OppositeApp).store

/** Sunset for a day, at the saved place or guessed from the time zone. */
fun Settings.sunset(zone: ZoneId = ZoneId.systemDefault()): (LocalDate) -> Long = { date ->
    val (la, lo) = if (lat != null && lon != null) lat to lon else Sun.guess(zone, date)
    Sun.sunset(date, la, lo, zone)
}
