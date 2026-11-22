#!/usr/bin/env python3
"""
tools/art_pipeline/generate_characters.py
Modelado y exportación de Spritesheets 2D con Cel-Shading y Tinted Outlines:
- rata_aspirable_sheet.png (mascota oficial de Elvis: idle, walk, happy_jump, serenade)
- principessa_sheet.png (Francesca: idle, holding_tulips, smile_blush)
- street_life_sheets.png (coche vintage rojo, gatito, perrito, pareja, pajarito)
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter
import numpy as np

OUTPUT_DIR = "app/src/main/assets/sprites"
DRAWABLE_DIR = "app/src/main/res/drawable-nodpi"
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(DRAWABLE_DIR, exist_ok=True)

# Paleta Estándar Storybook
WINE_OUTLINE = (43, 10, 20, 255)       # #2B0A14
CRIMSON_BASE = (184, 20, 44, 255)      # #B8142C
CRIMSON_SHADOW = (110, 7, 24, 255)     # #6E0718
GOLD_ACCENT = (255, 215, 0, 255)       # #FFD700
GOLD_RIM = (255, 225, 168, 255)        # #FFE1A8
WARM_WHITE = (255, 248, 238, 255)      # #FFF8EE
CHEEK_BLUSH = (232, 93, 117, 180)      # #E85D75
SHADOW_CONTACT = (26, 8, 16, 80)       # #1A0810 at 30% alpha

def draw_oval(draw, bbox, fill, outline=None, width=1):
    draw.ellipse(bbox, fill=fill, outline=outline, width=width)

def draw_rounded_rect(draw, bbox, radius, fill, outline=None, width=1):
    draw.rounded_rectangle(bbox, radius=radius, fill=fill, outline=outline, width=width)

def generate_rata_aspirable():
    """
    Rata Aspirable: Ratita tierna gris perla cálido con barriguita crema,
    orejas rosadas, bufanda larga rojo carmesí ondeando y mini guitarra acústica.
    Grid: 4 filas x 6 columnas, 64x64 px por frame.
    """
    FRAME_W, FRAME_H = 64, 64
    COLS, ROWS = 6, 4
    sheet = Image.new("RGBA", (FRAME_W * COLS, FRAME_H * ROWS), (0, 0, 0, 0))

    PEARL_GRAY = (205, 200, 205, 255)
    PEARL_SHADOW = (165, 158, 168, 255)
    BELLY_CREAM = (248, 242, 235, 255)
    EAR_PINK = (245, 180, 195, 255)
    GUITAR_WOOD = (195, 115, 55, 255)
    GUITAR_DARK = (130, 65, 25, 255)

    for row in range(ROWS):
        for col in range(COLS):
            frame = Image.new("RGBA", (FRAME_W, FRAME_H), (0, 0, 0, 0))
            draw = ImageDraw.Draw(frame)

            t = col / COLS
            phase = t * 2.0 * math.pi

            # Parámetros según animación
            bounce_y = 0
            scarf_wave = math.sin(phase) * 4
            whisker_wiggle = math.sin(phase * 2) * 2
            heart_scale = 0
            strumming = False

            if row == 0:  # idle: respiración y bigotes
                bounce_y = int(math.sin(phase) * 1.5)
            elif row == 1:  # walk: pasitos y balanceo
                bounce_y = int(abs(math.sin(phase * 2)) * 3)
                scarf_wave = math.sin(phase * 2) * 6
            elif row == 2:  # happy_jump: salto elástico + corazón
                jump_progress = math.sin(t * math.pi)
                bounce_y = -int(jump_progress * 10)
                heart_scale = jump_progress
            elif row == 3:  # serenade: tocando guitarra + notas
                bounce_y = int(math.sin(phase) * 1.2)
                strumming = True

            # 1. Sombra de contacto en el suelo
            shadow_w = 26 - abs(bounce_y) * 0.8
            draw.ellipse([32 - shadow_w/2, 56, 32 + shadow_w/2, 60], fill=SHADOW_CONTACT)

            cy = 38 + bounce_y

            # 2. Cola larga ondulada
            tail_points = [
                (20, cy + 8),
                (14 + math.sin(phase) * 3, cy + 6),
                (8 + math.sin(phase + 1) * 4, cy - 2),
                (10 + math.sin(phase + 2) * 3, cy - 8)
            ]
            draw.line(tail_points, fill=PEARL_SHADOW, width=3)
            draw.line(tail_points, fill=PEARL_GRAY, width=2)

            # 3. Mini Guitarra en la espalda o frente
            if strumming:
                # Guitarra al frente
                draw.ellipse([30, cy + 4, 46, cy + 16], fill=GUITAR_WOOD, outline=WINE_OUTLINE, width=1)
                draw.ellipse([34, cy + 7, 39, cy + 12], fill=GUITAR_DARK)
                draw.line([(32, cy + 4), (46, cy - 8)], fill=GUITAR_WOOD, width=3)
            else:
                # Guitarra apoyada en la espalda
                draw.ellipse([14, cy + 2, 26, cy + 15], fill=GUITAR_WOOD, outline=WINE_OUTLINE, width=1)
                draw.line([(18, cy + 3), (12, cy - 7)], fill=GUITAR_WOOD, width=2)

            # 4. Cuerpo de la ratita (pera tierna)
            draw.ellipse([22, cy - 2, 42, cy + 16], fill=PEARL_SHADOW)
            draw.ellipse([23, cy - 2, 41, cy + 15], fill=PEARL_GRAY, outline=WINE_OUTLINE, width=1)
            # Barriguita crema
            draw.ellipse([27, cy + 2, 38, cy + 14], fill=BELLY_CREAM)

            # 5. Patitas
            draw.ellipse([22, cy + 14, 28, cy + 18], fill=EAR_PINK, outline=WINE_OUTLINE, width=1)
            draw.ellipse([34, cy + 14, 40, cy + 18], fill=EAR_PINK, outline=WINE_OUTLINE, width=1)

            # 6. Cabeza redondeada
            head_y = cy - 12
            draw.ellipse([23, head_y, 41, head_y + 16], fill=PEARL_GRAY, outline=WINE_OUTLINE, width=1)

            # 7. Orejitas grandes rosadas
            # Oreja izquierda
            draw.ellipse([18, head_y - 8, 28, head_y + 4], fill=PEARL_GRAY, outline=WINE_OUTLINE, width=1)
            draw.ellipse([20, head_y - 6, 26, head_y + 2], fill=EAR_PINK)
            # Oreja derecha
            draw.ellipse([36, head_y - 8, 46, head_y + 4], fill=PEARL_GRAY, outline=WINE_OUTLINE, width=1)
            draw.ellipse([38, head_y - 6, 44, head_y + 2], fill=EAR_PINK)

            # 8. Ojos expresivos y mejillas
            # Ojo izquierdo
            draw.ellipse([27, head_y + 5, 30, head_y + 9], fill=WINE_OUTLINE)
            draw.point((28, head_y + 6), fill=WARM_WHITE)
            # Ojo derecho
            draw.ellipse([34, head_y + 5, 37, head_y + 9], fill=WINE_OUTLINE)
            draw.point((35, head_y + 6), fill=WARM_WHITE)
            # Naricita rosada
            draw.polygon([(31, head_y + 10), (33, head_y + 10), (32, head_y + 12)], fill=EAR_PINK)
            # Bigotitos
            draw.line([(24, head_y + 10), (18, head_y + 9 + whisker_wiggle)], fill=WINE_OUTLINE, width=1)
            draw.line([(40, head_y + 10), (46, head_y + 9 - whisker_wiggle)], fill=WINE_OUTLINE, width=1)

            # 9. Bufanda larga roja carmesí ondeando
            draw.rounded_rectangle([21, cy - 3, 43, cy + 2], radius=3, fill=CRIMSON_BASE, outline=WINE_OUTLINE, width=1)
            # Extremo suelto de la bufanda
            scarf_pts = [
                (37, cy + 1),
                (45 + scarf_wave, cy + 6),
                (48 + scarf_wave * 1.2, cy + 16),
                (42 + scarf_wave * 0.8, cy + 15),
                (36, cy + 3)
            ]
            draw.polygon(scarf_pts, fill=CRIMSON_BASE, outline=WINE_OUTLINE)
            # Flecos dorados
            draw.line([(42 + scarf_wave*0.8, cy + 15), (48 + scarf_wave*1.2, cy + 16)], fill=GOLD_ACCENT, width=2)

            # 10. Efectos especiales (Corazones o notas musicales)
            if heart_scale > 0.1:
                hx, hy = 32, cy - 24 - int(heart_scale * 8)
                hs = int(heart_scale * 7)
                draw.polygon([
                    (hx, hy + hs),
                    (hx - hs, hy),
                    (hx - hs//2, hy - hs//2),
                    (hx, hy),
                    (hx + hs//2, hy - hs//2),
                    (hx + hs, hy)
                ], fill=CRIMSON_BASE, outline=GOLD_RIM)

            if strumming and col % 2 == 1:
                # Notas musicales flotando
                nx, ny = 48, cy - 10 - col * 2
                draw.ellipse([nx, ny, nx + 4, ny + 3], fill=GOLD_ACCENT)
                draw.line([(nx + 4, ny + 1), (nx + 4, ny - 6)], fill=GOLD_ACCENT, width=1)
                draw.line([(nx + 4, ny - 6), (nx + 7, ny - 5)], fill=GOLD_ACCENT, width=1)

            sheet.paste(frame, (col * FRAME_W, row * FRAME_H))

    sheet.save(os.path.join(OUTPUT_DIR, "rata_aspirable_sheet.png"))
    print("✓ Generado rata_aspirable_sheet.png")

def generate_principessa():
    """
    Francesca (Principessa): Chibi elegante con abrigo largo rojo rubí (#B8142C),
    cuello de peluche crema, bufanda, cabello sedoso oscuro y ramo de tulipanes.
    Grid: 3 filas x 6 columnas, 64x80 px por frame.
    """
    FRAME_W, FRAME_H = 64, 80
    COLS, ROWS = 6, 3
    sheet = Image.new("RGBA", (FRAME_W * COLS, FRAME_H * ROWS), (0, 0, 0, 0))

    SKIN_TONE = (255, 226, 212, 255)
    HAIR_DARK = (38, 22, 28, 255)
    HAIR_SHINE = (85, 52, 64, 255)
    RUBY_COAT = (184, 20, 44, 255)
    COAT_SHADOW = (110, 7, 24, 255)
    FUR_COLLAR = (255, 250, 242, 255)
    TULIP_RED = (220, 30, 60, 255)
    TULIP_PINK = (248, 140, 165, 255)
    LEAF_GREEN = (32, 95, 62, 255)

    for row in range(ROWS):
        for col in range(COLS):
            frame = Image.new("RGBA", (FRAME_W, FRAME_H), (0, 0, 0, 0))
            draw = ImageDraw.Draw(frame)

            t = col / COLS
            phase = t * 2.0 * math.pi
            wind = math.sin(phase) * 3

            cy = 44
            bounce_y = int(math.sin(phase) * 1.5) if row == 0 else int(abs(math.sin(phase * 2)) * 3)

            # Sombra en suelo
            draw.ellipse([32 - 14, 72, 32 + 14, 77], fill=SHADOW_CONTACT)

            # Cabello trasero
            hair_pts = [
                (20, 24 + bounce_y),
                (14 + wind, 52 + bounce_y),
                (32, 56 + bounce_y),
                (50 + wind, 52 + bounce_y),
                (44, 24 + bounce_y)
            ]
            draw.polygon(hair_pts, fill=HAIR_DARK)

            # Abrigo Rojo Rubí
            coat_top = 34 + bounce_y
            coat_pts = [
                (26, coat_top),
                (38, coat_top),
                (46, 68 + bounce_y),
                (18, 68 + bounce_y)
            ]
            draw.polygon(coat_pts, fill=COAT_SHADOW)
            draw.polygon([(26, coat_top), (38, coat_top), (43, 67 + bounce_y), (21, 67 + bounce_y)], fill=RUBY_COAT)
            draw.line(coat_pts, fill=WINE_OUTLINE, width=1)
            # Botoncitos dorados
            for by in range(coat_top + 10, 64 + bounce_y, 8):
                draw.ellipse([31, by, 33, by + 2], fill=GOLD_ACCENT)

            # Cuello de peluche crema
            draw.ellipse([23, coat_top - 4, 41, coat_top + 6], fill=FUR_COLLAR, outline=WINE_OUTLINE, width=1)

            # Cabeza y rostro
            head_y = 16 + bounce_y
            draw.ellipse([20, head_y, 44, head_y + 22], fill=SKIN_TONE, outline=WINE_OUTLINE, width=1)

            # Rubor en mejillas
            draw.ellipse([22, head_y + 13, 27, head_y + 17], fill=CHEEK_BLUSH)
            draw.ellipse([37, head_y + 13, 42, head_y + 17], fill=CHEEK_BLUSH)

            # Ojos grandes preciosos
            draw.ellipse([24, head_y + 8, 29, head_y + 14], fill=WINE_OUTLINE)
            draw.point((25, head_y + 9), fill=WARM_WHITE)
            draw.point((27, head_y + 12), fill=WARM_WHITE)

            draw.ellipse([35, head_y + 8, 40, head_y + 14], fill=WINE_OUTLINE)
            draw.point((36, head_y + 9), fill=WARM_WHITE)
            draw.point((38, head_y + 12), fill=WARM_WHITE)

            # Sonrisa tierna
            draw.arc([30, head_y + 13, 34, head_y + 17], start=0, end=180, fill=WINE_OUTLINE, width=1)

            # Flequillo sedoso
            bangs_pts = [
                (20, head_y + 6),
                (26, head_y + 9),
                (32, head_y + 5),
                (38, head_y + 9),
                (44, head_y + 6),
                (44, head_y),
                (20, head_y)
            ]
            draw.polygon(bangs_pts, fill=HAIR_DARK)
            draw.line([(24, head_y + 3), (30, head_y + 3)], fill=HAIR_SHINE, width=2)
            draw.line([(34, head_y + 3), (40, head_y + 3)], fill=HAIR_SHINE, width=2)

            # Fila 1: Sosteniendo ramo de tulipanes
            if row == 1:
                # Tallos
                draw.line([(32, coat_top + 14), (30, coat_top + 2)], fill=LEAF_GREEN, width=2)
                draw.line([(32, coat_top + 14), (34, coat_top + 2)], fill=LEAF_GREEN, width=2)
                # Tulipán rojo
                draw.ellipse([28, coat_top - 2, 33, coat_top + 5], fill=TULIP_RED, outline=WINE_OUTLINE, width=1)
                # Tulipán rosa
                draw.ellipse([33, coat_top - 1, 38, coat_top + 6], fill=TULIP_PINK, outline=WINE_OUTLINE, width=1)
                # Manitas con guantes blancos
                draw.ellipse([28, coat_top + 10, 36, coat_top + 16], fill=FUR_COLLAR, outline=WINE_OUTLINE, width=1)

            sheet.paste(frame, (col * FRAME_W, row * FRAME_H))

    sheet.save(os.path.join(OUTPUT_DIR, "principessa_sheet.png"))
    print("✓ Generado principessa_sheet.png")

def generate_street_life():
    """
    Entidades urbanas:
    - Coche vintage rojo con árbol de Navidad en el techo (128x64 px x 4 frames)
    - Gatito acurrucado / caminando (48x48 px x 4 frames)
    - Perrito trotando (48x48 px x 4 frames)
    - Pareja tomada de la mano (64x64 px x 4 frames)
    """
    # 1. Coche Vintage Rojo con Árbol
    CAR_W, CAR_H = 120, 60
    car_sheet = Image.new("RGBA", (CAR_W * 4, CAR_H), (0, 0, 0, 0))

    CHERRY_RED = (195, 25, 45, 255)
    CHERRY_SHADOW = (125, 12, 28, 255)
    CHROME_BUMPER = (240, 245, 250, 255)
    PINE_GREEN = (35, 95, 55, 255)
    HEADLIGHT_GLOW = (255, 235, 140, 220)

    for f in range(4):
        frame = Image.new("RGBA", (CAR_W, CAR_H), (0, 0, 0, 0))
        draw = ImageDraw.Draw(frame)
        bounce = int(math.sin(f * math.pi / 2) * 1.5)

        # Sombra en la calle
        draw.ellipse([10, 48, 110, 56], fill=SHADOW_CONTACT)

        # Carrocería principal redondeada
        cy = 22 + bounce
        # Techo cabina
        draw.ellipse([34, cy - 8, 86, cy + 18], fill=CHERRY_SHADOW)
        draw.ellipse([35, cy - 8, 85, cy + 17], fill=CHERRY_RED, outline=WINE_OUTLINE, width=1)
        # Ventanas celestes
        draw.ellipse([40, cy - 4, 58, cy + 10], fill=(185, 220, 245, 230), outline=WINE_OUTLINE, width=1)
        draw.ellipse([62, cy - 4, 80, cy + 10], fill=(185, 220, 245, 230), outline=WINE_OUTLINE, width=1)

        # Cuerpo del auto
        draw.rounded_rectangle([15, cy + 6, 105, cy + 24], radius=6, fill=CHERRY_RED, outline=WINE_OUTLINE, width=1)
        draw.line([(15, cy + 18), (105, cy + 18)], fill=CHROME_BUMPER, width=1)

        # Árbol de Navidad amarrado al techo
        draw.polygon([(30, cy - 10), (90, cy - 10), (80, cy - 20), (45, cy - 18)], fill=PINE_GREEN, outline=WINE_OUTLINE)
        # Cuerdas doradas amarrando el árbol
        draw.line([(42, cy - 18), (48, cy - 6)], fill=GOLD_ACCENT, width=1)
        draw.line([(68, cy - 18), (74, cy - 6)], fill=GOLD_ACCENT, width=1)

        # Faros delanteros luminosos
        draw.ellipse([100, cy + 10, 106, cy + 18], fill=HEADLIGHT_GLOW)
        # Cono de luz proyectado
        draw.polygon([(106, cy + 12), (120, cy + 6), (120, cy + 28), (106, cy + 16)], fill=(255, 240, 160, 60))

        # Ruedas con tapacubos cromados
        wheel_rot = f * 45
        for wx in [32, 88]:
            draw.ellipse([wx - 8, cy + 16, wx + 8, cy + 32], fill=(40, 38, 42, 255), outline=WINE_OUTLINE, width=1)
            draw.ellipse([wx - 4, cy + 20, wx + 4, cy + 28], fill=CHROME_BUMPER)

        car_sheet.paste(frame, (f * CAR_W, 0))

    car_sheet.save(os.path.join(OUTPUT_DIR, "vintage_car_sheet.png"))
    print("✓ Generado vintage_car_sheet.png")

    # 2. Gatito Esponjoso en la Banca
    CAT_W, CAT_H = 48, 48
    cat_sheet = Image.new("RGBA", (CAT_W * 4, CAT_H), (0, 0, 0, 0))
    CAT_ORANGE = (235, 145, 60, 255)
    CAT_WHITE = (255, 250, 245, 255)

    for f in range(4):
        frame = Image.new("RGBA", (CAT_W, CAT_H), (0, 0, 0, 0))
        draw = ImageDraw.Draw(frame)
        tail_w = math.sin(f * math.pi / 2) * 4

        # Sombra
        draw.ellipse([12, 38, 36, 44], fill=SHADOW_CONTACT)

        # Cola
        draw.line([(32, 34), (40 + tail_w, 28), (42 + tail_w, 20)], fill=CAT_ORANGE, width=3)

        # Cuerpo
        draw.ellipse([14, 22, 36, 38], fill=CAT_ORANGE, outline=WINE_OUTLINE, width=1)
        draw.ellipse([16, 26, 30, 38], fill=CAT_WHITE)

        # Cabeza
        draw.ellipse([18, 12, 34, 26], fill=CAT_ORANGE, outline=WINE_OUTLINE, width=1)
        # Orejitas
        draw.polygon([(20, 14), (23, 6), (26, 13)], fill=CAT_ORANGE, outline=WINE_OUTLINE)
        draw.polygon([(26, 13), (29, 6), (32, 14)], fill=CAT_ORANGE, outline=WINE_OUTLINE)

        # Ojos cerrados dormilones o abiertos
        if f % 2 == 0:
            draw.arc([21, 16, 25, 20], start=0, end=180, fill=WINE_OUTLINE, width=1)
            draw.arc([27, 16, 31, 20], start=0, end=180, fill=WINE_OUTLINE, width=1)
        else:
            draw.ellipse([22, 17, 24, 19], fill=WINE_OUTLINE)
            draw.ellipse([28, 17, 30, 19], fill=WINE_OUTLINE)

        cat_sheet.paste(frame, (f * CAT_W, 0))

    cat_sheet.save(os.path.join(OUTPUT_DIR, "cat_sheet.png"))
    print("✓ Generado cat_sheet.png")

if __name__ == "__main__":
    generate_rata_aspirable()
    generate_principessa()
    generate_street_life()
    print("¡Spritesheets generados exitosamente!")
