package app.opposite.domain

import kotlin.random.Random

/**
 * Who leaves the presents under the doors? Ten neighbours could be the Secret Grandfather; each season
 * one of them is. Every suspect has six habits (handwriting, wrapping, footprints, hour, gait, smell).
 * Each lit window gives one clue about the real one — mostly a vague yes/no about a habit — and a
 * «director» picks how sharp each clue is so the answer narrows slowly: four suspects left till about
 * the 10th, two by the 18th, one only on the 21st–23rd.
 */
object Mystery {
    enum class Attr(val ru: String, val en: String) { HAND("почерк", "handwriting"), WRAP("упаковка", "wrapping"), STEPS("следы", "footprints"), TIME("время", "hour"), GAIT("походка", "gait"), SMELL("запах", "smell") }

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
        Suspect("petr", 22, t("Пётр Ильич", "Pyotr Ilyich"), t("почтальон на пенсии", "retired postman"), a("slant", "string", "valenki", "night", "limp", "wax"),
            t("Тридцать лет носил почту по этому кварталу.", "Delivered the post on this block for thirty years."), null,
            t("Тридцать лет он носил письма этому дому и знает каждого по имени и по почерку. На пенсии писем не стало, и он начал носить их сам. Посылки вязал бечёвкой, как привык на почте, и выходил к трём ночи, когда больная нога всё равно не даёт спать.",
                "For thirty years he carried this building's letters and knows everyone by name and by handwriting. When he retired the letters stopped, so he started delivering his own. He tied the parcels with string the post-office way, and went out at three in the morning, when his bad leg wouldn't let him sleep anyway."),
            t("«Спасибо, что смотрели на наши окна. Ваш П. И.»", "“Thank you for watching our windows. Yours, P. I.”")),
        Suspect("vera", 6, t("Вера Павловна", "Vera Pavlovna"), t("учительница", "teacher"), a("round", "paper", "heels", "evening", "slow", "chalk"),
            t("Сорок лет преподаёт литературу.", "Has taught literature for forty years."),
            t("«Вера Павловна? Да она с давлением лежит, на лестницу не выходит. Я ей сам хлеб ношу».", "“Vera Pavlovna? She's in bed with her blood pressure, doesn't go near the stairs. I take her bread myself.”"),
            t("Сорок лет Вера Павловна ставила оценки. В этом году ей захотелось ставить только пятёрки, всем и сразу. Подарки она заворачивала в тетрадные листы, а спускалась поздно вечером, когда последнее сочинение было проверено.",
                "For forty years Vera Pavlovna handed out marks. This year she wanted to give only top marks, to everyone at once. She wrapped the presents in exercise-book pages and came down late in the evening, once the last essay was marked."),
            t("«Всем отлично. В. П.»", "“Excellent, all of you. V. P.”")),
        Suspect("gosha", 2, t("Гоша", "Gosha"), t("9 лет", "aged 9"), a("print", "newspaper", "kidboots", "early", "skip", "tangerine"),
            t("Строит ракету из коробок. Знает всех во дворе.", "Building a rocket from boxes. Knows everyone in the yard."),
            t("«Гоша? У него ключей нет, мать запирает. Да и откуда у мальчишки деньги на мёд?»", "“Gosha? He hasn't got keys, his mum locks him in. And where would a boy get money for honey?”"),
            t("Гоша копил на телескоп с сентября. А потом решил, что телескоп подождёт: в доме всем грустно, а он знает, кто где живёт, лучше любого почтальона. Подарки заворачивал в мамины газеты и разносил сразу после уроков, вприпрыжку. Себе он тоже подарил варежки, для конспирации.",
                "Gosha had been saving for a telescope since September. Then he decided the telescope could wait: everyone in the building was sad, and he knows who lives where better than any postman. He wrapped the presents in his mum's newspapers and delivered them straight after school, skipping. He gave himself mittens too, as cover."),
            t("«ГОША-1 выходит на связь. Г.»", "“GOSHA-1 calling. G.”")),
        Suspect("rustam", 7, t("Рустам", "Rustam"), t("дворник", "caretaker"), a("print", "newspaper", "valenki", "dawn", "quick", "tar"),
            t("Двадцать лет метёт этот двор и чинит детям санки.", "Has swept this yard for twenty years and mends the children's sledges."),
            t("«Рустам по ночам спит как убитый, весь подъезд слышит храп через стену».", "“Rustam sleeps like a log at night, the whole stairwell hears him snoring through the wall.”"),
            t("Рустам видит дом раньше всех: в шесть утра, с метлой. Он знает, у кого сломались санки, кто давно не получал писем и кто плакал на лавочке. Подарки он оставлял, пока подметал лестницу, и заворачивал в газеты из макулатуры.",
                "Rustam sees the building before anyone else: at six in the morning, broom in hand. He knows whose sledge broke, who hasn't had a letter in ages and who cried on the bench. He left the presents while he swept the stairs, wrapped in newspapers from the recycling."),
            t("«С наступающим. Ваш дворник»", "“Happy holidays. Your caretaker”")),
        Suspect("sonya", 4, t("Соня", "Sonya"), t("студентка", "student"), a("quick", "paper", "sneakers", "night", "quick", "coffee"),
            t("Пятый курс, сессия, сопромат.", "Final year, exams, structural mechanics."),
            t("«Соня? У неё стипендия три тысячи, она на проездной еле наскребает».", "“Sonya? Her grant is three thousand roubles, she can barely afford her bus pass.”"),
            t("Соня не спала из-за сессии, и в три часа ночи, между билетами, ей становилось одиноко. Тогда она бегом спускалась по лестнице с маленькими свёртками в тетрадных листах. «Так хотя бы кто-то утром улыбнётся».",
                "Sonya couldn't sleep for exams, and at three in the morning, between revision questions, she got lonely. So she ran down the stairs with little parcels wrapped in exercise-book pages. “At least someone will smile in the morning.”"),
            t("«Сдам и вернусь с новыми. С.»", "“Back with more after the exams. S.”")),
        Suspect("lev", 9, t("Лев Маркович", "Lev Markovich"), t("пианист", "pianist"), a("curly", "notes", "shoes", "evening", "hum", "rosin"),
            t("Когда-то играл Шопена в Ленинграде. Теперь аккомпанирует на утренниках.", "Once played Chopin in Leningrad. Now accompanies children's parties."),
            t("«Лев Маркович неделю назад раздавил очки, а без них дальше двери не видит».", "“Lev Markovich sat on his glasses a week ago, and without them he can't see past his own door.”"),
            t("Лев Маркович сорок лет давал концерты, а теперь его никто не зовёт. Тогда он придумал концерт без сцены: каждый вечер после репетиции поднимался по лестнице, напевал и оставлял у дверей что-нибудь в нотной бумаге.",
                "Lev Markovich gave concerts for forty years, and now nobody asks him. So he invented a concert without a stage: every evening after rehearsal he climbed the stairs, humming, and left something wrapped in manuscript paper at each door."),
            t("«Фортиссимо! Л. М.»", "“Fortissimo! L. M.”")),
        Suspect("zoya", 10, t("Зоя", "Zoya"), t("медсестра", "nurse"), a("quick", "bandage", "sneakers", "dawn", "quick", "iodine"),
            t("Работает в ночную смену в районной больнице.", "Works night shifts at the district hospital."),
            t("«Зоя после ночной смены приходит и падает. Я её видел: на ногах не стоит».", "“Zoya comes home from her night shift and drops. I've seen her: she can barely stand.”"),
            t("На работе Зоя видит много грустного. Возвращаясь со смены в шесть утра, она решила, что хотя бы в её доме всё будет хорошо. Ленточек под рукой не было, поэтому подарки она перевязывала бинтом.",
                "Zoya sees a lot of sadness at work. Coming home from her shift at six in the morning, she decided that in her own building, at least, things would be good. She had no ribbon to hand, so she tied the presents with bandage."),
            t("«Будьте здоровы. З.»", "“Stay well. Z.”")),
        Suspect("olga", 14, t("Ольга", "Olga"), t("швея", "seamstress"), a("round", "fabric", "heels", "evening", "hum", "oil"),
            t("Шьёт костюмы снежинок для всего детского сада.", "Sews snowflake costumes for the whole nursery."),
            t("«У Ольги рука в гипсе, на катке упала. Какие уж тут свёртки».", "“Olga's arm is in plaster, she fell on the rink. No parcels from her.”"),
            t("Каждый вечер у Ольги оставался мешок обрезков от снежинок. Обрезки становились обёрткой, а обёртка — поводом зайти к соседям, с которыми она двадцать лет здоровалась только кивком.",
                "Every evening Olga was left with a bag of offcuts from the snowflakes. The offcuts became wrapping, and the wrapping became an excuse to visit neighbours she'd only nodded to for twenty years."),
            t("«Сшито с любовью. О.»", "“Sewn with love. O.”")),
        Suspect("arkady", 3, t("Аркадий", "Arkady"), t("таксист", "taxi driver"), a("slant", "map", "shoes", "night", "quick", "petrol"),
            t("Ночами возит людей, днём спит.", "Drives people around at night, sleeps by day."),
            t("«Аркадий? У него машина третью неделю в ремонте, он у брата в Мытищах живёт».", "“Arkady? His car's been in the garage three weeks, he's staying at his brother's out of town.”"),
            t("Аркадий по ночам развозит чужих людей на чужие праздники. В эту зиму он решил, что праздник будет и в его доме. Между заказами заезжал во двор, оставлял свёрток в старой карте города и уезжал, пока никто не проснулся.",
                "At night Arkady drives strangers to other people's parties. This winter he decided there'd be a party in his own building too. Between fares he pulled into the yard, left a parcel wrapped in an old city map, and was gone before anyone woke."),
            t("«Поездка оплачена. А.»", "“This ride is paid for. A.”")),
        Suspect("nina", 16, t("Бабушка Нина", "Granny Nina"), t("живёт тут с 1971", "here since 1971"), a("curly", "fabric", "cane", "early", "slow", "perfume"),
            t("Помнит всех, кто когда-либо жил в этом доме.", "Remembers everyone who ever lived in this building."),
            t("«Нина Петровна до пятого этажа не дойдёт, а подарки и там были».", "“Nina Petrovna can't climb to the fifth floor, and there were presents up there too.”"),
            t("Нина Петровна помнит, кому что дарили на каждый Новый год с 1971-го. Ей просто стало жаль, что теперь никто не дарит. Она ходила медленно, с палочкой, засветло, пока все на работе, и заворачивала подарки в лоскуты от старых платьев.",
                "Nina Petrovna remembers who gave what every New Year since 1971. It simply made her sad that nobody gives anything now. She went slowly, with her stick, in daylight while everyone was at work, and wrapped the presents in scraps of old dresses."),
            t("«Ваша соседка с первого этажа»", "“Your neighbour from the ground floor”")),
    )

    fun suspect(id: String) = suspects.first { it.id == id }

    /** How a habit sounds inside a clue. */
    val phrase: Map<Attr, Map<String, Tx>> = mapOf(
        Attr.HAND to mapOf(
            "slant" to t("размашистый, с сильным наклоном вправо", "sweeping, with a strong slant to the right"),
            "print" to t("никакой: всё печатными буквами", "none to speak of: all block capitals"),
            "round" to t("круглый и аккуратный, как в прописях", "round and neat, like a copybook"),
            "quick" to t("торопливый, буквы скачут", "hurried, the letters jumping about"),
            "curly" to t("с завитушками, как на старых открытках", "full of curls, like an old postcard")),
        Attr.WRAP to mapOf(
            "string" to t("перевязан бечёвкой крест-накрест, как посылка на почте", "tied with string, criss-cross, like a post-office parcel"),
            "newspaper" to t("завёрнут в газету «Вечерняя Москва»", "wrapped in an evening newspaper"),
            "paper" to t("завёрнут в тетрадный лист в клетку", "wrapped in a squared exercise-book page"),
            "notes" to t("завёрнут в нотную бумагу", "wrapped in manuscript paper"),
            "fabric" to t("обёрнут лоскутом ситца в цветочек", "wrapped in a scrap of flowery cotton"),
            "map" to t("завёрнут в старую карту города", "wrapped in an old city map"),
            "bandage" to t("перевязан бинтом вместо ленточки", "tied with a bandage instead of a ribbon")),
        Attr.STEPS to mapOf(
            "valenki" to t("огромные валенки, размер сорок четвёртый, не меньше", "huge felt boots, size 44 at least"),
            "kidboots" to t("маленькие детские сапожки", "small children's boots"),
            "heels" to t("женские сапоги на каблуке", "women's heeled boots"),
            "sneakers" to t("кроссовки, совсем не по погоде", "trainers, not at all for the weather"),
            "shoes" to t("мужские ботинки с узкими носами", "men's shoes with pointed toes"),
            "cane" to t("короткие шаги, а рядом ямки от палочки", "short steps, with little holes from a walking stick")),
        Attr.TIME to mapOf(
            "night" to t("около трёх ночи", "around three in the morning"),
            "dawn" to t("около шести утра", "around six in the morning"),
            "evening" to t("около одиннадцати вечера", "around eleven at night"),
            "early" to t("около пяти вечера, ещё засветло", "around five in the evening, still light")),
        Attr.GAIT to mapOf(
            "limp" to t("прихрамывая", "with a limp"),
            "skip" to t("вприпрыжку", "skipping"),
            "quick" to t("быстро, почти бегом", "fast, almost running"),
            "slow" to t("медленно, то и дело останавливаясь", "slowly, stopping every few steps"),
            "hum" to t("не спеша и напевая что-то под нос", "unhurried, humming something")),
        Attr.SMELL to mapOf(
            "wax" to t("сургучом", "of sealing wax"),
            "chalk" to t("мелом", "of chalk"),
            "tangerine" to t("мандаринами", "of tangerines"),
            "tar" to t("морозом и смолой", "of frost and pine tar"),
            "coffee" to t("крепким кофе", "of strong coffee"),
            "rosin" to t("канифолью", "of rosin"),
            "iodine" to t("больницей: йодом и спиртом", "of hospital: iodine and spirit"),
            "oil" to t("машинным маслом", "of machine oil"),
            "petrol" to t("бензином", "of petrol"),
            "perfume" to t("духами «Красная Москва»", "of “Red Moscow” perfume")),
    )

    /** How a habit sounds in the suspect's file. */
    val habit: Map<Attr, Map<String, Tx>> = mapOf(
        Attr.HAND to mapOf(
            "slant" to t("Пишет размашисто, с наклоном вправо.", "Writes in big strokes, slanting right."),
            "print" to t("Пишет только печатными буквами.", "Writes only in block capitals."),
            "round" to t("Почерк круглый, как в прописях.", "Round copybook handwriting."),
            "quick" to t("Пишет торопливо, буквы скачут.", "Writes in a hurry, the letters jump."),
            "curly" to t("Пишет с завитушками.", "Writes with curls and flourishes.")),
        Attr.WRAP to mapOf(
            "string" to t("Свёртки по старой привычке вяжет бечёвкой крест-накрест.", "Out of old habit, ties parcels with string, criss-cross."),
            "newspaper" to t("Всё заворачивает в газеты.", "Wraps everything in newspaper."),
            "paper" to t("Дома горы тетрадей в клетку.", "Has piles of squared exercise books at home."),
            "notes" to t("В квартире кипы нотной бумаги.", "Has stacks of manuscript paper in the flat."),
            "fabric" to t("Дома мешок цветастых лоскутов.", "Keeps a bag of flowery fabric scraps."),
            "map" to t("В бардачке стопка старых карт города.", "Keeps a stack of old city maps in the glovebox."),
            "bandage" to t("В кармане всегда лежит бинт.", "Always has a bandage in a pocket.")),
        Attr.STEPS to mapOf(
            "valenki" to t("Зимой ходит в валенках сорок четвёртого размера.", "Wears size 44 felt boots in winter."),
            "kidboots" to t("Носит детские сапожки, тридцать первый размер.", "Wears children's boots, size 31."),
            "heels" to t("Ходит в сапогах на каблуке.", "Wears heeled boots."),
            "sneakers" to t("Даже зимой ходит в кроссовках.", "Wears trainers even in winter."),
            "shoes" to t("Носит ботинки с узкими носами.", "Wears shoes with pointed toes."),
            "cane" to t("Ходит с палочкой.", "Walks with a stick.")),
        Attr.TIME to mapOf(
            "night" to t("Не спит до трёх ночи.", "Is awake till three in the morning."),
            "dawn" to t("К шести утра уже на ногах (или только домой).", "Is up by six in the morning (or just getting home)."),
            "evening" to t("Возвращается домой к одиннадцати вечера.", "Gets home around eleven at night."),
            "early" to t("Рано ложится, по делам ходит засветло.", "Goes to bed early, runs errands in daylight.")),
        Attr.GAIT to mapOf(
            "limp" to t("Прихрамывает.", "Walks with a limp."),
            "skip" to t("Везде скачет вприпрыжку.", "Skips everywhere."),
            "quick" to t("Всегда бегом.", "Always in a rush."),
            "slow" to t("Ходит медленно, с остановками.", "Walks slowly, with stops."),
            "hum" to t("Ходит не спеша и всё время напевает.", "Walks unhurried, always humming.")),
        Attr.SMELL to mapOf(
            "wax" to t("В квартире пахнет сургучом.", "The flat smells of sealing wax."),
            "chalk" to t("В квартире пахнет мелом.", "The flat smells of chalk."),
            "tangerine" to t("В квартире пахнет мандаринами.", "The flat smells of tangerines."),
            "tar" to t("В квартире пахнет морозом и смолой.", "The flat smells of frost and pine tar."),
            "coffee" to t("В квартире пахнет крепким кофе.", "The flat smells of strong coffee."),
            "rosin" to t("В квартире пахнет канифолью.", "The flat smells of rosin."),
            "iodine" to t("В квартире пахнет йодом и спиртом.", "The flat smells of iodine and spirit."),
            "oil" to t("В квартире пахнет машинным маслом.", "The flat smells of machine oil."),
            "petrol" to t("В квартире пахнет бензином.", "The flat smells of petrol."),
            "perfume" to t("В квартире пахнет духами «Красная Москва».", "The flat smells of “Red Moscow” perfume.")),
    )

    /** A vague yes/no about one habit: whether the culprit's value is in [set]. */
    class Feature(val key: String, val attr: Attr, val set: Set<String>, val yes: Tx, val no: Tx?)

    private fun f(key: String, attr: Attr, set: Set<String>, yes: Tx, no: Tx?) = Feature(key, attr, set, yes, no)

    val features = listOf(
        f("neat", Attr.HAND, setOf("round", "curly"), t("аккуратный, буква к букве", "neat, every letter in place"), t("корявый, будто писали на коленке", "scrawled, as if written on a knee")),
        f("slant", Attr.HAND, setOf("slant", "quick"), t("с заметным наклоном вправо", "slanting noticeably to the right"), t("прямой, без всякого наклона", "upright, no slant at all")),
        f("ink", Attr.HAND, setOf("slant", "print", "round", "quick", "curly"), t("обычный, шариковой ручкой, ничего особенного", "ordinary, ballpoint pen, nothing special"), null),
        f("joined", Attr.HAND, setOf("slant", "round", "quick", "curly"), t("взрослый, буквы соединены", "grown-up, the letters joined"), t("детский: каждая буква отдельно, печатными", "childish: every letter separate, in capitals")),
        f("paper", Attr.WRAP, setOf("newspaper", "paper", "notes", "map", "string"), t("завёрнут в бумагу", "wrapped in paper"), t("безо всякой бумаги", "not wrapped in paper at all")),
        f("tied", Attr.WRAP, setOf("string", "bandage", "fabric"), t("туго перевязан", "tied up tight"), t("ничем не перевязан, просто завёрнут", "not tied, just wrapped")),
        f("home", Attr.WRAP, setOf("string", "newspaper", "paper", "notes", "fabric", "map", "bandage"), t("упакован вручную, явно не в магазине", "wrapped by hand, clearly not in a shop"), null),
        f("nonews", Attr.WRAP, setOf("string", "paper", "notes", "fabric", "map", "bandage"), t("упакован аккуратно, точно не в газету", "wrapped neatly, certainly not in newspaper"), t("завёрнут в газету", "wrapped in newspaper")),
        f("big", Attr.STEPS, setOf("valenki", "shoes"), t("крупные, мужского размера", "big, a man's size"), t("небольшие", "small")),
        f("winter", Attr.STEPS, setOf("valenki", "kidboots", "heels", "cane"), t("от тёплой зимней обуви", "from warm winter boots"), t("от лёгкой обуви, совсем не по погоде", "from light shoes, not for the weather at all")),
        f("pair", Attr.STEPS, setOf("valenki", "kidboots", "heels", "sneakers", "shoes", "cane"), t("одной и той же пары, туда и обратно", "the same pair, there and back"), null),
        f("adult", Attr.STEPS, setOf("valenki", "heels", "sneakers", "shoes", "cane"), t("взрослые", "a grown-up's"), t("совсем маленькие, детские", "tiny, a child's")),
        f("dark", Attr.TIME, setOf("night", "dawn", "evening"), t("Было уже темно", "It was already dark"), t("Было ещё светло", "It was still light")),
        f("late", Attr.TIME, setOf("night", "dawn"), t("Было глубоко за полночь", "It was well past midnight"), t("Было ещё до полуночи", "It was before midnight")),
        f("quiet", Attr.TIME, setOf("night", "dawn", "evening", "early"), t("Двор был пуст, все сидели по квартирам", "The yard was empty, everyone was indoors"), null),
        f("fast", Attr.GAIT, setOf("skip", "quick"), t("быстро", "quickly"), t("не торопясь", "unhurriedly")),
        f("heavy", Attr.GAIT, setOf("limp", "slow"), t("тяжело, будто каждый шаг даётся с трудом", "heavily, as if every step were hard work"), t("легко", "lightly")),
        f("alone", Attr.GAIT, setOf("limp", "skip", "quick", "slow", "hum"), t("одна, ни с кем не здороваясь", "alone, greeting no one"), null),
        f("steady", Attr.GAIT, setOf("skip", "quick", "slow", "hum"), t("ровно, не хромая", "evenly, without a limp"), t("прихрамывая", "with a limp")),
        f("sweet", Attr.SMELL, setOf("tangerine", "perfume", "coffee", "rosin"), t("чем-то приятным, сладковатым", "of something pleasant and sweetish"), t("чем-то резким", "of something sharp")),
        f("tech", Attr.SMELL, setOf("petrol", "oil", "iodine"), t("чем-то техническим, как в мастерской или больнице", "of something technical, like a workshop or a hospital"), t("вполне по-домашнему", "of home, nothing unusual")),
        f("faint", Attr.SMELL, setOf("wax", "chalk", "tangerine", "tar", "coffee", "rosin", "iodine", "oil", "petrol", "perfume"), t("чем-то едва уловимым", "of something faint"), null),
    )

    private fun exact(attr: Attr, v: String, ru: Boolean): String {
        val p = phrase.getValue(attr).getValue(v)(ru)
        return if (attr == Attr.TIME) (if (ru) "Было $p" else "It was $p") else p
    }

    /** Which habit each window speaks about, and in what words. Window 22 gives an alibi instead. */
    class Slot(val attr: Attr?, val ru: (String) -> String, val en: (String) -> String)

    val slots: Map<Int, Slot> = mapOf(
        1 to Slot(Attr.HAND, { "На подоконнике открытка без подписи: «Вы не одна». Почерк $it." }, { "On the windowsill, an unsigned card: “You are not alone”. The handwriting is $it." }),
        2 to Slot(Attr.WRAP, { "У Гоши новые варежки. «Под дверью нашёл, честно». Свёрток был $it." }, { "Gosha has new mittens. “Found them outside the door, honest”. The parcel was $it." }),
        3 to Slot(Attr.GAIT, { "Аркадий видел ночью во дворе фигуру с мешком. Шла $it." }, { "Arkady saw a figure with a sack in the yard at night. It walked $it." }),
        4 to Slot(Attr.HAND, { "У Сони на двери пакетик кофе и записка «Сдашь». Почерк $it." }, { "On Sonya's door: a bag of coffee and a note, “You'll pass”. The handwriting is $it." }),
        5 to Slot(Attr.STEPS, { "Близнецы клянутся, что видели у двери следы. Говорят, $it." }, { "The twins swear they saw footprints by the door. They say: $it." }),
        6 to Slot(Attr.WRAP, { "Вера Павловна нашла у двери тёплые варежки. Свёрток был $it." }, { "Vera Pavlovna found warm mittens at her door. The parcel was $it." }),
        7 to Slot(Attr.STEPS, { "Рустам утром подметал и видел у всех дверей одни и те же следы: $it." }, { "Sweeping this morning, Rustam saw the same footprints at every door: $it." }),
        8 to Slot(Attr.SMELL, { "Бублик вернулся с запиской за ошейником: «Нашёлся у подъезда». Бумажка пахнет $it." }, { "Bagel came home with a note in his collar: “Found by the entrance”. The paper smells $it." }),
        9 to Slot(Attr.TIME, { "Лев Маркович не спал и слышал, как кто-то тихо поднимался по лестнице. $it." }, { "Lev Markovich lay awake and heard someone creep up the stairs. $it." }),
        10 to Slot(Attr.TIME, { "Зоя видела, как кто-то оставил свёрток у почтовых ящиков. $it." }, { "Zoya saw someone leave a parcel by the letterboxes. $it." }),
        11 to Slot(Attr.WRAP, { "Под ёлкой у Кирилла и Ани свёрток без подписи. Он был $it." }, { "Under Kirill and Anya's tree, an unsigned parcel. It was $it." }),
        12 to Slot(Attr.TIME, { "В журнале наблюдений за рыбками запись: «Скрипнула дверь подъезда». $it." }, { "In the fish-watching log: “The front door creaked”. $it." }),
        13 to Slot(Attr.GAIT, { "Света укачивала малыша у окна и видела во дворе фигуру со свёртком. Шла $it." }, { "Rocking the baby by the window, Sveta saw a figure with a parcel in the yard. It walked $it." }),
        14 to Slot(Attr.HAND, { "Ольге подбросили открытку «Спасибо за снежинок». Почерк $it." }, { "Someone left Olga a card: “Thank you for the snowflakes”. The handwriting is $it." }),
        15 to Slot(Attr.STEPS, { "На свежем катке остались следы к подъезду: $it." }, { "On the fresh ice there are footprints to the entrance: $it." }),
        16 to Slot(Attr.HAND, { "Нина сравнила открытку со старыми письмами из шкатулки. Почерк $it." }, { "Nina compared the card with old letters from her box. The handwriting is $it." }),
        17 to Slot(Attr.TIME, { "Гена успел зарисовать во дворе фигуру со свёртком. На эскизе пометка: «$it»." }, { "Gena managed to sketch a figure with a parcel in the yard. A note on the sketch: “$it”." }),
        18 to Slot(Attr.SMELL, { "Ахмедовым подарили банку мёда. Крышка пахнет $it." }, { "Someone gave the Akhmedovs a jar of honey. The lid smells $it." }),
        19 to Slot(Attr.GAIT, { "Камера Инны сняла размытую фигуру. По походке видно: шла $it." }, { "Inna's camera caught a blurry figure. You can tell from the walk: it went $it." }),
        20 to Slot(Attr.SMELL, { "Савва говорит: «Нос не обманешь. Подарок пах $it»." }, { "Savva says: “You can't fool the nose. The present smelled $it”." }),
        21 to Slot(Attr.STEPS, { "Лена разглядела утром следы на ступенях: $it." }, { "In the morning Lena studied the footprints on the steps: $it." }),
        22 to Slot(null, { it }, { it }),
        23 to Slot(Attr.GAIT, { "Маша пишет Деду Морозу: «Я видела вас из окна! Вы шли $it…»" }, { "Masha writes to Grandfather Frost: “I saw you from my window! You were walking $it…”" }),
    )

    private val nervous = Slot(Attr.SMELL, { "Здесь уверяют, что ничего не видели, и быстро меняют тему. А в прихожей почему-то пахнет $it." },
        { "They swear they saw nothing here and quickly change the subject. Yet the hallway smells $it." })

    /** What the director chose for one window: a feature, the exact habit, or an alibi. */
    data class Pick(val kind: String, val attr: Attr? = null, val feature: String? = null, val who: String? = null) {
        fun encode() = listOf(kind, attr?.name ?: "", feature ?: "", who ?: "").joinToString(":")

        companion object {
            fun decode(s: String): Pick {
                val p = s.split(":")
                return Pick(p[0], p.getOrNull(1)?.takeIf { it.isNotEmpty() }?.let(Attr::valueOf), p.getOrNull(2)?.takeIf { it.isNotEmpty() }, p.getOrNull(3)?.takeIf { it.isNotEmpty() })
            }
        }
    }

    /** One clue as the player reads it, and which suspects it still allows. */
    class Clue(val day: Int, val attr: Attr?, val alibiOf: String?, val text: Tx, val allows: (Suspect) -> Boolean)

    private fun targetLeft(d: Int) = when {
        d <= 10 -> 4
        d <= 15 -> 3
        d <= 20 -> 2
        else -> 1
    }

    private fun hostOf(day: Int) = suspects.firstOrNull { it.day == day }

    private fun test(p: Pick, c: Suspect): (Suspect) -> Boolean = when (p.kind) {
        "alibi" -> { s -> s.id != p.who }
        "exact" -> { s -> s.traits[p.attr] == c.traits[p.attr] }
        else -> {
            val f = features.first { it.key == p.feature }
            val ok = c.traits.getValue(f.attr) in f.set
            ({ s -> (s.traits.getValue(f.attr) in f.set) == ok })
        }
    }

    /** Plans the season's 23 clues so the culprit can be named only near the end. */
    fun plan(culprit: String, random: Random): Map<Int, Pick> {
        val c = suspect(culprit)
        var left = suspects
        val out = mutableMapOf<Int, Pick>()
        for (d in 1..23) {
            val slot = slots.getValue(d)
            val options = if (slot.attr == null) {
                suspects.filter { it.id != c.id && it.alibi != null }.map { Pick("alibi", who = it.id) }
            } else {
                val attr = if (hostOf(d)?.id == c.id) Attr.SMELL else slot.attr
                features.filter { it.attr == attr }.map { Pick("feat", attr, it.key) } + Pick("exact", attr)
            }
            val counted = options.map { it to left.count(test(it, c)) }
            val target = targetLeft(d)
            val chosen = if (d >= 21) {
                val m = counted.minOf { it.second }
                counted.filter { it.second == m }
            } else {
                val good = counted.filter { it.second >= target }
                if (good.isNotEmpty()) good.filter { it.second == good.minOf { g -> g.second } }
                else counted.filter { it.second == counted.maxOf { m -> m.second } }
            }
            val pick = chosen[random.nextInt(chosen.size)].first
            out[d] = pick
            left = left.filter(test(pick, c))
        }
        return out
    }

    fun clue(day: Int, culprit: String, plan: Map<Int, Pick>): Clue? {
        val slot = slots[day] ?: return null
        val pick = plan[day] ?: return null
        val c = suspect(culprit)
        val words = if (hostOf(day)?.id == c.id && slot.attr != null) nervous else slot
        if (pick.kind == "alibi") {
            val a = suspect(pick.who!!)
            return Clue(day, null, a.id, t("Пётр Ильич знает всех в доме: ${a.alibi!!.ru}", "Pyotr Ilyich knows everyone in the building: ${a.alibi!!.en}"), test(pick, c))
        }
        val attr = pick.attr!!
        val value: (Boolean) -> String = if (pick.kind == "exact") { ru -> exact(attr, c.traits.getValue(attr), ru) } else {
            val f = features.first { it.key == pick.feature }
            val ok = c.traits.getValue(attr) in f.set
            ({ ru -> (if (ok) f.yes else f.no!!)(ru) })
        }
        return Clue(day, attr, null, Tx(words.ru(value(true)), words.en(value(false))), test(pick, c))
    }

    fun encode(plan: Map<Int, Pick>) = plan.entries.sortedBy { it.key }.joinToString(";") { "${it.key}=${it.value.encode()}" }
    fun decode(s: String): Map<Int, Pick> = s.split(";").filter { it.contains("=") }.associate { e -> e.substringBefore("=").toInt() to Pick.decode(e.substringAfter("=")) }
}
