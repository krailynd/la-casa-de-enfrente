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
        Resident(1, 42, t("Тамара Ивановна", "Tamara Ivanovna"), t("кв. 17", "flat 17"), 0xFFC9A88A, 0xFF8A3B3B,
            t("Позвоните тому, кому давно собирались позвонить.", "Call someone you have been meaning to call for a while."),
            t("Тамара Ивановна каждый вечер ждёт звонка от сына из Владивостока. Там уже ночь, и звонит обычно она. Сегодня он позвонил первым.",
                "Every evening Tamara Ivanovna waits for her son to ring from Vladivostok. It is night there already, and usually she is the one who calls. Tonight he rang first."),
            listOf(
                Thing("armchair", 70f, note = t("Кресло продавлено ровно там, где удобно держать телефон.", "The armchair sags exactly where it's comfortable to hold a phone.")),
                Thing("plant", 285f, note = t("Герань. Цветёт даже в декабре, «назло».", "Geranium. It flowers even in December, “out of spite”.")),
                Thing("clock", 200f, note = t("Часы спешат на семь часов. Это время Владивостока.", "The clock is seven hours fast. That's Vladivostok time.")),
                Thing("envelope", 180f, 150f, clue = true),
            ),
            listOf(Look(70f, sit = true, hair = "bun", hairColor = 0xFFC9C9CF, top = 0xFF6A4A7A, pattern = "cardigan", bottom = 0xFF4A3A3A, skirt = true, arms = "phone", glasses = true, shawl = 0xFFB8484A))),
        Resident(2, 53, t("Гоша, 9 лет", "Gosha, 9"), t("кв. 23", "flat 23"), 0xFF7FA0B8, 0xFFD8A33A,
            t("Выйдите во двор, когда стемнеет, и постойте пять минут. Просто так.", "Step into the yard after dark and stand there for five minutes. Just because."),
            t("Гоша строит ракету из коробок от холодильника. Говорит, в январе она полетит. Мама говорит, в январе она поедет на помойку.",
                "Gosha is building a rocket out of fridge boxes. He says it will fly in January. His mum says in January it goes to the bins."),
            listOf(
                Thing("rocket", 250f, note = t("На борту написано: «ГОША-1». Иллюминатор из дуршлага.", "Painted on the side: “GOSHA-1”. The porthole is a colander.")),
                Thing("bed", 80f, note = t("Под подушкой фонарик и карта звёздного неба.", "Under the pillow: a torch and a star map.")),
                Thing("mitten", 150f, 183f, clue = true),
            ),
            listOf(Look(195f, kid = true, hair = "short", hairColor = 0xFFC46A2A, top = 0xFF2F6A9A, pattern = "stripes", bottom = 0xFF3A3A4A, arms = "wave", freckles = true)),
            mandarin = 318f to 186f),
        Resident(3, 21, t("Аркадий", "Arkady"), t("кв. 6", "flat 6"), 0xFF8D8F7A, 0xFF3E5A7A,
            t("Купите мандарины не себе и отдайте.", "Buy tangerines for someone else and give them away."),
            t("Аркадий таксует по ночам и спит днём. Чай на столе остывает уже третий раз.", "Arkady drives a taxi at night and sleeps by day. The tea on the table has gone cold for the third time."),
            listOf(
                Thing("table", 120f, note = t("Чай, сушки и шашечки «такси» вместо подставки.", "Tea, bagels, and a taxi sign used as a coaster.")),
                Thing("radio", 270f, note = t("Рация шипит: «Борт двенадцать, вы где?»", "The radio hisses: “Car twelve, where are you?”")),
                Thing("note", 96f, 124f, clue = true),
            ),
            listOf(Look(205f, hair = "flatcap", hat = 0xFF4A4A48, hairColor = 0xFF3A2A20, top = 0xFF6A5A48, moustache = true, sleepy = true, arms = "cup", skin = 0xFFE8B894))),
        Resident(4, 50, t("Соня", "Sonya"), t("кв. 21", "flat 21"), 0xFFB7A4C4, 0xFF4B6B4B,
            t("Напишите три строки человеку, который вас чему-то научил.", "Write three lines to someone who taught you something."),
            t("У Сони сессия. Над столом висит гирлянда, а на ней прищепками конспекты по сопромату.", "Sonya has exams. A string of lights hangs over her desk, with lecture notes pegged to it."),
            listOf(
                Thing("desk", 150f, note = t("Ноутбук, четыре кружки и шпаргалка, которую жалко выбросить.", "A laptop, four mugs and a crib sheet too good to throw away.")),
                Thing("shelf", 270f, note = t("Учебники и один томик Цветаевой, «для души».", "Textbooks, and one volume of Tsvetaeva “for the soul”.")),
                Thing("parcel", 94f, 186f, clue = true),
            ),
            listOf(Look(150f, sit = true, behind = true, hair = "long", hairColor = 0xFF2A1A14, top = 0xFFD27A7A, pattern = "hoodie", arms = "book", glasses = true))),
        Resident(5, 31, t("Лаптевы", "The Laptevs"), t("кв. 11", "flat 11"), 0xFFD6B97A, 0xFF3B6D8F,
            t("Испеките или купите что-нибудь к чаю и съешьте не торопясь, без телефона.", "Bake or buy something for tea and eat it slowly, without your phone."),
            t("Близнецы Лаптевы вырезают снежинки. На полу их уже на целый сугроб.", "The Laptev twins are cutting paper snowflakes. There's a whole snowdrift of them on the floor."),
            listOf(
                Thing("table", 170f, note = t("Ножницы, клей и тарелка с печеньем «ушки».", "Scissors, glue and a plate of palmier biscuits.")),
                Thing("garland", 0f, note = t("Гирлянда из бумажных снежинок. Одна получилась шестиугольной, как настоящая.", "A garland of paper snowflakes. One came out six-sided, like a real one.")),
                Thing("boots", 290f, clue = true),
            ),
            listOf(
                Look(145f, kid = true, behind = true, hair = "pigtails", hairColor = 0xFF8A5A2A, top = 0xFFC8483C, pattern = "nordic", arms = "hands"),
                Look(200f, kid = true, behind = true, hair = "short", hairColor = 0xFF8A5A2A, top = 0xFFC8483C, pattern = "nordic", arms = "wave", freckles = true),
            ),
            mandarin = 40f to 186f),
        Resident(6, 33, t("Вера Павловна", "Vera Pavlovna"), t("кв. 13", "flat 13"), 0xFF9FB39A, 0xFFA8423A,
            t("Переберите одну полку. Отдайте книгу, которую больше не откроете.", "Sort through one shelf. Give away a book you will never open again."),
            t("Вера Павловна сорок лет преподаёт литературу. Вечером она проверяет тетради и вяжет, иногда одновременно.", "Vera Pavlovna has taught literature for forty years. In the evenings she marks essays and knits, sometimes both at once."),
            listOf(
                Thing("shelf", 90f, note = t("Пушкин, Чехов и сто восемь тетрадей 7 «Б».", "Pushkin, Chekhov and a hundred and eight exercise books from class 7B.")),
                Thing("armchair", 250f, note = t("На подлокотнике лежит клубок красной пряжи.", "A ball of red wool sits on the armrest.")),
                Thing("mitten", 178f, 190f, clue = true),
            ),
            listOf(Look(250f, sit = true, hair = "bun", hairColor = 0xFF8A7A6A, top = 0xFF3A5A7A, pattern = "cardigan", skirt = true, bottom = 0xFF5A3A3A, arms = "knit", glasses = true))),
        Resident(7, 10, t("Рустам", "Rustam"), t("кв. 1, дворник", "flat 1, the caretaker"), 0xFFA58E6E, 0xFF566B3A,
            t("Уберите что-нибудь общее: подъезд, двор, лестницу. Хоть немного.", "Tidy something shared: the stairwell, the yard, the landing. Even a little."),
            t("Рустам чинит детям санки. У него целая очередь из сломанных полозьев.", "Rustam mends the children's sledges. There's a queue of broken runners."),
            listOf(
                Thing("sewing", 150f, note = t("Верстак. Тиски, отвёртки, банка с винтиками.", "A workbench. A vice, screwdrivers, a jar of screws.")),
                Thing("tv", 280f, note = t("Включён футбол без звука.", "Football on, sound off.")),
                Thing("boots", 60f, clue = true),
            ),
            listOf(Look(222f, hair = "cap", hat = 0xFF2A4A6A, hairColor = 0xFF2A1A14, beard = true, skin = 0xFFD9A57A, top = 0xFFE07A2A, pattern = "hivis", arms = "cup")),
            mandarin = 230f to 140f),
        Resident(8, 54, t("Майя и кот Бублик", "Maya and Bagel the cat"), t("кв. 24", "flat 24"), 0xFFE0C3A3, 0xFF7A3E6A,
            t("Сфотографируйте что-то красивое по дороге и никому не показывайте.", "Photograph something beautiful on your way and show it to no one."),
            t("Бублик сбежал вчера вечером, и Майя не спала до утра. А утром он сидел на коврике, сытый и довольный.", "Bagel ran off last night and Maya didn't sleep till morning. In the morning he was on the doormat, fed and pleased with himself."),
            listOf(
                Thing("cat", 200f, clue = true),
                Thing("plant", 70f, note = t("Кошачья мята. Объедена до корня.", "Catnip. Chewed down to the root.")),
                Thing("lamp", 300f, note = t("Торшер. Бублик спит под ним, когда тепло.", "A floor lamp. Bagel sleeps under it when it's warm.")),
            ),
            listOf(Look(125f, hair = "curly", hairColor = 0xFFB5482A, top = 0xFFE8D9C0, pattern = "nordic", arms = "cup", freckles = true))),
        Resident(9, 44, t("Лев Маркович", "Lev Markovich"), t("кв. 19", "flat 19"), 0xFF6F5A6E, 0xFFC9A25A,
            t("Послушайте целиком одну пластинку или альбом. Ничего не делая.", "Listen to one whole record or album. Doing nothing else."),
            t("Лев Маркович репетирует к детскому утреннику. Играет «В лесу родилась ёлочка» так, что соседи плачут.", "Lev Markovich is rehearsing for the children's party. He plays the Christmas-tree song so well the neighbours cry."),
            listOf(
                Thing("piano", 130f, note = t("«Красный Октябрь», 1961 год. Ля первой октавы западает.", "A “Red October” upright from 1961. The A above middle C sticks.")),
                Thing("frame", 280f, note = t("Афиша: «Л. М. Розен. Шопен». Ленинград, 1979.", "A poster: “L. M. Rosen. Chopin”. Leningrad, 1979.")),
                Thing("envelope", 150f, 96f, clue = true),
            ),
            listOf(Look(235f, hair = "bald", hairColor = 0xFFD8D8D8, top = 0xFF2A2A38, bow = true, glasses = true))),
        Resident(10, 14, t("Зоя", "Zoya"), t("кв. 3, медсестра", "flat 3, a nurse"), 0xFF9CB7C0, 0xFF4A4A6A,
            t("Лягте сегодня на час раньше.", "Go to bed an hour earlier tonight."),
            t("Зоя работает в ночную смену. Дома у неё всегда горит ночник, «чтобы не забыть, что я тоже дома».", "Zoya works nights. She always leaves a night light on at home, “so I remember I live here too”."),
            listOf(
                Thing("bed", 110f, note = t("Плед с оленями от бабушки.", "A blanket with reindeer, from her grandmother.")),
                Thing("lamp", 240f, note = t("Ночник на дежурстве.", "The night light, on duty.")),
                Thing("key", 300f, 150f, clue = true),
            ),
            listOf(Look(130f, sit = true, hair = "bun", hairColor = 0xFF3A2418, top = 0xFF6AA8A8, pattern = "scrubs", arms = "cup", sleepy = true))),
        Resident(11, 52, t("Кирилл и Аня", "Kirill and Anya"), t("кв. 22", "flat 22"), 0xFFC4CBB0, 0xFFB0573E,
            t("Скажите близкому «спасибо» за что-то конкретное.", "Thank someone close to you for one specific thing."),
            t("Первая ёлка вместе. Спорили, живая или искусственная. Купили обе.", "Their first tree together. They argued: real or artificial. They bought both."),
            listOf(
                Thing("tree", 90f, note = t("Живая. Пахнет на весь подъезд.", "The real one. The whole stairwell smells of it.")),
                Thing("tree", 230f, note = t("Искусственная. Зато иголки не осыпаются.", "The artificial one. At least it doesn't drop needles.")),
                Thing("parcel", 160f, 183f, clue = true),
            ),
            listOf(
                Look(140f, hair = "short", hairColor = 0xFF5A3A22, beard = true, top = 0xFF8A2F2F, pattern = "nordic"),
                Look(180f, hair = "long", hairColor = 0xFFE0C070, top = 0xFFE8DCC0, skirt = true, bottom = 0xFF5A6A8A),
            ),
            mandarin = 320f to 120f),
        Resident(12, 24, t("Николай Степанович", "Nikolai Stepanovich"), t("кв. 8", "flat 8"), 0xFF7D9A8C, 0xFF3A4F6A,
            t("Полейте цветы, свои или чужие.", "Water some plants, yours or someone else's."),
            t("Николай Степанович рыбак, но зимой рыбачит только взглядом: у него аквариум на двести литров.", "Nikolai Stepanovich is a fisherman, but in winter he only fishes with his eyes: he has a two-hundred-litre aquarium."),
            listOf(
                Thing("aquarium", 150f, note = t("Гуппи, два сомика и затонувший сундук.", "Guppies, two catfish and a sunken chest.")),
                Thing("radio", 285f, note = t("«Маяк». Прогноз: снег, ветер северный.", "Radio Mayak. Forecast: snow, north wind.")),
                Thing("note", 292f, 92f, clue = true),
            ),
            listOf(Look(72f, hair = "bald", hairColor = 0xFFBBBBBB, beard = true, top = 0xFF4A5A7A, pattern = "stripes"))),
        Resident(13, 22, t("Света и малыш", "Sveta and the baby"), t("кв. 7", "flat 7"), 0xFFE3C7C7, 0xFF5A7A9A,
            t("Напишите себе на завтра одну добрую фразу и положите в карман.", "Write yourself one kind sentence for tomorrow and put it in your pocket."),
            t("Малышу восемь месяцев. Он впервые видит снег и пытается поймать его через стекло.", "The baby is eight months old. He's seeing snow for the first time and trying to catch it through the glass."),
            listOf(
                Thing("bed", 90f, note = t("Кроватка. Сверху мобиль с бумажными звёздами.", "The cot, with a mobile of paper stars.")),
                Thing("gifts", 250f, note = t("Подарки, которые малыш разорвёт вместе с бумагой.", "Presents the baby will tear open, paper and all.")),
                Thing("mitten", 160f, 189f, clue = true),
            ),
            listOf(Look(200f, hair = "bun", hairColor = 0xFFC8A060, top = 0xFF9AB0D8, arms = "baby"))),
        Resident(14, 40, t("Ольга", "Olga"), t("кв. 16, швея", "flat 16, a seamstress"), 0xFFB39CB0, 0xFF5D8A6A,
            t("Почините одну вещь: пришейте пуговицу, зашейте дырку.", "Mend one thing: sew on a button, darn a hole."),
            t("Ольга шьёт костюмы снежинок для всего детского сада. Пятнадцать штук, и все разного размера.", "Olga is sewing snowflake costumes for the whole nursery. Fifteen of them, all different sizes."),
            listOf(
                Thing("sewing", 140f, note = t("«Чайка-142». Стучит, как поезд.", "A “Chaika-142”. It rattles like a train.")),
                Thing("frame", 270f, note = t("Выкройка снежинки на миллиметровке.", "A snowflake pattern on graph paper.")),
                Thing("mitten", 140f, 132f, clue = true),
            ),
            listOf(Look(140f, sit = true, behind = true, hair = "bob", hairColor = 0xFF2A1A1A, top = 0xFFC97A8A, pattern = "apron", glasses = true, arms = "hands"))),
        Resident(15, 51, t("Дима и Саша", "Dima and Sasha"), t("кв. 22", "flat 22"), 0xFF8CA6C9, 0xFFC97A3A,
            t("Пройдите сегодня три тысячи шагов просто так, без цели.", "Walk three thousand steps today, for no reason at all."),
            t("Братья заливают во дворе каток. Сосед снизу говорит, что заливают его.", "The brothers are flooding a skating rink in the yard. The neighbour downstairs says they're flooding him."),
            listOf(
                Thing("bed", 80f, note = t("Двухэтажная кровать, за верхний ярус идёт вечная война.", "A bunk bed. The top bunk is under permanent dispute.")),
                Thing("boots", 260f, clue = true),
                Thing("frame", 200f, note = t("Плакат «Трактора», подписанный кем-то неразборчиво.", "A hockey poster signed by someone illegible.")),
            ),
            listOf(
                Look(158f, kid = true, hair = "cap", hat = 0xFFC8483C, hairColor = 0xFF6A4A2A, top = 0xFF2A5A9A, pattern = "nordic", arms = "wave"),
                Look(198f, kid = true, hair = "cap", hat = 0xFF2A5A9A, hairColor = 0xFF6A4A2A, top = 0xFFC8483C, pattern = "nordic", freckles = true),
            )),
        Resident(16, 13, t("Бабушка Нина", "Granny Nina"), t("кв. 2", "flat 2"), 0xFFD2B48C, 0xFF7A4A3A,
            t("Найдите старую фотографию и расскажите о ней кому-нибудь.", "Find an old photograph and tell someone about it."),
            t("Бабушка Нина живёт в этом доме с 1971 года. Помнит всех, даже тех, кого уже не помнит никто.", "Granny Nina has lived in this building since 1971. She remembers everyone, even the ones nobody else does."),
            listOf(
                Thing("frame", 180f, clue = true),
                Thing("armchair", 80f, note = t("Кресло-качалка скрипит в ритме вальса.", "The rocking chair creaks in waltz time.")),
                Thing("clock", 290f, note = t("Часы с кукушкой. Кукушка устала и выходит через раз.", "A cuckoo clock. The cuckoo is tired and comes out every other hour.")),
            ),
            listOf(Look(80f, sit = true, hair = "kerchief", hat = 0xFFC84A5A, hairColor = 0xFFCCCCCC, top = 0xFF7A5A4A, shawl = 0xFFE8DCC0, skirt = true, bottom = 0xFF3A3A5A, arms = "book", glasses = true)),
            mandarin = 300f to 186f),
        Resident(17, 43, t("Гена", "Gena"), t("кв. 18, художник", "flat 18, a painter"), 0xFFC8C0A8, 0xFF2F4F6F,
            t("Нарисуйте что-нибудь за пять минут. Можно плохо.", "Draw something in five minutes. Badly is fine."),
            t("Гена рисует наш дом по памяти. Говорит, это единственный дом, который он знает наизусть.", "Gena paints our building from memory. He says it's the only house he knows by heart."),
            listOf(
                Thing("easel", 170f, clue = true),
                Thing("shelf", 60f, note = t("Банки из-под огурцов, в них кисти.", "Pickle jars full of brushes.")),
                Thing("plant", 322f, note = t("Фикус, который Гена рисовал сорок раз.", "A rubber plant Gena has painted forty times.")),
            ),
            listOf(Look(255f, hair = "beret", hat = 0xFF2A2A2A, hairColor = 0xFF4A3020, beard = true, top = 0xFFC9B07A, pattern = "apron", arms = "brush"))),
        Resident(18, 30, t("Ахмедовы", "The Akhmedovs"), t("кв. 9", "flat 9"), 0xFFC98F6B, 0xFF2F6A6A,
            t("Позовите кого-нибудь на чай. Можно на следующей неделе.", "Invite someone round for tea. Next week is fine."),
            t("У Ахмедовых всегда кто-то в гостях. Сегодня тоже, хотя никого не звали.", "The Akhmedovs always have guests. Tonight too, though nobody was invited."),
            listOf(
                Thing("table", 150f, note = t("Плов, самса, и чай уже по третьему кругу.", "Plov, samsa, and the third round of tea.")),
                Thing("honey", 176f, 124f, clue = true),
                Thing("tv", 300f, note = t("По телевизору «Ирония судьбы». В который раз.", "“The Irony of Fate” on TV. Yet again.")),
            ),
            listOf(
                Look(110f, sit = true, behind = true, hair = "short", hairColor = 0xFF1A1A1A, moustache = true, skin = 0xFFD6A07A, top = 0xFF2F6A6A, arms = "hands"),
                Look(148f, sit = true, behind = true, hair = "kerchief", hat = 0xFFC8483C, hairColor = 0xFF1A1A1A, skin = 0xFFE0AA82, top = 0xFFE0B04A, arms = "cup"),
                Look(198f, kid = true, behind = true, hair = "short", hairColor = 0xFF1A1A1A, skin = 0xFFD6A07A, top = 0xFF4A7A3A, pattern = "stripes", arms = "wave"),
            )),
        Resident(19, 41, t("Инна", "Inna"), t("кв. 16", "flat 16"), 0xFF4F5A6A, 0xFF9A9A9A,
            t("Час без экранов после заката.", "One hour without screens after sunset."),
            t("Инна программистка. Повесила камеру над дверью «на всякий случай», и вот этот случай наступил.", "Inna is a programmer. She put a camera over her door “just in case”, and now the case has come."),
            listOf(
                Thing("desk", 120f, note = t("Три монитора. Сегодня все выключены, и это непривычно.", "Three monitors. Tonight they're all off, which feels strange.")),
                Thing("coat", 270f, clue = true),
                Thing("plant", 40f, note = t("Кактус. Единственное, что у Инны выживает.", "A cactus. The only thing that survives at Inna's.")),
            ),
            listOf(Look(120f, sit = true, behind = true, hair = "bob", hairColor = 0xFFC86A9A, top = 0xFF3A3A4A, pattern = "hoodie", glasses = true, arms = "cup"))),
        Resident(20, 34, t("Дед Савва", "Grandad Savva"), t("кв. 14", "flat 14"), 0xFF8A7A5A, 0xFF6A3A3A,
            t("Узнайте прогноз погоды не из телефона, а по небу.", "Work out the weather from the sky, not from your phone."),
            t("Дед Савва слушает радио и спорит с ним. Радио пока выигрывает.", "Grandad Savva listens to the radio and argues with it. The radio is winning so far."),
            listOf(
                Thing("radio", 150f, clue = true),
                Thing("armchair", 270f, note = t("Кресло с газетой «Труд» за 1996 год.", "An armchair with a 1996 newspaper on it.")),
                Thing("clock", 60f, note = t("Ходики. Савва подтягивает гирю каждый вечер в девять.", "A weight clock. Savva pulls up the weight every evening at nine.")),
            ),
            listOf(Look(270f, sit = true, hair = "bald", hairColor = 0xFFE0E0E0, beard = true, skin = 0xFFE8B894, top = 0xFF6A5A3A, pattern = "cardigan", arms = "cup")),
            mandarin = 60f to 186f),
        Resident(21, 23, t("Лена и Петя", "Lena and Petya"), t("кв. 10", "flat 10"), 0xFFA9B8A0, 0xFF8A5A3A,
            t("Сыграйте во что-нибудь с живым человеком.", "Play a game with a real person."),
            t("Лена и Петя играют одну партию в шахматы с 2019 года. По ходу в неделю.", "Lena and Petya have been playing the same game of chess since 2019. One move a week."),
            listOf(
                Thing("chess", 170f, clue = true),
                Thing("shelf", 60f, note = t("Шахматные журналы стопками до потолка.", "Chess magazines stacked to the ceiling.")),
                Thing("lamp", 300f, note = t("Лампа, под которой думают.", "The lamp you think under.")),
            ),
            listOf(
                Look(115f, sit = true, stool = true, hair = "curly", hairColor = 0xFF3A2418, top = 0xFF8A4A6A),
                Look(228f, sit = true, stool = true, hair = "short", hairColor = 0xFF6A4A2A, glasses = true, top = 0xFF4A6A8A, pattern = "cardigan"),
            )),
        Resident(22, 11, t("Пётр Ильич", "Pyotr Ilyich"), t("кв. 4", "flat 4"), 0xFF9A8C74, 0xFF5A4A3A,
            t("Отнесите кому-нибудь подарок анонимно.", "Leave someone a present anonymously."),
            t("Пётр Ильич тридцать лет носил почту по этому кварталу. Теперь пьёт чай один, а свет у него горит до утра.", "Pyotr Ilyich delivered the post on this block for thirty years. Now he drinks his tea alone, and his light stays on till morning."),
            listOf(
                Thing("table", 130f, note = t("На столе бечёвка, сургуч, марки и спицы.", "On the table: string, sealing wax, stamps and knitting needles.")),
                Thing("coat", 280f, clue = true),
                Thing("clock", 60f, note = t("Часы стоят на без пяти полночь.", "The clock has stopped at five to midnight.")),
            ),
            listOf(Look(118f, sit = true, behind = true, hair = "bald", hairColor = 0xFFD0D0D0, moustache = true, glasses = true, top = 0xFF8A8A8A, pattern = "cardigan", arms = "knit"))),
        Resident(23, 32, t("Маша, 7 лет", "Masha, 7"), t("кв. 12", "flat 12"), 0xFFE8B4B8, 0xFF4A8A8A,
            t("Повесьте на окно что-нибудь: гирлянду или бумажную снежинку.", "Hang something in your window: lights or a paper snowflake."),
            t("Маша пишет письмо Деду Морозу. Третью страницу. Список подарков на обороте.", "Masha is writing to Grandfather Frost. Page three. The present list is on the back."),
            listOf(
                Thing("desk", 150f, note = t("Фломастеры, наклейки и письмо: «Дорогой Дедушка Мороз…»", "Felt tips, stickers and a letter: “Dear Grandfather Frost…”")),
                Thing("garland", 0f, note = t("Гирлянда-«фонарики». Синий не горит.", "A string of lantern lights. The blue one is out.")),
                Thing("note", 260f, 120f, clue = true),
            ),
            listOf(Look(150f, kid = true, behind = true, hair = "pigtails", hairColor = 0xFF3A2418, top = 0xFF4A8AD6, arms = "hands", freckles = true)),
            mandarin = 30f to 140f),
        Resident(24, 20, t("Квартира 5", "Flat 5"), t("пустовала с весны", "empty since spring"), 0xFFD9C9A3, 0xFF8A2F2F,
            t("Зажгите свечу на окне и выйдите посмотреть на свой дом снаружи.", "Light a candle in your window and go outside to look at your own home."),
            t("Квартира пустовала с весны. Сегодня в ней собрался весь подъезд: кто-то оставил ключ под ковриком и записку «Для всех».",
                "The flat stood empty since spring. Tonight the whole stairwell is in it: someone left a key under the mat and a note, “For everyone”."),
            listOf(
                Thing("tree", 170f, note = t("Ёлка, которую принёс неизвестно кто.", "A tree nobody admits to bringing.")),
                Thing("table", 280f, note = t("Каждый принёс что-нибудь. Получился стол на тридцать человек.", "Everyone brought something. Now there's a table for thirty.")),
                Thing("gifts", 60f, note = t("Двадцать три свёртка, и все упакованы одинаково.", "Twenty-three parcels, all wrapped the same way.")),
            ),
            listOf(
                Look(112f, hair = "bun", hairColor = 0xFFC9C9CF, top = 0xFF6A4A7A, pattern = "cardigan", skirt = true, bottom = 0xFF4A3A3A, glasses = true, arms = "cup", shawl = 0xFFB8484A),
                Look(142f, kid = true, hair = "short", hairColor = 0xFFC46A2A, top = 0xFF2F6A9A, pattern = "stripes"),
                Look(222f, hair = "bald", hairColor = 0xFFD0D0D0, moustache = true, glasses = true, top = 0xFF8A8A8A, pattern = "cardigan", arms = "wave"),
                Look(250f, kid = true, hair = "pigtails", hairColor = 0xFF3A2418, top = 0xFF4A8AD6, arms = "wave"),
            )),
    )

    fun resident(day: Int) = residents[day - 1]
    fun at(cell: Int) = residents.firstOrNull { it.cell == cell }
}
