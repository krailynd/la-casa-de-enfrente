#!/usr/bin/env python3
"""
tools/art_pipeline/generate_flora.py
Generación de la Colección Botánica (Rosas Rojas, Tulipanes y Adornos Florales):
- rose_bloom_sheet.png (8 frames de florecimiento de capullo a rosa abierta)
- tulip_bouquet.png (ramo de tulipanes en alta densidad para cartas y música)
- floral_divider.png (divisor ornamental con filigrana dorada y rosas)
- corner_roses.png (adorno para esquinas de cartas y modales)
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter

OUTPUT_DIR = "app/src/main/assets/sprites"
DRAWABLE_DIR = "app/src/main/res/drawable-nodpi"
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(DRAWABLE_DIR, exist_ok=True)

WINE_OUTLINE = (43, 10, 20, 255)       # #2B0A14
ROSE_DEEP = (110, 7, 24, 255)          # #6E0718
ROSE_VELVET = (217, 30, 54, 255)       # #D91E36
ROSE_LIGHT = (255, 122, 143, 255)      # #FF7A8F
LEAF_DARK = (22, 62, 46, 255)          # #163E2E
LEAF_LIGHT = (38, 120, 78, 255)        # #26784E
GOLD_FILIGREE = (246, 198, 103, 255)   # #F6C667
GOLD_BRIGHT = (255, 225, 168, 255)     # #FFE1A8

def generate_rose_bloom():
    """
    8 frames de florecimiento de rosa roja carmesí (64x64 px).
    De capullo cerrado con sépalos verdes hasta rosa exuberante en capas Bézier.
    """
    FRAME_W, FRAME_H = 64, 64
    FRAMES = 8
    sheet = Image.new("RGBA", (FRAME_W * FRAMES, FRAME_H), (0, 0, 0, 0))

    for f in range(FRAMES):
        frame = Image.new("RGBA", (FRAME_W, FRAME_H), (0, 0, 0, 0))
        draw = ImageDraw.Draw(frame)

        progress = f / (FRAMES - 1)  # 0.0 a 1.0

        # Tallo y hojas
        stem_pts = [(32, 38), (32, 58)]
        draw.line(stem_pts, fill=LEAF_DARK, width=3)
        draw.line(stem_pts, fill=LEAF_LIGHT, width=2)

        # Hojas a los lados
        leaf_spread = 6 + int(progress * 14)
        draw.ellipse([32 - leaf_spread, 44, 32 - 2, 52], fill=LEAF_DARK, outline=WINE_OUTLINE, width=1)
        draw.ellipse([32 + 2, 46, 32 + leaf_spread, 54], fill=LEAF_DARK, outline=WINE_OUTLINE, width=1)

        # Pétalos de rosa floreciendo en espiral
        cx, cy = 32, 30
        max_r = 6 + int(progress * 16)

        # Capa exterior de pétalos
        if progress > 0.3:
            num_petals = 6
            for p in range(num_petals):
                angle = p * (2 * math.pi / num_petals) + progress * 0.5
                px = cx + math.cos(angle) * (max_r * 0.75)
                py = cy + math.sin(angle) * (max_r * 0.6)
                pw = int(max_r * 0.7)
                draw.ellipse([px - pw, py - pw, px + pw, py + pw], fill=ROSE_DEEP, outline=WINE_OUTLINE, width=1)
                draw.ellipse([px - pw + 1, py - pw + 1, px + pw - 1, py + pw - 1], fill=ROSE_VELVET)

        # Capa media de pétalos
        mid_r = 4 + int(progress * 10)
        for p in range(5):
            angle = p * (2 * math.pi / 5) - progress * 0.8
            px = cx + math.cos(angle) * (mid_r * 0.5)
            py = cy + math.sin(angle) * (mid_r * 0.4)
            pw = int(mid_r * 0.6)
            draw.ellipse([px - pw, py - pw, px + pw, py + pw], fill=ROSE_VELVET)
            draw.ellipse([px - pw + 1, py - pw + 1, px + pw - 2, py + pw - 2], fill=ROSE_LIGHT)

        # Centro capullo
        core_r = 3 + int(progress * 4)
        draw.ellipse([cx - core_r, cy - core_r, cx + core_r, cy + core_r], fill=ROSE_DEEP)
        draw.ellipse([cx - core_r + 1, cy - core_r + 1, cx + core_r - 1, cy + core_r - 1], fill=ROSE_VELVET)

        sheet.paste(frame, (f * FRAME_W, 0))

    sheet.save(os.path.join(OUTPUT_DIR, "rose_bloom_sheet.png"))
    print("✓ Generado rose_bloom_sheet.png")

def generate_tulip_bouquet():
    """
    Ramo de tulipanes en alta resolución (128x128 px).
    Tulipanes carmesí, frambuesa y rosa pastel atados con lazo dorado.
    """
    IMG_W, IMG_H = 128, 128
    img = Image.new("RGBA", (IMG_W, IMG_H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Tallos elegantes
    stems = [
        [(58, 60), (62, 110)],
        [(64, 52), (64, 112)],
        [(70, 62), (66, 110)]
    ]
    for s in stems:
        draw.line(s, fill=LEAF_DARK, width=4)
        draw.line(s, fill=LEAF_LIGHT, width=2)

    # Hojas largas estilizadas
    draw.polygon([(64, 100), (38, 70), (48, 64), (64, 90)], fill=LEAF_DARK, outline=WINE_OUTLINE)
    draw.polygon([(64, 100), (90, 72), (80, 66), (64, 90)], fill=LEAF_DARK, outline=WINE_OUTLINE)

    # 3 Tulipanes principales
    # 1. Tulipán izquierdo (Frambuesa)
    tx, ty = 46, 52
    draw.ellipse([tx - 12, ty - 18, tx + 12, ty + 14], fill=ROSE_DEEP, outline=WINE_OUTLINE, width=1)
    draw.ellipse([tx - 10, ty - 16, tx + 6, ty + 12], fill=(232, 50, 85, 255))
    draw.ellipse([tx - 4, ty - 16, tx + 10, ty + 12], fill=ROSE_LIGHT)

    # 2. Tulipán derecho (Rosa pastel)
    tx, ty = 82, 54
    draw.ellipse([tx - 12, ty - 18, tx + 12, ty + 14], fill=ROSE_DEEP, outline=WINE_OUTLINE, width=1)
    draw.ellipse([tx - 10, ty - 16, tx + 6, ty + 12], fill=(245, 140, 165, 255))
    draw.ellipse([tx - 4, ty - 16, tx + 10, ty + 12], fill=(255, 195, 210, 255))

    # 3. Tulipán central alto (Rojo Carmesí)
    tx, ty = 64, 40
    draw.ellipse([tx - 15, ty - 22, tx + 15, ty + 16], fill=ROSE_DEEP, outline=WINE_OUTLINE, width=1)
    draw.ellipse([tx - 13, ty - 20, tx + 8, ty + 14], fill=ROSE_VELVET)
    draw.ellipse([tx - 6, ty - 20, tx + 13, ty + 14], fill=(255, 60, 90, 255))
    draw.ellipse([tx - 4, ty - 14, tx + 4, ty + 8], fill=ROSE_LIGHT)

    # Lazo dorado de seda
    draw.ellipse([54, 96, 74, 106], fill=GOLD_FILIGREE, outline=WINE_OUTLINE, width=1)
    draw.polygon([(64, 102), (52, 118), (58, 120), (64, 104)], fill=GOLD_BRIGHT, outline=WINE_OUTLINE)
    draw.polygon([(64, 102), (76, 118), (70, 120), (64, 104)], fill=GOLD_BRIGHT, outline=WINE_OUTLINE)

    img.save(os.path.join(DRAWABLE_DIR, "tulip_bouquet.png"))
    print("✓ Generado tulip_bouquet.png")

def generate_floral_divider():
    """
    Divisor ornamental dorado con rosas rojas y hojas (320x32 px).
    """
    W, H = 320, 32
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    cy = 16
    # Línea central dorada con degradado en los extremos
    draw.line([(30, cy), (290, cy)], fill=GOLD_FILIGREE, width=1)
    draw.line([(60, cy), (260, cy)], fill=GOLD_BRIGHT, width=2)

    # Diamante central
    draw.polygon([(160, cy - 8), (168, cy), (160, cy + 8), (152, cy)], fill=GOLD_BRIGHT, outline=WINE_OUTLINE)

    # Rosita roja en el centro
    draw.ellipse([156, cy - 4, 164, cy + 4], fill=ROSE_VELVET, outline=WINE_OUTLINE)
    draw.ellipse([158, cy - 2, 162, cy + 2], fill=ROSE_LIGHT)

    # Hojitas laterales
    draw.ellipse([142, cy - 3, 150, cy + 3], fill=LEAF_LIGHT)
    draw.ellipse([170, cy - 3, 178, cy + 3], fill=LEAF_LIGHT)

    # Puntos terminales decorativos
    draw.ellipse([26, cy - 3, 32, cy + 3], fill=GOLD_BRIGHT)
    draw.ellipse([288, cy - 3, 294, cy + 3], fill=GOLD_BRIGHT)

    img.save(os.path.join(DRAWABLE_DIR, "floral_divider.png"))
    print("✓ Generado floral_divider.png")

if __name__ == "__main__":
    generate_rose_bloom()
    generate_tulip_bouquet()
    generate_floral_divider()
    print("¡Colección botánica generada exitosamente!")
