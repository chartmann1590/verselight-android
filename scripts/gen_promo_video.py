from PIL import Image, ImageDraw, ImageFont
import subprocess, os

W, H = 1920, 1080
tmp = "H:/bible-verse-app/tmp_promo"
os.makedirs(tmp, exist_ok=True)

def draw_frame(text1, text2, verse, ref, frame_num, bg="#f8f1e4"):
    img = Image.new('RGB', (W,H), bg)
    d = ImageDraw.Draw(img)
    try:
        title = ImageFont.truetype("C:/Windows/Fonts/georgia.ttf", 72)
        big = ImageFont.truetype("C:/Windows/Fonts/georgia.ttf", 64)
        med = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 38)
        small = ImageFont.truetype("C:/Windows/Fonts/arial.ttf", 28)
    except:
        title = big = med = small = ImageFont.load_default()
    
    # top bar
    d.rectangle([0,0,W,110], fill="#173a38")
    d.text((60,28), "LumaVerse", font=med, fill="white")
    d.text((W-120,32), "\u2600", font=med, fill="#b88746")
    
    # card centered
    card_w, card_h = 900, 620
    cx, cy = W//2 - card_w//2, 200
    d.rounded_rectangle([cx,cy,cx+card_w,cy+card_h], radius=40, fill="white")
    d.text((W//2, cy+80), "\u2726", font=title, fill="#b88746", anchor="mm")
    d.text((W//2, cy+220), verse, font=big, fill="#173a38", anchor="mm", align="center", spacing=10)
    d.text((W//2, cy+420), ref, font=med, fill="#6c6a62", anchor="mm")
    d.text((W//2, cy+500), text2, font=small, fill="#b88746", anchor="mm")
    
    # bottom caption
    d.text((W//2, H-120), text1, font=med, fill="#173a38", anchor="mm")
    d.text((W//2, H-70), "FREE \u2022 OFFLINE \u2022 60+ LANGUAGES \u2022 CONTAINS ADS", font=small, fill="#6c6a62", anchor="mm")
    
    path = f"{tmp}/frame_{frame_num:03d}.png"
    img.save(path)
    return path

frames = []
# 6 scenes, 5 seconds each at 30fps = 150 frames per scene, total 900 frames = 30s
scenes = [
    ("A little light, every day.", "One shared verse for everyone", '"The light shines in\nthe darkness"', "JOHN 1:5 \u00b7 WEB"),
    ("One shared verse, daily", "Beautiful, calm reading", '"Your word is a lamp\nto my feet"', "PSALM 119:105 \u00b7 WEB"),
    ("Your private journey", "Likes & shares stay private", '"Be still, and know\nthat I am God"', "PSALM 46:10 \u00b7 WEB"),
    ("Gracious community", "Kind reflections, no noise", '"Love is patient,\nlove is kind"', "1 COR 13:4 \u00b7 WEB"),
    ("60+ languages, on-device", "ML Kit \u2022 ~30MB \u2022 Offline", '"La luz brilla en\nla oscuridad"', "JUAN 1:5 \u00b7 WEB"),
    ("Carry the light", "Free \u2022 No in-app purchases", '"A little light,\nevery day"', "LUMAVERSE"),
]

fps = 30
sec_per_scene = 5
# generate one image per scene, ffmpeg will hold it
for i, (t1,t2,verse,ref) in enumerate(scenes):
    frames.append(draw_frame(t1,t2,verse,ref,i, bg="#f8f1e4" if i%2==0 else "#dcece5"))

# create concat file
concat = f"{tmp}/concat.txt"
with open(concat,"w",encoding="utf-8") as f:
    for p in frames:
        f.write(f"file '{p}'\n")
        f.write(f"duration {sec_per_scene}\n")
    f.write(f"file '{frames[-1]}'\n")

out = "H:/bible-verse-app/store-promo-video-1080p.mp4"
cmd = f'ffmpeg -y -f concat -safe 0 -i "{concat}" -vf "scale=1920:1080:flags=lanczos,fps=30" -c:v libx264 -pix_fmt yuv420p -crf 20 -preset veryfast -t 30 "{out}"'
print(cmd)
import subprocess
r = subprocess.run(cmd, shell=True, capture_output=True, text=True)
print(r.stdout[-2000:] if r.stdout else "")
print(r.stderr[-2000:] if r.stderr else "")
print("done", os.path.exists(out), os.path.getsize(out) if os.path.exists(out) else 0)
