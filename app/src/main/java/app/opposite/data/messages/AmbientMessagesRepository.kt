package app.opposite.data.messages

import android.content.Context
import org.json.JSONObject
import java.util.Collections

object AmbientMessagesRepository {
    private val categories = mutableMapOf<String, List<String>>()
    private val activeBags = mutableMapOf<String, MutableList<String>>()
    private var isLoaded = false

    fun initialize(context: Context) {
        if (isLoaded) return
        try {
            val jsonStr = context.assets.open("francesca_bubbles.json").bufferedReader().use { it.readText() }
            val obj = JSONObject(jsonStr)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val arr = obj.getJSONArray(key)
                val list = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getString(i))
                }
                categories[key] = list
                activeBags[key] = list.toMutableList().also { Collections.shuffle(it) }
            }
            isLoaded = true
        } catch (_: Exception) {
            // Fallback en caso de error de lectura
            categories["romantic_general"] = listOf(
                "Eres mi lugar seguro y mi persona favorita en el mundo, Principessa ❤️",
                "Cada segundo desde el 6 de noviembre a las 6:18:35 PM lo guardo como un tesoro ✨"
            )
            activeBags["romantic_general"] = categories["romantic_general"]!!.toMutableList()
            isLoaded = true
        }
    }

    /**
     * Obtiene el siguiente mensaje sin repetir (ShuffleBag) para la categoría deseada.
     */
    fun getNextMessage(context: Context, categoryKey: String): String {
        initialize(context)

        val bag = activeBags[categoryKey]
        if (bag.isNullOrEmpty()) {
            val original = categories[categoryKey] ?: categories["romantic_general"] ?: listOf("Te amo, Principessa ❤️")
            val newBag = original.toMutableList().also { Collections.shuffle(it) }
            activeBags[categoryKey] = newBag
            return newBag.removeAt(0)
        }
        return bag.removeAt(0)
    }

    /**
     * Selecciona inteligentemente una categoría según la entidad tocada y la hora del día.
     */
    fun getMessageForEntity(context: Context, entityTag: String, isDayTime: Boolean, isSunset: Boolean): Pair<String, String> {
        val (speaker, category) = when (entityTag.lowercase()) {
            "sol" -> "Sol ☀️" to "sun_morning"
            "luna" -> "Luna 🌙" to "moon_night"
            "estrellas" -> "Estrella Fugaz 🌠" to "moon_night"
            "farola" -> "Farola Cálida 🏮" to (if (isSunset) "sunset_golden" else "moon_night")
            "cielo" -> "Cielo de Ica 🌇" to (if (isSunset) "sunset_golden" else if (isDayTime) "sun_morning" else "moon_night")
            "rata_aspirable", "rata" -> "Rata Aspirable 🐭🎸" to "rata_aspirable"
            "gato", "gatito" -> "Gatito de la Banca 🐱" to "animals"
            "perro", "perrito" -> "Perrito con Pañuelo 🐶" to "animals"
            "pajarito", "petirrojo" -> "Pajarito Mensajero 🐦" to "animals"
            "coche", "auto" -> "Auto Vintage Rojo 🚗" to "street_pedestrians"
            "pareja" -> "Pareja Paseando 👫" to "street_pedestrians"
            "peaton", "vecino" -> "Vecino con Café ☕" to "street_pedestrians"
            "muñeco", "snowman" -> "Muñeco de Nieve ⛄" to "christmas_props"
            "pino", "arbol" -> "Pino Navideño 🎄" to "christmas_props"
            "corona", "puerta" -> "Corona Navideña 🎀" to "christmas_props"
            "guirnalda", "luces" -> "Luces Festivas ✨" to "christmas_props"
            else -> "Nuestro Refugio 🌹" to "romantic_general"
        }

        val message = getNextMessage(context, category)
        return speaker to message
    }
}
