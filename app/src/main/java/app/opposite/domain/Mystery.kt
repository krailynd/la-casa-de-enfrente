package app.opposite.domain

import kotlin.random.Random

/**
 * El misterio del Admirador Secreto de Principessa.
 * Diez vecinos del edificio son sospechosos; el verdadero admirador es «El Músico de Medianoche» (Elvis).
 * Cada sospechoso tiene seis hábitos verificables (letra, envoltorio, pisadas, hora, paso, aroma).
 * Las 23 pistas descubiertas en las ventanas descartan progresivamente al resto hasta dejar únicamente a Elvis el día 24.
 */
object Mystery {
    enum class Attr(val ru: String, val en: String) {
        HAND("letra", "letra"),
        WRAP("envoltorio", "envoltorio"),
        STEPS("pisadas", "pisadas"),
        TIME("hora", "hora"),
        GAIT("paso", "paso"),
        SMELL("aroma", "aroma"),
    }

    class Suspect(
        val id: String,
        val day: Int,
        val name: Tx,
        val sub: Tx,
        val traits: Map<Attr, String>,
        val intro: Tx,
        val alibi: Tx?,
        val finale: Tx,
        val sign: Tx,
    )

    private fun t(ru: String, en: String) = Tx(ru, en)
    private fun a(hand: String, wrap: String, steps: String, time: String, gait: String, smell: String) =
        mapOf(Attr.HAND to hand, Attr.WRAP to wrap, Attr.STEPS to steps, Attr.TIME to time, Attr.GAIT to gait, Attr.SMELL to smell)

    val suspects = listOf(
        Suspect(
            "elvis", 24,
            t("El Músico de Medianoche", "El Músico de Medianoche"),
            t("productor y guitarrista (Elvis)", "productor y guitarrista (Elvis)"),
            a("slant", "kraft", "sneakers", "night", "hum", "coffee_wood"),
            t("Se queda despierto hasta la madrugada componiendo melodías acústicas y beats.", "Se queda despierto hasta la madrugada componiendo melodías acústicas y beats."),
            null,
            t(
                "¡Siempre fui yo, Mio Principessa! Cada nota de guitarra en Sol# mayor, cada carta sincera, cada broma sobre el Sol y la Rata, y cada detalle en este calendario de código los hice pensando únicamente en tu sonrisa. Te amo con todo mi corazón.",
                "¡Siempre fui yo, Mio Principessa! Cada nota de guitarra en Sol# mayor, cada carta sincera, cada broma sobre el Sol y la Rata, y cada detalle en este calendario de código los hice pensando únicamente en tu sonrisa. Te amo con todo mi corazón.",
            ),
            t("«Para siempre tuyo, Tu Sol (y tu rata favorita). Elvis»", "«Para siempre tuyo, Tu Sol (y tu rata favorita). Elvis»"),
        ),
        Suspect(
            "julian", 18,
            t("Don Julián", "Don Julián"),
            t("el sereno nocturno", "el sereno nocturno"),
            a("round", "string", "shoes", "early", "slow", "tea"),
            t("Vigila el patio con su linterna amarilla y siempre duerme a las diez de la noche.", "Vigila el patio con su linterna amarilla y siempre duerme a las diez de la noche."),
            t("«¿Don Julián? Imposible, a las diez en punto apaga su linterna y no sale hasta el amanecer».", "«¿Don Julián? Imposible, a las diez en punto apaga su linterna y no sale hasta el amanecer»."),
            t("Don Julián cuida el edificio desde hace décadas con cariño paternal.", "Don Julián cuida el edificio desde hace décadas con cariño paternal."),
            t("«Que tengan buenas noches. Don Julián»", "«Que tengan buenas noches. Don Julián»"),
        ),
        Suspect(
            "clotilde", 9,
            t("Doña Clotilde", "Doña Clotilde"),
            t("la repostera", "la repostera"),
            a("round", "fabric", "heels", "dawn", "slow", "cinnamon"),
            t("Hornea galletas con olor a canela y vainilla; madruga a las cinco de la mañana.", "Hornea galletas con olor a canela y vainilla; madruga a las cinco de la mañana."),
            t("«Doña Clotilde no sube las escaleras de noche; se acuesta muy temprano para amasar».", "«Doña Clotilde no sube las escaleras de noche; se acuesta muy temprano para amasar»."),
            t("Doña Clotilde endulza el vecindario con sus recetas tradicionales.", "Doña Clotilde endulza el vecindario con sus recetas tradicionales."),
            t("«Con cariño y canela. Clotilde»", "«Con cariño y canela. Clotilde»"),
        ),
        Suspect(
            "mateo", 12,
            t("Mateo", "Mateo"),
            t("el cartero ciclista", "el cartero ciclista"),
            a("quick", "newspaper", "sneakers", "evening", "quick", "mint"),
            t("Llega en su bicicleta roja con cartas y sellos antiguos; es alérgico al chocolate.", "Llega en su bicicleta roja con cartas y sellos antiguos; es alérgico al chocolate."),
            t("«Mateo no soporta el chocolate ni los dulces; se le brota la piel al instante».", "«Mateo no soporta el chocolate ni los dulces; se le brota la piel al instante»."),
            t("Mateo pedalea por todo Ica entregando buenas noticias.", "Mateo pedalea por todo Ica entregando buenas noticias."),
            t("«Entregado a tiempo. Mateo»", "«Entregado a tiempo. Mateo»"),
        ),
        Suspect(
            "valeria", 10,
            t("Valeria", "Valeria"),
            t("la pintora del ático", "la pintora del ático"),
            a("curly", "paper", "sneakers", "night", "quick", "paint"),
            t("Tiene las manos manchadas de acuarela y pinta atardeceres en el techo.", "Tiene las manos manchadas de acuarela y pinta atardeceres en el techo."),
            t("«Valeria usa tinta azul brillante y acuarelas moradas; no escribe con tinta oscura».", "«Valeria usa tinta azul brillante y acuarelas moradas; no escribe con tinta oscura»."),
            t("Valeria llena de color y arte cada rincón del ático.", "Valeria llena de color y arte cada rincón del ático."),
            t("«Pinceladas de luz. Valeria»", "«Pinceladas de luz. Valeria»"),
        ),
        Suspect(
            "armando", 11,
            t("El Profesor Armando", "El Profesor Armando"),
            t("astrónomo del edificio", "astrónomo del edificio"),
            a("print", "paper", "shoes", "night", "slow", "oldbooks"),
            t("Observa la luna con su telescopio y anota en libretas con olor a libros viejos.", "Observa la luna con su telescopio y anota en libretas con olor a libros viejos."),
            t("«El profesor Armando solo escribe en mayúsculas cuadradas y nunca dibuja».", "«El profesor Armando solo escribe en mayúsculas cuadradas y nunca dibuja»."),
            t("El profesor conoce cada constelación y estrella en el cielo.", "El profesor conoce cada constelación y estrella en el cielo."),
            t("«Mirando al cosmos. Prof. Armando»", "«Mirando al cosmos. Prof. Armando»"),
        ),
        Suspect(
            "beatriz", 16,
            t("Sra. Beatriz", "Sra. Beatriz"),
            t("la florista", "la florista"),
            a("curly", "ribbon", "heels", "early", "hum", "eucalyptus"),
            t("Ata ramitos de flores con cinta dorada y huele a rosas y eucalipto fresco.", "Ata ramitos de flores con cinta dorada y huele a rosas y eucalipto fresco."),
            t("«Beatriz cierra su florería a las seis de la tarde y no sale en toda la noche».", "«Beatriz cierra su florería a las seis de la tarde y no sale en toda la noche»."),
            t("Beatriz adora llenar de flores y aromas frescos los descansos del edificio.", "Beatriz adora llenar de flores y aromas frescos los descansos del edificio."),
            t("«Flores para el alma. Beatriz»", "«Flores para el alma. Beatriz»"),
        ),
        Suspect(
            "tomas", 15,
            t("Tomás", "Tomás"),
            t("el barista", "el barista"),
            a("quick", "napkin", "sneakers", "dawn", "quick", "coffee"),
            t("Prepara café con dibujos de espuma y escucha jazz suave en el bajo.", "Prepara café con dibujos de espuma y escucha jazz suave en el bajo."),
            t("«Tomás escribe notas rápidas en servilletas de papel con tinta que se corre».", "«Tomás escribe notas rápidas en servilletas de papel con tinta que se corre»."),
            t("Tomás despierta al barrio entero con su café de especialidad.", "Tomás despierta al barrio entero con su café de especialidad."),
            t("«Un café con cariño. Tomás»", "«Un café con cariño. Tomás»"),
        ),
        Suspect(
            "lucia", 14,
            t("Lucía", "Lucía"),
            t("la bibliotecaria", "la bibliotecaria"),
            a("print", "kraft", "shoes", "evening", "slow", "tea"),
            t("Marca libros con hojas secas; su letra es completamente recta y pulcra.", "Marca libros con hojas secas; su letra es completamente recta y pulcra."),
            t("«Lucía solo toma té de hierbas y no toma café ni chocolates».", "«Lucía solo toma té de hierbas y no toma café ni chocolates»."),
            t("Lucía guarda historias y libros para alegrar a los vecinos.", "Lucía guarda historias y libros para alegrar a los vecinos."),
            t("«Entre páginas. Lucía»", "«Entre páginas. Lucía»"),
        ),
        Suspect(
            "bigotes", 17,
            t("El Agente Bigotes", "El Agente Bigotes"),
            t("gato detective del tejado", "gato detective del tejado"),
            a("paw", "wool", "paws", "night", "skip", "fish"),
            t("Deja huellitas en el alféizar, roba galletitas y no sabe escribir (¡miau!).", "Deja huellitas en el alféizar, roba galletitas y no sabe escribir (¡miau!)."),
            t("«¡El Agente Bigotes es un gato! Por más listo que sea, no puede escribir cartas de amor».", "«¡El Agente Bigotes es un gato! Por más listo que sea, no puede escribir cartas de amor»."),
            t("Bigotes patrulla el tejado ronroneando a la luz de la luna.", "Bigotes patrulla el tejado ronroneando a la luz de la luna."),
            t("«¡Miau! Patrulla felina cumplida»", "«¡Miau! Patrulla felina cumplida»"),
        ),
    )

    fun suspect(id: String) = suspects.firstOrNull { it.id == id } ?: suspects.first()

    val phrase: Map<Attr, Map<String, Tx>> = mapOf(
        Attr.HAND to mapOf(
            "slant" to t("letra inclinada a la derecha con pequeñas lunas dibujadas", "letra inclinada a la derecha con pequeñas lunas dibujadas"),
            "round" to t("letra redonda y prolija como de caligrafía escolar", "letra redonda y prolija como de caligrafía escolar"),
            "quick" to t("letra rápida e impaciente que baila sobre el papel", "letra rápida e impaciente que baila sobre el papel"),
            "curly" to t("letra con adornos y florituras artísticas", "letra con adornos y florituras artísticas"),
            "print" to t("letra en mayúsculas rectas y precisas", "letra en mayúsculas rectas y precisas"),
            "paw" to t("marcas curiosas como pequeñas huellitas de almohadilla", "marcas curiosas como pequeñas huellitas de almohadilla"),
        ),
        Attr.WRAP to mapOf(
            "kraft" to t("papel kraft fino atado con un lazo carmesí", "papel kraft fino atado con un lazo carmesí"),
            "string" to t("cuerda rústica atada en cruz con nudo firme", "cuerda rústica atada en cruz con nudo firme"),
            "fabric" to t("retazos de tela suave con motivos florales", "retazos de tela suave con motivos florales"),
            "newspaper" to t("papel de periódico doblado con cuidado", "papel de periódico doblado con cuidado"),
            "paper" to t("hojas blancas de cuaderno de dibujo", "hojas blancas de cuaderno de dibujo"),
            "ribbon" to t("cinta dorada brillante bien anudada", "cinta dorada brillante bien anudada"),
            "napkin" to t("servilletas suaves de cafetería", "servilletas suaves de cafetería"),
            "wool" to t("hebras de lana roja enredadas", "hebras de lana roja enredadas"),
        ),
        Attr.STEPS to mapOf(
            "sneakers" to t("zapatillas cómodas y silenciosas", "zapatillas cómodas y silenciosas"),
            "shoes" to t("zapatos formales de cuero que resuenan suavemente", "zapatos formales de cuero que resuenan suavemente"),
            "heels" to t("tacones que dejan una marca fina y elegante", "tacones que dejan una marca fina y elegante"),
            "paws" to t("pequeñas huellas silenciosas de cuatro patitas", "pequeñas huellas silenciosas de cuatro patitas"),
        ),
        Attr.TIME to mapOf(
            "night" to t("altas horas de la madrugada (pasadas las dos)", "altas horas de la madrugada (pasadas las dos)"),
            "dawn" to t("muy temprano al amanecer (alrededor de las cinco)", "muy temprano al amanecer (alrededor de las cinco)"),
            "evening" to t("al caer la noche (cerca de las diez)", "al caer la noche (cerca de las diez)"),
            "early" to t("justo al atardecer, cuando la luz cae", "justo al atardecer, cuando la luz cae"),
        ),
        Attr.GAIT to mapOf(
            "hum" to t("un paso rítmico mientras tararea una melodía acústica", "un paso rítmico mientras tararea una melodía acústica"),
            "slow" to t("paso lento y pausado", "paso lento y pausado"),
            "quick" to t("pasos rápidos y enérgicos", "pasos rápidos y enérgicos"),
            "skip" to t("saltos ágiles y silenciosos", "saltos ágiles y silenciosos"),
        ),
        Attr.SMELL to mapOf(
            "coffee_wood" to t("madera de guitarra acústica y café recién hecho", "madera de guitarra acústica y café recién hecho"),
            "tea" to t("té de manzanilla y hierbas aromáticas", "té de manzanilla y hierbas aromáticas"),
            "cinnamon" to t("canela caliente y vainilla horneada", "canela caliente y vainilla horneada"),
            "mint" to t("menta fresca y aire limpio de la mañana", "menta fresca y aire limpio de la mañana"),
            "paint" to t("pintura al agua y acuarelas frescas", "pintura al agua y acuarelas frescas"),
            "oldbooks" to t("libros antiguos y papel añejo", "libros antiguos y papel añejo"),
            "eucalyptus" to t("eucalipto fresco y flores del jardín", "eucalipto fresco y flores del jardín"),
            "coffee" to t("café espresso tostado", "café espresso tostado"),
            "fish" to t("atún fresco para gatos", "atún fresco para gatos"),
        ),
    )

    val habit: Map<Attr, Map<String, Tx>> = mapOf(
        Attr.HAND to mapOf(
            "slant" to t("Escribe con letra inclinada a la derecha y dibuja pequeñas lunas.", "Escribe con letra inclinada a la derecha y dibuja pequeñas lunas."),
            "round" to t("Tiene letra redonda y prolija, de caligrafía escolar.", "Tiene letra redonda y prolija, de caligrafía escolar."),
            "quick" to t("Escribe con trazos veloces e impacientes.", "Escribe con trazos veloces e impacientes."),
            "curly" to t("Decora cada letra con florituras y adornos.", "Decora cada letra con florituras y adornos."),
            "print" to t("Escribe únicamente en letras de imprenta.", "Escribe únicamente en letras de imprenta."),
            "paw" to t("No sabe escribir; solo deja marquitas con sus patitas.", "No sabe escribir; solo deja marquitas con sus patitas."),
        ),
        Attr.WRAP to mapOf(
            "kraft" to t("Envuelve con papel kraft delicado y lazos rojos.", "Envuelve con papel kraft delicado y lazos rojos."),
            "string" to t("Ata los paquetes con cuerda rústica en cruz.", "Ata los paquetes con cuerda rústica en cruz."),
            "fabric" to t("Usa retazos de tela estampada.", "Usa retazos de tela estampada."),
            "newspaper" to t("Usa páginas de periódico dobladas.", "Usa páginas de periódico dobladas."),
            "paper" to t("Usa hojas de block de dibujo.", "Usa hojas de block de dibujo."),
            "ribbon" to t("Usa cinta dorada de regalo.", "Usa cinta dorada de regalo."),
            "napkin" to t("Usa servilletas de cafetería.", "Usa servilletas de cafetería."),
            "wool" to t("Usa hilos de lana roja.", "Usa hilos de lana roja."),
        ),
        Attr.STEPS to mapOf(
            "sneakers" to t("Calza zapatillas cómodas y silenciosas.", "Calza zapatillas cómodas y silenciosas."),
            "shoes" to t("Usa zapatos de vestir de suela de cuero.", "Usa zapatos de vestir de suela de cuero."),
            "heels" to t("Usa calzado con taco que resuena al andar.", "Usa calzado con taco que resuena al andar."),
            "paws" to t("Camina sobre cuatro almohadillas silenciosas.", "Camina sobre cuatro almohadillas silenciosas."),
        ),
        Attr.TIME to mapOf(
            "night" to t("Sale en plena madrugada, pasada la medianoche.", "Sale en plena madrugada, pasada la medianoche."),
            "dawn" to t("Se despierta a las cinco de la mañana para salir.", "Se despierta a las cinco de la mañana para salir."),
            "evening" to t("Sale alrededor de las diez de la noche.", "Sale alrededor de las diez de la noche."),
            "early" to t("Solo sale justo al atardecer.", "Solo sale justo al atardecer."),
        ),
        Attr.GAIT to mapOf(
            "hum" to t("Camina tarareando una melodía acústica con ritmo alegre.", "Camina tarareando una melodía acústica con ritmo alegre."),
            "slow" to t("Camina despacio, tomándose su tiempo.", "Camina despacio, tomándose su tiempo."),
            "quick" to t("Camina deprisa, casi corriendo por las escaleras.", "Camina deprisa, casi corriendo por las escaleras."),
            "skip" to t("Se mueve dando saltos ágiles y silenciosos.", "Se mueve dando saltos ágiles y silenciosos."),
        ),
        Attr.SMELL to mapOf(
            "coffee_wood" to t("Huele a madera de guitarra acústica y café recién hecho.", "Huele a madera de guitarra acústica y café recién hecho."),
            "tea" to t("Huele a manzanilla y hierbas aromáticas.", "Huele a manzanilla y hierbas aromáticas."),
            "cinnamon" to t("Huele a canela y vainilla recién horneada.", "Huele a canela y vainilla recién horneada."),
            "mint" to t("Huele a menta fresca.", "Huele a menta fresca."),
            "paint" to t("Huele a acuarelas y óleo.", "Huele a acuarelas y óleo."),
            "oldbooks" to t("Huele a páginas de libros antiguos.", "Huele a páginas de libros antiguos."),
            "eucalyptus" to t("Huele a eucalipto y flores frescas.", "Huele a eucalipto y flores frescas."),
            "coffee" to t("Huele a café tostado.", "Huele a café tostado."),
            "fish" to t("Huele a comida de gato.", "Huele a comida de gato."),
        ),
    )

    data class Clue(val day: Int, val attr: Attr?, val value: String?, val alibiOf: String?, val text: Tx) {
        fun allows(p: Suspect): Boolean = when {
            alibiOf != null -> p.id != alibiOf
            attr != null -> p.traits[attr] == value
            else -> true
        }
    }

    enum class Sharpness { MILD, DIRECT }
    data class Slot(val day: Int, val attr: Attr?, val alibi: Boolean = false)
    data class Pick(val slot: Slot, val sharpness: Sharpness)

    val slots = mapOf(
        1 to Slot(1, Attr.HAND),
        2 to Slot(2, Attr.SMELL),
        3 to Slot(3, Attr.TIME),
        4 to Slot(4, Attr.WRAP),
        5 to Slot(5, Attr.GAIT),
        6 to Slot(6, Attr.STEPS),
        7 to Slot(7, Attr.HAND),
        8 to Slot(8, Attr.TIME),
        9 to Slot(9, Attr.SMELL),
        10 to Slot(10, Attr.WRAP),
        11 to Slot(11, Attr.GAIT),
        12 to Slot(12, Attr.STEPS),
        13 to Slot(13, Attr.SMELL),
        14 to Slot(14, Attr.HAND),
        15 to Slot(15, Attr.TIME),
        16 to Slot(16, Attr.WRAP),
        17 to Slot(17, null, alibi = true),
        18 to Slot(18, Attr.GAIT),
        19 to Slot(19, Attr.SMELL),
        20 to Slot(20, Attr.STEPS),
        21 to Slot(21, Attr.HAND),
        22 to Slot(22, null, alibi = true),
        23 to Slot(23, Attr.WRAP),
    )

    fun clue(day: Int, culpritId: String, plan: Map<Int, Pick>): Clue? {
        val slot = slots[day] ?: return null
        val c = suspect(culpritId)
        val p = plan[day] ?: Pick(slot, Sharpness.DIRECT)

        if (slot.alibi) {
            val other = suspects.firstOrNull { it.id != culpritId && it.alibi != null } ?: suspects.first { it.id != culpritId }
            return Clue(day, null, null, other.id, other.alibi ?: t("Tiene una coartada comprobada.", "Tiene una coartada comprobada."))
        }

        val attr = slot.attr ?: return null
        val traitVal = c.traits.getValue(attr)
        val traitText = cleanLocalizedText(phrase.getValue(attr).getValue(traitVal).en)

        val fullText = when (attr) {
            Attr.HAND -> t("La nota encontrada tiene $traitText.", "La nota encontrada tiene $traitText.")
            Attr.WRAP -> t("El paquete estaba envuelto con $traitText.", "El paquete estaba envuelto con $traitText.")
            Attr.STEPS -> t("En el descansillo quedaron huellas de $traitText.", "En el descansillo quedaron huellas de $traitText.")
            Attr.TIME -> t("Quien dejó el detalle pasó a $traitText.", "Quien dejó el detalle pasó a $traitText.")
            Attr.GAIT -> t("Alguien escuchó alejarse $traitText.", "Alguien escuchó alejarse $traitText.")
            Attr.SMELL -> t("Cerca de la puerta quedó un inconfundible aroma a $traitText.", "Cerca de la puerta quedó un inconfundible aroma a $traitText.")
        }
        return Clue(day, attr, traitVal, null, fullText)
    }

    fun cleanLocalizedText(raw: String): String {
        val txRegex = Regex("""Tx\(ru=([^,]*),\s*en=([^)]*)\)""")
        return txRegex.replace(raw) { matchResult ->
            val en = matchResult.groupValues[2].trim()
            val ru = matchResult.groupValues[1].trim()
            if (en.isNotBlank()) en else ru
        }
    }

    fun plan(culpritId: String, random: Random = Random.Default): Map<Int, Pick> {
        return slots.mapValues { (_, slot) ->
            Pick(slot, Sharpness.DIRECT)
        }
    }

    fun encode(plan: Map<Int, Pick>): String = plan.entries.sortedBy { it.key }.joinToString(",") { "${it.key}:${it.value.sharpness.name.first()}" }
    fun decode(s: String): Map<Int, Pick> = if (s.isBlank()) emptyMap() else s.split(",").associate { chunk ->
        val (k, v) = chunk.split(":")
        val day = k.toInt()
        val slot = slots.getValue(day)
        day to Pick(slot, if (v == "M") Sharpness.MILD else Sharpness.DIRECT)
    }
}
