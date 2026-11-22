package app.opposite.domain

/** A line in both languages. */
data class Tx(val ru: String, val en: String) {
    operator fun invoke(isRu: Boolean) = if (isRu) ru else en
}

/** A thing in a room. `x`, `y` are in the 360×250 room picture; `y` matters only for small things. */
data class Thing(val type: String, val x: Float, val y: Float = 0f, val note: Tx? = null, val clue: Boolean = false) {
    val small get() = type in SMALL

    companion object {
        val SMALL = setOf("envelope", "mitten", "parcel", "key", "note", "honey")
    }
}

/** How a neighbour looks. Drawn by PeopleArt; feet at `x` on the room floor. */
data class Look(
    val x: Float,
    val sit: Boolean = false,
    val kid: Boolean = false,
    val hair: String = "short",
    val hairColor: Long = 0xFF4A3022,
    val skin: Long = 0xFFF0C39D,
    val top: Long = 0xFF7A4A6A,
    val pattern: String = "plain",
    val bottom: Long = 0xFF3A3F55,
    val skirt: Boolean = false,
    val arms: String = "down",
    val glasses: Boolean = false,
    val beard: Boolean = false,
    val moustache: Boolean = false,
    val sleepy: Boolean = false,
    val freckles: Boolean = false,
    val hat: Long = 0xFF888888,
    val shawl: Long? = null,
    val bow: Boolean = false,
    val yarn: Long = 0xFFC8302C,
    val behind: Boolean = false,
    val stool: Boolean = false,
)

/**
 * One of the 24 windows. [cell] is floor×10 + column (floors 1..5, columns 0..4; 12 is the entrance).
 * The window lights on evening [day] of the season.
 */
data class Resident(
    val day: Int,
    val cell: Int,
    val name: Tx,
    val flat: Tx,
    val wall: Long,
    val curtain: Long,
    val task: Tx,
    val story: Tx,
    val things: List<Thing>,
    val people: List<Look>,
    val mandarin: Pair<Float, Float>? = null,
)

object House {
    private fun t(ru: String, en: String) = Tx(ru, en)

    val residents: List<Resident> = listOf(
        Resident(1, 42, t("Тамара Ивановна", "Tamara Ivanovna"), t("кв. 17", "apto. 17"), 0xFFC9A88A, 0xFF8A3B3B,
            t("Позвоните тому, кому давно собирались позвонить.", "Llama a esa persona a la que llevas tiempo queriendo llamar."),
            t("Тамара Ивановна каждый вечер ждёт звонка от сына из Владивостока. Там уже ночь, и звонит обычно она. Сегодня он позвонил первым.",
                "Cada noche Tamara Ivánovna espera la llamada de su hijo desde Vladivostok. Allí ya es de noche, y suele ser ella quien llama. Hoy él ha llamado primero."),
            listOf(
                Thing("armchair", 70f, note = t("Кресло продавлено ровно там, где удобно держать телефон.", "El sillón está hundido justo donde resulta cómodo sostener el teléfono.")),
                Thing("plant", 285f, note = t("Герань. Цветёт даже в декабре, «назло».", "Geranio. Florece incluso en diciembre, «por llevar la contraria».")),
                Thing("clock", 200f, note = t("Часы спешат на семь часов. Это время Владивостока.", "El reloj adelanta siete horas. Es la hora de Vladivostok.")),
                Thing("envelope", 180f, 150f, clue = true),
            ),
            listOf(Look(70f, sit = true, hair = "bun", hairColor = 0xFFC9C9CF, top = 0xFF6A4A7A, pattern = "cardigan", bottom = 0xFF4A3A3A, skirt = true, arms = "phone", glasses = true, shawl = 0xFFB8484A))),
        Resident(2, 53, t("Гоша, 9 лет", "Gosha, 9 años"), t("кв. 23", "apto. 23"), 0xFF7FA0B8, 0xFFD8A33A,
            t("Выйдите во двор, когда стемнеет, и постойте пять минут. Просто так.", "Sal al patio cuando anochezca y quédate allí cinco minutos. Sin motivo alguno."),
            t("Гоша строит ракету из коробок от холодильника. Говорит, в январе она полетит. Мама говорит, в январе она поедет на помойку.",
                "Gosha está construyendo un cohete con cajas de frigorífico. Dice que volará en enero. Su madre dice que en enero irá a la basura."),
            listOf(
                Thing("rocket", 250f, note = t("На борту написано: «ГОША-1». Иллюминатор из дуршлага.", "En un lateral dice: «GOSHA-1». La ventanilla es un colador.")),
                Thing("bed", 80f, note = t("Под подушкой фонарик и карта звёздного неба.", "Bajo la almohada: una linterna y un mapa estelar.")),
                Thing("mitten", 150f, 183f, clue = true),
            ),
            listOf(Look(195f, kid = true, hair = "short", hairColor = 0xFFC46A2A, top = 0xFF2F6A9A, pattern = "stripes", bottom = 0xFF3A3A4A, arms = "wave", freckles = true)),
            mandarin = 318f to 186f),
        Resident(3, 21, t("Аркадий", "Arkady"), t("кв. 6", "apto. 6"), 0xFF8D8F7A, 0xFF3E5A7A,
            t("Купите мандарины не себе и отдайте.", "Compra mandarinas para alguien más y regálalas."),
            t("Аркадий таксует по ночам и спит днём. Чай на столе остывает уже третий раз.", "Arkadi conduce un taxi de noche y duerme de día. El té sobre la mesa se ha enfriado por tercera vez."),
            listOf(
                Thing("table", 120f, note = t("Чай, сушки и шашечки «такси» вместо подставки.", "Té, rosquillas y un cartel de taxi usado como posavasos.")),
                Thing("radio", 270f, note = t("Рация шипит: «Борт двенадцать, вы где?»", "La radio sisea: «Coche doce, ¿dónde estás?»")),
                Thing("note", 96f, 124f, clue = true),
            ),
            listOf(Look(205f, hair = "flatcap", hat = 0xFF4A4A48, hairColor = 0xFF3A2A20, top = 0xFF6A5A48, moustache = true, sleepy = true, arms = "cup", skin = 0xFFE8B894))),
        Resident(4, 50, t("Соня", "Sonya"), t("кв. 21", "apto. 21"), 0xFFB7A4C4, 0xFF4B6B4B,
            t("Напишите три строки человеку, который вас чему-то научил.", "Escribe tres líneas a alguien que te haya enseñado algo en la vida."),
            t("У Сони сессия. Над столом висит гирлянда, а на ней прищепками конспекты по сопромату.", "Sonia está de exámenes. Una tira de luces cuelga sobre su escritorio, con apuntes sujetos con pinzas."),
            listOf(
                Thing("desk", 150f, note = t("Ноутбук, четыре кружки и шпаргалка, которую жалко выбросить.", "Un portátil, cuatro tazas y una chuleta que da demasiada pena tirar.")),
                Thing("shelf", 270f, note = t("Учебники и один томик Цветаевой, «для души».", "Libros de texto, y un tomo de Tsvetáyeva «para el alma».")),
                Thing("parcel", 94f, 186f, clue = true),
            ),
            listOf(Look(150f, sit = true, behind = true, hair = "long", hairColor = 0xFF2A1A14, top = 0xFFD27A7A, pattern = "hoodie", arms = "book", glasses = true))),
        Resident(5, 31, t("Лаптевы", "Los Láptev"), t("кв. 11", "apto. 11"), 0xFFD6B97A, 0xFF3B6D8F,
            t("Испеките или купите что-нибудь к чаю и съешьте не торопясь, без телефона.", "Hornea o compra algo para la merienda y tómalo sin prisa, sin el teléfono."),
            t("Близнецы Лаптевы вырезают снежинки. На полу их уже на целый сугроб.", "Los gemelos Láptev recortan copos de nieve de papel. Ya tienen un ventisquero entero sobre el suelo."),
            listOf(
                Thing("table", 170f, note = t("Ножницы, клей и тарелка с печеньем «ушки».", "Tijeras, pegamento y un plato de palmeras hojaldradas.")),
                Thing("garland", 0f, note = t("Гирлянда из бумажных снежинок. Одна получилась шестиугольной, как настоящая.", "Una guirnalda de copos de papel. Uno salió con seis puntas, como los de verdad.")),
                Thing("boots", 290f, clue = true),
            ),
            listOf(
                Look(145f, kid = true, behind = true, hair = "pigtails", hairColor = 0xFF8A5A2A, top = 0xFFC8483C, pattern = "nordic", arms = "hands"),
                Look(200f, kid = true, behind = true, hair = "short", hairColor = 0xFF8A5A2A, top = 0xFFC8483C, pattern = "nordic", arms = "wave", freckles = true),
            ),
            mandarin = 40f to 186f),
        Resident(6, 33, t("Вера Павловна", "Vera Pavlovna"), t("кв. 13", "apto. 13"), 0xFF9FB39A, 0xFFA8423A,
            t("Переберите одну полку. Отдайте книгу, которую больше не откроете.", "Ordena una estantería. Regala un libro que sepas que no volverás a abrir."),
            t("Вера Павловна сорок лет преподаёт литературу. Вечером она проверяет тетради и вяжет, иногда одновременно.", "Vera Pávlovna lleva cuarenta años enseñando literatura. Por las noches corrige redacciones y teje, a veces ambas cosas a la vez."),
            listOf(
                Thing("shelf", 90f, note = t("Пушкин, Чехов и сто восемь тетрадей 7 «Б».", "Pushkin, Chéjov y ciento ocho cuadernos de la clase 7.º B.")),
                Thing("armchair", 250f, note = t("На подлокотнике лежит клубок красной пряжи.", "Un ovillo de lana roja descansa en el apoyabrazos.")),
                Thing("mitten", 178f, 190f, clue = true),
            ),
            listOf(Look(250f, sit = true, hair = "bun", hairColor = 0xFF8A7A6A, top = 0xFF3A5A7A, pattern = "cardigan", skirt = true, bottom = 0xFF5A3A3A, arms = "knit", glasses = true))),
        Resident(7, 10, t("Рустам", "Rustam"), t("кв. 1, дворник", "apto. 1, conserje"), 0xFFA58E6E, 0xFF566B3A,
            t("Уберите что-нибудь общее: подъезд, двор, лестницу. Хоть немного.", "Limpia o arregla algo compartido: el portal, el patio, la escalera. Aunque sea un poco."),
            t("Рустам чинит детям санки. У него целая очередь из сломанных полозьев.", "Rustam arregla los trineos de los niños. Hay toda una fila de patines rotos esperando."),
            listOf(
                Thing("sewing", 150f, note = t("Верстак. Тиски, отвёртки, банка с винтиками.", "Un banco de trabajo: tornillo de banco, destornilladores, un tarro de tornillos.")),
                Thing("tv", 280f, note = t("Включён футбол без звука.", "Fútbol puesto, sin sonido.")),
                Thing("boots", 60f, clue = true),
            ),
            listOf(Look(222f, hair = "cap", hat = 0xFF2A4A6A, hairColor = 0xFF2A1A14, beard = true, skin = 0xFFD9A57A, top = 0xFFE07A2A, pattern = "hivis", arms = "cup")),
            mandarin = 230f to 140f),
        Resident(8, 54, t("Майя и кот Бублик", "Maya y el gato Bagel"), t("кв. 24", "apto. 24"), 0xFFE0C3A3, 0xFF7A3E6A,
            t("Сфотографируйте что-то красивое по дороге и никому не показывайте.", "Fotografía algo hermoso por el camino y no se lo enseñes a nadie."),
            t("Бублик сбежал вчера вечером, и Майя не спала до утра. А утром он сидел на коврике, сытый и довольный.", "Bagel se escapó anoche y Maya no durmió hasta el amanecer. Por la mañana estaba en el felpudo, comido y satisfecho."),
            listOf(
                Thing("cat", 200f, clue = true),
                Thing("plant", 70f, note = t("Кошачья мята. Объедена до корня.", "Hierba gatera. Mordisqueada hasta la raíz.")),
                Thing("lamp", 300f, note = t("Торшер. Бублик спит под ним, когда тепло.", "Una lámpara de pie. Bagel duerme bajo ella cuando está tibia.")),
            ),
            listOf(Look(125f, hair = "curly", hairColor = 0xFFB5482A, top = 0xFFE8D9C0, pattern = "nordic", arms = "cup", freckles = true))),
        Resident(9, 44, t("Лев Маркович", "Lev Markovich"), t("кв. 19", "apto. 19"), 0xFF6F5A6E, 0xFFC9A25A,
            t("Послушайте целиком одну пластинку или альбом. Ничего не делая.", "Escucha un disco o álbum entero de principio a fin. Sin hacer nada más."),
            t("Лев Маркович репетирует к детскому утреннику. Играет «В лесу родилась ёлочка» так, что соседи плачут.", "Lev Márkovich ensaya para la fiesta infantil. Toca la canción del abeto navideño con tanto sentimiento que emociona a los vecinos."),
            listOf(
                Thing("piano", 130f, note = t("«Красный Октябрь», 1961 год. Ля первой октавы западает.", "Un vertical «Octubre Rojo» de 1961. El la central a veces se encasquilla.")),
                Thing("frame", 280f, note = t("Афиша: «Л. М. Розен. Шопен». Ленинград, 1979.", "Un cartel: «L. M. Rosen. Chopin». Leningrado, 1979.")),
                Thing("envelope", 150f, 96f, clue = true),
            ),
            listOf(Look(235f, hair = "bald", hairColor = 0xFFD8D8D8, top = 0xFF2A2A38, bow = true, glasses = true))),
        Resident(10, 14, t("Зоя", "Zoya"), t("кв. 3, медсестра", "apto. 3, enfermera"), 0xFF9CB7C0, 0xFF4A4A6A,
            t("Лягте сегодня на час раньше.", "Acuéstate hoy una hora antes."),
            t("Зоя работает в ночную смену. Дома у неё всегда горит ночник, «чтобы не забыть, что я тоже дома».", "Zoya trabaja en el turno de noche. En casa siempre deja una lamparita encendida, «para recordar que yo también vivo aquí»."),
            listOf(
                Thing("bed", 110f, note = t("Плед с оленями от бабушки.", "Una manta con renos, regalo de su abuela.")),
                Thing("lamp", 240f, note = t("Ночник на дежурстве.", "La lamparita de noche, de guardia.")),
                Thing("key", 300f, 150f, clue = true),
            ),
            listOf(Look(130f, sit = true, hair = "bun", hairColor = 0xFF3A2418, top = 0xFF6AA8A8, pattern = "scrubs", arms = "cup", sleepy = true))),
        Resident(11, 52, t("Кирилл и Аня", "Kirill and Anya"), t("кв. 22", "apto. 22"), 0xFFC4CBB0, 0xFFB0573E,
            t("Скажите близкому «спасибо» за что-то конкретное.", "Da las gracias a alguien cercano por algo concreto."),
            t("Первая ёлка вместе. Спорили, живая или искусственная. Купили обе.", "Su primer árbol de Navidad juntos. Discutieron si natural o artificial. Compraron los dos."),
            listOf(
                Thing("tree", 90f, note = t("Живая. Пахнет на весь подъезд.", "El natural. Todo el portal huele a pino fresco.")),
                Thing("tree", 230f, note = t("Искусственная. Зато иголки не осыпаются.", "El artificial. Al menos a este no se le caen las agujas.")),
                Thing("parcel", 160f, 183f, clue = true),
            ),
            listOf(
                Look(140f, hair = "short", hairColor = 0xFF5A3A22, beard = true, top = 0xFF8A2F2F, pattern = "nordic"),
                Look(180f, hair = "long", hairColor = 0xFFE0C070, top = 0xFFE8DCC0, skirt = true, bottom = 0xFF5A6A8A),
            ),
            mandarin = 320f to 120f),
        Resident(12, 24, t("Николай Степанович", "Nikolai Stepanovich"), t("кв. 8", "apto. 8"), 0xFF7D9A8C, 0xFF3A4F6A,
            t("Полейте цветы, свои или чужие.", "Riega unas plantas, tuyas o de alguien más."),
            t("Николай Степанович рыбак, но зимой рыбачит только взглядом: у него аквариум на двести литров.", "Nikolái Stepánovich es pescador, pero en invierno solo pesca con la mirada: tiene un acuario de doscientos litros."),
            listOf(
                Thing("aquarium", 150f, note = t("Гуппи, два сомика и затонувший сундук.", "Guppys, dos peces gato y un cofre hundido.")),
                Thing("radio", 285f, note = t("«Маяк». Прогноз: снег, ветер северный.", "Radio Mayak. Previsión: nieve, viento del norte.")),
                Thing("note", 292f, 92f, clue = true),
            ),
            listOf(Look(72f, hair = "bald", hairColor = 0xFFBBBBBB, beard = true, top = 0xFF4A5A7A, pattern = "stripes"))),
        Resident(13, 22, t("Света и малыш", "Sveta y el bebé"), t("кв. 7", "apto. 7"), 0xFFE3C7C7, 0xFF5A7A9A,
            t("Напишите себе на завтра одну добрую фразу и положите в карман.", "Escríbete una frase amable para mañana y guárdala en el bolsillo."),
            t("Малышу восемь месяцев. Он впервые видит снег и пытается поймать его через стекло.", "El bebé tiene ocho meses. Ve la nieve por primera vez e intenta atraparla a través del cristal."),
            listOf(
                Thing("bed", 90f, note = t("Кроватка. Сверху мобиль с бумажными звёздами.", "La cuna, con un móvil de estrellas de papel.")),
                Thing("gifts", 250f, note = t("Подарки, которые малыш разорвёт вместе с бумагой.", "Regalos que el bebé romperá junto con el papel envoltorio.")),
                Thing("mitten", 160f, 189f, clue = true),
            ),
            listOf(Look(200f, hair = "bun", hairColor = 0xFFC8A060, top = 0xFF9AB0D8, arms = "baby"))),
        Resident(14, 40, t("Ольга", "Olga"), t("кв. 16, швея", "apto. 16, costurera"), 0xFFB39CB0, 0xFF5D8A6A,
            t("Почините одну вещь: пришейте пуговицу, зашейте дырку.", "Arregla algo roto: cose un botón, zurce un agujero."),
            t("Ольга шьёт костюмы снежинок для всего детского сада. Пятнадцать штук, и все разного размера.", "Olga está cosiendo disfraces de copos de nieve para toda la guardería. Quince en total, todos de tallas distintas."),
            listOf(
                Thing("sewing", 140f, note = t("«Чайка-142». Стучит, как поезд.", "Una máquina «Chaika-142». Tintinea como un tren.")),
                Thing("frame", 270f, note = t("Выкройка снежинки на миллиметровке.", "Un patrón de copo de nieve sobre papel milimetrado.")),
                Thing("mitten", 140f, 132f, clue = true),
            ),
            listOf(Look(140f, sit = true, behind = true, hair = "bob", hairColor = 0xFF2A1A1A, top = 0xFFC97A8A, pattern = "apron", glasses = true, arms = "hands"))),
        Resident(15, 51, t("Дима и Саша", "Dima and Sasha"), t("кв. 22", "apto. 22"), 0xFF8CA6C9, 0xFFC97A3A,
            t("Пройдите сегодня три тысячи шагов просто так, без цели.", "Camina hoy tres mil pasos sin prisa, sin rumbo fijo."),
            t("Братья заливают во дворе каток. Сосед снизу говорит, что заливают его.", "Los hermanos están inundando una pista de patinaje en el patio. El vecino de abajo dice que lo están inundando a él."),
            listOf(
                Thing("bed", 80f, note = t("Двухэтажная кровать, за верхний ярус идёт вечная война.", "Una litera. La cama de arriba es motivo de disputa eterna.")),
                Thing("boots", 260f, clue = true),
                Thing("frame", 200f, note = t("Плакат «Трактора», подписанный кем-то неразборчиво.", "Un póster de hockey firmado por alguien con letra ilegible.")),
            ),
            listOf(
                Look(158f, kid = true, hair = "cap", hat = 0xFFC8483C, hairColor = 0xFF6A4A2A, top = 0xFF2A5A9A, pattern = "nordic", arms = "wave"),
                Look(198f, kid = true, hair = "cap", hat = 0xFF2A5A9A, hairColor = 0xFF6A4A2A, top = 0xFFC8483C, pattern = "nordic", freckles = true),
            )),
        Resident(16, 13, t("Бабушка Нина", "Abuela Nina"), t("кв. 2", "apto. 2"), 0xFFD2B48C, 0xFF7A4A3A,
            t("Найдите старую фотографию и расскажите о ней кому-нибудь.", "Busca una fotografía antigua y cuéntale su historia a alguien."),
            t("Бабушка Нина живёт в этом доме с 1971 года. Помнит всех, даже тех, кого уже не помнит никто.", "La abuela Nina vive en este edificio desde 1971. Se acuerda de todos, incluso de aquellos a los que ya nadie recuerda."),
            listOf(
                Thing("frame", 180f, clue = true),
                Thing("armchair", 80f, note = t("Кресло-качалка скрипит в ритме вальса.", "La mecedora cruje a ritmo de vals.")),
                Thing("clock", 290f, note = t("Часы с кукушкой. Кукушка устала и выходит через раз.", "Un reloj de cuco. El cuco está cansado y solo sale una de cada dos horas.")),
            ),
            listOf(Look(80f, sit = true, hair = "kerchief", hat = 0xFFC84A5A, hairColor = 0xFFCCCCCC, top = 0xFF7A5A4A, shawl = 0xFFE8DCC0, skirt = true, bottom = 0xFF3A3A5A, arms = "book", glasses = true)),
            mandarin = 300f to 186f),
        Resident(17, 43, t("Гена", "Gena"), t("кв. 18, художник", "apto. 18, pintor"), 0xFFC8C0A8, 0xFF2F4F6F,
            t("Нарисуйте что-нибудь за пять минут. Можно плохо.", "Dibuja algo en cinco minutos. No importa si no queda bien."),
            t("Гена рисует наш дом по памяти. Говорит, это единственный дом, который он знает наизусть.", "Gena pinta nuestro edificio de memoria. Dice que es la única casa que se sabe de memoria."),
            listOf(
                Thing("easel", 170f, clue = true),
                Thing("shelf", 60f, note = t("Банки из-под огурцов, в них кисти.", "Botes de pepinillos llenos de pinceles.")),
                Thing("plant", 322f, note = t("Фикус, который Гена рисовал сорок раз.", "Un ficus que Gena ha pintado cuarenta veces.")),
            ),
            listOf(Look(255f, hair = "beret", hat = 0xFF2A2A2A, hairColor = 0xFF4A3020, beard = true, top = 0xFFC9B07A, pattern = "apron", arms = "brush"))),
        Resident(18, 30, t("Ахмедовы", "Los Ajmédov"), t("кв. 9", "apto. 9"), 0xFFC98F6B, 0xFF2F6A6A,
            t("Позовите кого-нибудь на чай. Можно на следующей неделе.", "Invita a alguien a merendar o tomar un café. Puede ser la próxima semana."),
            t("У Ахмедовых всегда кто-то в гостях. Сегодня тоже, хотя никого не звали.", "En casa de los Ajmédov siempre hay invitados. Esta noche también, aunque no habían invitado a nadie formalmente."),
            listOf(
                Thing("table", 150f, note = t("Плов, самса, и чай уже по третьему кругу.", "Plov, empanadas y la tercera ronda de té.")),
                Thing("honey", 176f, 124f, clue = true),
                Thing("tv", 300f, note = t("По телевизору «Ирония судьбы». В который раз.", "En la tele «La ironía del destino». Una vez más.")),
            ),
            listOf(
                Look(110f, sit = true, behind = true, hair = "short", hairColor = 0xFF1A1A1A, moustache = true, skin = 0xFFD6A07A, top = 0xFF2F6A6A, arms = "hands"),
                Look(148f, sit = true, behind = true, hair = "kerchief", hat = 0xFFC8483C, hairColor = 0xFF1A1A1A, skin = 0xFFE0AA82, top = 0xFFE0B04A, arms = "cup"),
                Look(198f, kid = true, behind = true, hair = "short", hairColor = 0xFF1A1A1A, skin = 0xFFD6A07A, top = 0xFF4A7A3A, pattern = "stripes", arms = "wave"),
            )),
        Resident(19, 41, t("Инна", "Inna"), t("кв. 16", "apto. 16"), 0xFF4F5A6A, 0xFF9A9A9A,
            t("Час без экранов после заката.", "Pasa una hora sin pantallas tras la puesta de sol."),
            t("Инна программистка. Повесила камеру над дверью «на всякий случай», и вот этот случай наступил.", "Inna es programadora. Puso una cámara sobre su puerta «por si acaso», y ese caso por fin ha llegado."),
            listOf(
                Thing("desk", 120f, note = t("Три монитора. Сегодня все выключены, и это непривычно.", "Tres monitores. Esta noche están todos apagados, lo cual se siente extraño.")),
                Thing("coat", 270f, clue = true),
                Thing("plant", 40f, note = t("Кактус. Единственное, что у Инны выживает.", "Un cactus. Lo único que sobrevive en casa de Inna.")),
            ),
            listOf(Look(120f, sit = true, behind = true, hair = "bob", hairColor = 0xFFC86A9A, top = 0xFF3A3A4A, pattern = "hoodie", glasses = true, arms = "cup"))),
        Resident(20, 34, t("Дед Савва", "Abuelo Savva"), t("кв. 14", "apto. 14"), 0xFF8A7A5A, 0xFF6A3A3A,
            t("Узнайте прогноз погоды не из телефона, а по небу.", "Averigua el tiempo mirando al cielo, no en el teléfono."),
            t("Дед Савва слушает радио и спорит с ним. Радио пока выигрывает.", "El abuelo Savva escucha la radio y discute con ella. Por ahora va ganando la radio."),
            listOf(
                Thing("radio", 150f, clue = true),
                Thing("armchair", 270f, note = t("Кресло с газетой «Труд» за 1996 год.", "Un sillón con un periódico de 1996 encima.")),
                Thing("clock", 60f, note = t("Ходики. Савва подтягивает гирю каждый вечер в девять.", "Un reloj de pesas. Savva sube la pesa todas las noches a las nueve en punto.")),
            ),
            listOf(Look(270f, sit = true, hair = "bald", hairColor = 0xFFE0E0E0, beard = true, skin = 0xFFE8B894, top = 0xFF6A5A3A, pattern = "cardigan", arms = "cup")),
            mandarin = 60f to 186f),
        Resident(21, 23, t("Лена и Петя", "Lena and Petya"), t("кв. 10", "apto. 10"), 0xFFA9B8A0, 0xFF8A5A3A,
            t("Сыграйте во что-нибудь с живым человеком.", "Juega a algo de mesa o cartas con alguien en persona."),
            t("Лена и Петя играют одну партию в шахматы с 2019 года. По ходу в неделю.", "Lena y Petya juegan la misma partida de ajedrez desde 2019. Un movimiento a la semana."),
            listOf(
                Thing("chess", 170f, clue = true),
                Thing("shelf", 60f, note = t("Шахматные журналы стопками до потолка.", "Revistas de ajedrez apiladas hasta el techo.")),
                Thing("lamp", 300f, note = t("Лампа, под которой думают.", "La lámpara bajo la que se piensa.")),
            ),
            listOf(
                Look(115f, sit = true, stool = true, hair = "curly", hairColor = 0xFF3A2418, top = 0xFF8A4A6A),
                Look(228f, sit = true, stool = true, hair = "short", hairColor = 0xFF6A4A2A, glasses = true, top = 0xFF4A6A8A, pattern = "cardigan"),
            )),
        Resident(22, 11, t("Пётр Ильич", "Pyotr Ilyich"), t("кв. 4", "apto. 4"), 0xFF9A8C74, 0xFF5A4A3A,
            t("Отнесите кому-нибудь подарок анонимно.", "Deja un detalle o regalo a alguien de forma anónima."),
            t("Пётр Ильич тридцать лет носил почту по этому кварталу. Теперь пьёт чай один, а свет у него горит до утра.", "Piotr Ilich repartió el correo en esta manzana durante treinta años. Ahora toma el té solo, y su luz permanece encendida hasta el amanecer."),
            listOf(
                Thing("table", 130f, note = t("На столе бечёвка, сургуч, марки и спицы.", "Sobre la mesa: cuerda, lacre, sellos y agujas de tejer.")),
                Thing("coat", 280f, clue = true),
                Thing("clock", 60f, note = t("Часы стоят на без пяти полночь.", "El reloj se ha detenido a las doce menos cinco.")),
            ),
            listOf(Look(118f, sit = true, behind = true, hair = "bald", hairColor = 0xFFD0D0D0, moustache = true, glasses = true, top = 0xFF8A8A8A, pattern = "cardigan", arms = "knit"))),
        Resident(23, 32, t("Маша, 7 лет", "Masha, 7 años"), t("кв. 12", "apto. 12"), 0xFFE8B4B8, 0xFF4A8A8A,
            t("Повесьте на окно что-нибудь: гирлянду или бумажную снежинку.", "Cuelga algo en tu ventana: unas luces o un copo de nieve de papel."),
            t("Маша пишет письмо Деду Морозу. Третью страницу. Список подарков на обороте.", "Masha está escribiendo su carta a Papá Noel. Ya va por la página tres. La lista de regalos continúa al dorso."),
            listOf(
                Thing("desk", 150f, note = t("Фломастеры, наклейки и письмо: «Дорогой Дедушка Мороз…»", "Rotuladores, pegatinas y una carta: «Querido Papá Noel…»")),
                Thing("garland", 0f, note = t("Гирлянда-«фонарики». Синий не горит.", "Una guirnalda de farolillos. El azul no se enciende.")),
                Thing("note", 260f, 120f, clue = true),
            ),
            listOf(Look(150f, kid = true, behind = true, hair = "pigtails", hairColor = 0xFF3A2418, top = 0xFF4A8AD6, arms = "hands", freckles = true)),
            mandarin = 30f to 140f),
        Resident(24, 20, t("El Músico de Medianoche", "El Músico de Medianoche"), t("El Refugio de Nosotros", "El Refugio de Nosotros"), 0xFFD9C9A3, 0xFFC81D31,
            t("Mira a tu alrededor y sonríe: Nochebuena llegó llena de amor.", "Mira a tu alrededor y sonríe: Nochebuena llegó llena de amor."),
            t("La habitación de la música donde Elvis compuso cada melodía y cada detalle para ti. Esta noche todo el misterio se ilumina bajo la luz dorada y roja.",
                "La habitación de la música donde Elvis compuso cada melodía y cada detalle para ti. Esta noche todo el misterio se ilumina bajo la luz dorada y roja."),
            listOf(
                Thing("tree", 170f, note = t("Un árbol navideño decorado con lazos rojos y estrellas doradas.", "Un árbol navideño decorado con lazos rojos y estrellas doradas.")),
                Thing("table", 280f, note = t("Una guitarra acústica en Sol# mayor con partituras y flores amarillas.", "Una guitarra acústica en Sol# mayor con partituras y flores amarillas.")),
                Thing("gifts", 60f, note = t("Veinticuatro detalles llenos de amor sincero para Principessa.", "Veinticuatro detalles llenos de amor sincero para Principessa.")),
                Thing("envelope", 180f, 150f, clue = true),
            ),
            listOf(
                Look(112f, hair = "short", hairColor = 0xFF2A1A10, top = 0xFFC81D31, bottom = 0xFF241118, arms = "wave"),
            )),
    )

    fun resident(day: Int) = residents[day - 1]
    fun at(cell: Int) = residents.firstOrNull { it.cell == cell }
}
