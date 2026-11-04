"""Draws the launcher icon — a dark house at dusk with one warm window — as vector drawables.

python tools/icon.py

Shapes live in the 108×108 adaptive-icon space (safe zone 66 in the middle).
"""
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DRAW = ROOT / "app/src/main/res/drawable"
MIP = ROOT / "app/src/main/res/mipmap-anydpi-v26"

SKY = "#0F1A33"
HOUSE = "#3B4A6E"
DARK = "#16203B"
FRAME = "#E8E8EE"
WARM = "#FFCB6B"
WARM_D = "#F29B3B"
SNOW = "#EEF3FA"


def rect(x, y, w, h):
    return f"M{x},{y}h{w}v{h}h{-w}z"


def window(x, y, lit):
    shapes = [(rect(x - 1, y - 1, 12, 14), FRAME)]
    if lit:
        shapes += [(rect(x, y, 10, 12), WARM), (rect(x, y + 7, 10, 5), WARM_D), (f"M{x},{y}h3q-1,6 1,12h-4z M{x + 10},{y}h-3q1,6 -1,12h4z", "#C8483C")]
    else:
        shapes += [(rect(x, y, 10, 12), DARK)]
    shapes += [(rect(x + 4.6, y, .8, 12) + rect(x, y + 4, 10, .8), FRAME)]
    return shapes


FG = [(rect(30, 32, 48, 50), HOUSE), (rect(28, 29, 52, 4), SNOW)]
for row, y in enumerate([38, 54, 70]):
    for col, x in enumerate([35, 49, 63]):
        if row == 2 and col == 1:
            FG.append((rect(x, y, 10, 12), DARK))
            continue
        FG += window(x, y, row == 1 and col == 1)
FG.append(("M54,47 m-7,0 a7,7 0 1,0 14,0 a7,7 0 1,0 -14,0", "#33FFCB6B"))


def vector(shapes, w=108):
    paths = "\n".join(f'    <path android:fillColor="{c}" android:pathData="{d}" />' for d, c in shapes)
    return f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{w}dp" android:height="{w}dp" android:viewportWidth="{w}" android:viewportHeight="{w}">
{paths}
</vector>
'''


def main():
    DRAW.mkdir(parents=True, exist_ok=True)
    MIP.mkdir(parents=True, exist_ok=True)
    stars = [(20, 22), (86, 18), (76, 30), (24, 44), (90, 52), (16, 70)]
    bg = [(rect(0, 0, 108, 108), SKY)] + [(f"M{x},{y}m-.8,0a.8,.8 0 1,0 1.6,0a.8,.8 0 1,0 -1.6,0", "#CCFFFFFF") for x, y in stars]
    bg.append(("M0,86 Q30,80 54,87 T108,84 V108 H0z", "#C9D4EA"))
    (DRAW / "ic_launcher_background.xml").write_text(vector(bg), encoding="utf-8")
    (DRAW / "ic_launcher_foreground.xml").write_text(vector(FG), encoding="utf-8")
    mono = [(rect(30, 32, 48, 50), "#FFFFFFFF")]
    (DRAW / "ic_launcher_monochrome.xml").write_text(vector(mono + [(rect(49, 54, 10, 12), "#FF000000")]), encoding="utf-8")
    notify = [(rect(4, 3, 16, 18) + "M6,5v6.2h5.2V5z M12.8,5v6.2H18V5z M6,12.8V19h5.2v-6.2z M12.8,12.8V19H18v-6.2z", "#FFFFFFFF")]
    (DRAW / "ic_notify.xml").write_text(vector([("M4,3h16v18h-16z M11.2,3v18h1.6V3z M4,11.2h16v1.6H4z", "#FFFFFFFF")], 24).replace('android:fillColor="#FFFFFFFF"', 'android:fillColor="#FFFFFFFF" android:fillType="evenOdd"'), encoding="utf-8")
    (MIP / "ic_launcher.xml").write_text('''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
''', encoding="utf-8")
    print("icon written")


if __name__ == "__main__":
    main()
