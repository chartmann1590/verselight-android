from PIL import Image, ImageDraw
import math

# Exact colors from app
BG = "#1E3B3A"
CREAM = "#F4E9D5"
GOLD = "#B68B47"

SIZE = 512
# Adaptive icon viewport 108
SCALE = SIZE / 108

# Create 512 with background #1E3B3A and rounded corners (Play icon is square with rounded, but we provide square 512 with no rounding? Play requires 512 with no rounded corners, but we can provide with rounded for preview)
# For Play high-res icon, corners should be square (Play will mask), but we provide with 32px radius? Better to provide square without clipping, or with rounded 64.
img = Image.new("RGBA", (SIZE, SIZE), BG)
draw = ImageDraw.Draw(img)

# Draw cream circle: center 54,54 radius 41 in viewport
cx, cy = 54*SCALE, 54*SCALE
r = 41*SCALE
# Cream circle
draw.ellipse([cx-r, cy-r, cx+r, cy+r], fill=CREAM)

# Cross: path M50,28h8v20h17v8H58v25h-8V56H33v-8h17z
# This is a cross shape. Let's approximate as rectangles with exact coords scaled
# Horizontal bar of cross and vertical
# Vertical: x 50-58, y 28-81? Actually path: start 50,28 h8 (to 58) v20 (to 48) h17 (to 75) v8 (to 56) H58 (to 58) v25 (to 81) h-8 (to 50) V56 (to 56) H33 (to 33) v-8 (to 48) h17 (to 50) z
# Simplify: vertical bar 50-58, y 28-81 ; horizontal bar 33-75, y 48-56
vx1, vx2 = 50*SCALE, 58*SCALE
vy1, vy2 = 28*SCALE, 81*SCALE
hx1, hx2 = 33*SCALE, 75*SCALE
hy1, hy2 = 48*SCALE, 56*SCALE
draw.rectangle([vx1, vy1, vx2, vy2], fill=BG)
draw.rectangle([hx1, hy1, hx2, hy2], fill=BG)

# Gold underline: M29,72c7,5 15,7 25,7s18,-2 25,-7v6c-7,5 -15,8 -25,8s-18,-3 -25,-8z
# This is two curves. Approximate as rectangle + bezier. For exact, approximate as two horizontal bars with curves
# We'll draw as polygon approximating curve
# Top curve y 72 with control points 7,5 etc. Hard to replicate exactly with bezier, approximate as rectangle with slight curve
# Simplify: draw gold bar at y 72-78 and 78-80
# Top gold shape: from 29,72 to 79,72 with curve down 5-7 then flat
# Let's just draw as per path using PIL bezier approximation: create polygon points for curve
import math

def draw_gold():
    # Convert path to polygon with bezier approximation using cubic
    # M29,72 c7,5 15,7 25,7 s18,-2 25,-7
    # This is two cubic beziers: first c7,5 15,7 25,7 ; second s18,-2 25,-7 where s reflects
    # Second control is mirror of previous: 25,7 + (25,7 - 15,7) = 35,7? Actually need to compute
    # Then v6, then second curve mirrored downwards: -7,5 -15,8 -25,8
    # For simplicity, draw as filled polygon with 100 points sampling bezier
    def cubic(p0, p1, p2, p3, t):
        return (1-t)**3*p0 + 3*(1-t)**2*t*p1 + 3*(1-t)*t**2*p2 + t**3*p3
    # top curve points
    points = []
    # start 29,72
    x0, y0 = 29*SCALE, 72*SCALE
    points.append((x0,y0))
    # first bezier: cp1 29+7,72+5 ; cp2 29+15,72+7 ; end 29+25,72+7 => 54,79
    p0x, p0y = 29*SCALE, 72*SCALE
    p1x, p1y = (29+7)*SCALE, (72+5)*SCALE
    p2x, p2y = (29+15)*SCALE, (72+7)*SCALE
    p3x, p3y = (29+25)*SCALE, (72+7)*SCALE
    for t in [i/30 for i in range(31)]:
        points.append((cubic(p0x,p1x,p2x,p3x,t), cubic(p0y,p1y,p2y,p3y,t)))
    # second bezier s18,-2 25,-7 : cp1 is reflection of previous cp2 => 54+ (54-44)=64,79 ; cp2 54+18,79-2=72,77 ; end 54+25,79-7=79,72
    p0x, p0y = 54*SCALE, 79*SCALE
    # reflected cp1: 54 + (54-44) =64
    p1x, p1y = 64*SCALE, 79*SCALE
    p2x, p2y = 72*SCALE, 77*SCALE
    p3x, p3y = 79*SCALE, 72*SCALE
    for t in [i/30 for i in range(31)]:
        points.append((cubic(p0x,p1x,p2x,p3x,t), cubic(p0y,p1y,p2y,p3y,t)))
    # v6 down to 78
    points.append((79*SCALE, 78*SCALE))
    # bottom curve mirrored: M? Actually after v6, we go c-7,5 -15,8 -25,8 s-18,-3 -25,-8
    # bottom curve from 79,78 c-7,5 -15,8 -25,8 => to 54,86
    p0x, p0y = 79*SCALE, 78*SCALE
    p1x, p1y = 72*SCALE, 83*SCALE
    p2x, p2y = 64*SCALE, 86*SCALE
    p3x, p3y = 54*SCALE, 86*SCALE
    for t in [i/30 for i in range(31)]:
        points.append((cubic(p0x,p1x,p2x,p3x,t), cubic(p0y,p1y,p2y,p3y,t)))
    # second bottom s-18,-3 -25,-8 => to 29,78
    p0x, p0y = 54*SCALE, 86*SCALE
    p1x, p1y = 44*SCALE, 86*SCALE
    p2x, p2y = 36*SCALE, 83*SCALE
    p3x, p3y = 29*SCALE, 78*SCALE
    for t in [i/30 for i in range(31)]:
        points.append((cubic(p0x,p1x,p2x,p3x,t), cubic(p0y,p1y,p2y,p3y,t)))
    points.append((29*SCALE, 72*SCALE))
    draw.polygon(points, fill=GOLD)

draw_gold()

# Add subtle rounded corners for Play preview (optional, Play masks anyway) - keep square for exact match, but we can add 64 radius for aesthetics if needed
# For Play high-res icon, corners should be square, no rounding, system masks. So keep square.

# Save
img512 = img.resize((512,512), Image.LANCZOS)
# Ensure RGBA to RGB for Play (needs opaque)
# Our background is opaque, so convert to RGB
rgb = Image.new("RGB", (512,512), BG)
rgb.paste(img512, mask=img512.split()[3] if img512.mode=="RGBA" else None)
rgb.save("H:/bible-verse-app/store-icon-512-exact.png", "PNG")
rgb.save("H:/bible-verse-app/website/assets/icon-512.png", "PNG")
# Also save as webp for comparison
print("Exact icon generated 512", rgb.size)
# Generate feature graphic with exact icon on right
from PIL import ImageFont
W,H = 1024,500
fg = Image.new("RGB", (W,H), "#F4E9D5")
d2 = ImageDraw.Draw(fg)
for y in range(H):
    r = int(0x1E + (0xF4-0x1E)*y/H)
    g = int(0x3B + (0xE9-0x3B)*y/H)
    b = int(0x3A + (0xD5-0x3A)*y/H)
    d2.line([(0,y),(W,y)], fill=(r,g,b))
# Copy icon to right side of feature graphic
icon_small = rgb.resize((180,180), Image.LANCZOS)
fg.paste(icon_small, (780, 160))
try:
    tf = ImageFont.truetype("C:/Windows/Fonts/georgia.ttf", 52)
    sf = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 20)
except:
    tf=sf=ImageFont.load_default()
d2.text((48,70), "VERSE", font=tf, fill="#1E3B3A")
d2.text((210,70), "Light", font=tf, fill="#B68B47")
d2.text((48,145), "A little light, every day.", font=sf, fill="#1E3B3A")
fg.save("H:/bible-verse-app/store-feature-graphic-1024x500-exact.png", "PNG")
print("Feature graphic exact saved")
