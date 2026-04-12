#!/usr/bin/env python3
"""Generate all Tauri icon files from the base SVG."""

import cairosvg
from PIL import Image
import io
import os

ICONS_DIR = "frontend/apps/desktop/src-tauri/icons"
SVG_PATH = os.path.join(ICONS_DIR, "icon.svg")

# Read the SVG source
with open(SVG_PATH, "r") as f:
    svg_data = f.read()


def svg_to_png(size: int) -> Image.Image:
    """Render SVG to a PIL Image at the given size."""
    png_bytes = cairosvg.svg2png(
        bytestring=svg_data.encode("utf-8"),
        output_width=size,
        output_height=size,
    )
    return Image.open(io.BytesIO(png_bytes))


# All sizes Tauri expects
png_icons = {
    "32x32.png": 32,
    "128x128.png": 128,
    "128x128@2x.png": 256,
    "icon.png": 512,
    # Windows Store logos
    "Square30x30Logo.png": 30,
    "Square44x44Logo.png": 44,
    "Square71x71Logo.png": 71,
    "Square89x89Logo.png": 89,
    "Square107x107Logo.png": 107,
    "Square142x142Logo.png": 142,
    "Square150x150Logo.png": 150,
    "Square284x284Logo.png": 284,
    "Square310x310Logo.png": 310,
    "StoreLogo.png": 50,
}

for filename, size in png_icons.items():
    img = svg_to_png(size)
    img.save(os.path.join(ICONS_DIR, filename), "PNG")
    print(f"  {filename} ({size}x{size})")

# Generate .ico (Windows) — multiple sizes embedded
ico_sizes = [16, 24, 32, 48, 64, 128, 256]
ico_images = [svg_to_png(s) for s in ico_sizes]
ico_images[0].save(
    os.path.join(ICONS_DIR, "icon.ico"),
    format="ICO",
    sizes=[(s, s) for s in ico_sizes],
    append_images=ico_images[1:],
)
print(f"  icon.ico ({', '.join(str(s) for s in ico_sizes)})")

# Generate .icns (macOS) — Pillow supports saving icns
icns_sizes = [16, 32, 64, 128, 256, 512]
icns_images = [svg_to_png(s) for s in icns_sizes]
icns_images[0].save(
    os.path.join(ICONS_DIR, "icon.icns"),
    format="ICNS",
    append_images=icns_images[1:],
)
print(f"  icon.icns ({', '.join(str(s) for s in icns_sizes)})")

print("\nDone!")
