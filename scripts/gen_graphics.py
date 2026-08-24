from PIL import Image, ImageDraw, ImageFont
import os

W, H = 1024, 500
img = Image.new('RGB', (W,H), '#f8f1e4')
draw = ImageDraw.Draw(img)
for y in range(H):
    r = int(0xdc + (0xff-0xdc)*y/H)
    g = int(0xec + (0xfb-0xec)*y/H)
    b = int(0xe5 + (0xf4-0xe5)*y/H)
    draw.line([(0,y),(W,y)], fill=(r,g,b))
draw.rectangle([0,0,W,H], outline='#173a38', width=2)
try:
    title_font = ImageFont.truetype('C:/Windows/Fonts/georgia.ttf', 52)
    sub_font = ImageFont.truetype('C:/Windows/Fonts/arial.ttf', 20)
    serif_font = ImageFont.truetype('C:/Windows/Fonts/arial.ttf', 16)
except:
    title_font = ImageFont.load_default()
    sub_font = ImageFont.load_default()
    serif_font = ImageFont.load_default()
draw.text((48, 70), 'VERSE', font=title_font, fill='#173a38')
draw.text((208, 70), 'Light', font=title_font, fill='#b88746')
draw.text((48, 145), 'A little light, every day.', font=sub_font, fill='#173a38')
draw.text((48, 175), 'One shared Bible verse \u2022 Private journey \u2022 Gracious community', font=sub_font, fill='#6c6a62')
draw.text((48, 215), '\u2600 Daily verse  \u2661 Private  \u2726 Community  \U0001f310 60+ languages', font=serif_font, fill='#315a4e')
phone_x, phone_y = 720, 45
phone_w, phone_h = 260, 410
draw.rounded_rectangle([phone_x, phone_y, phone_x+phone_w, phone_y+phone_h], radius=32, fill='#172320')
draw.rounded_rectangle([phone_x+12, phone_y+18, phone_x+phone_w-12, phone_y+phone_h-12], radius=28, fill='#f8f1e4')
draw.rounded_rectangle([phone_x+90, phone_y+18, phone_x+170, phone_y+32], radius=8, fill='#172320')
draw.text((phone_x+60, phone_y+45), '\u2600  VERSELIGHT', font=sub_font, fill='#173a38')
draw.rounded_rectangle([phone_x+28, phone_y+85, phone_x+phone_w-28, phone_y+280], radius=18, fill='white', outline='#e6dcc8')
draw.text((phone_x+110, phone_y+100), '\u2726', font=title_font, fill='#b88746')
verse = '"The light shines in\nthe darkness, and the\ndarkness hasn\'t\novercome it."'
draw.text((phone_x+38, phone_y+135), verse, font=sub_font, fill='#173a38', align='center', spacing=4)
draw.text((phone_x+62, phone_y+245), 'JOHN 1:5 \u00b7 WEB', font=serif_font, fill='#6c6a62')
draw.text((48, 430), 'FREE \u2022 OFFLINE \u2022 CONTAINS ADS  \u2022  WORLD ENGLISH BIBLE', font=serif_font, fill='#6c6a62')
os.makedirs('H:/bible-verse-app/fastlane/metadata/android/en-US/images', exist_ok=True)
img.save('H:/bible-verse-app/fastlane/metadata/android/en-US/images/featureGraphic.png', 'PNG')
img.save('H:/bible-verse-app/store-feature-graphic-1024x500.png', 'PNG')
print('feature graphic done', img.size)
icon = Image.new('RGB', (512,512), '#173a38')
d2 = ImageDraw.Draw(icon)
d2.rounded_rectangle([0,0,512,512], radius=110, fill='#173a38')
d2.ellipse([86,86,426,426], fill='#f8f1e4')
d2.rectangle([246,150,266,360], fill='#173a38')
d2.rectangle([170,230,342,250], fill='#173a38')
d2.rectangle([120,365,392,385], fill='#b88746')
d2.rectangle([150,392,362,402], fill='#b88746')
icon.save('H:/bible-verse-app/fastlane/metadata/android/en-US/images/icon.png', 'PNG')
icon.save('H:/bible-verse-app/store-icon-512.png', 'PNG')
print('icon done')
for i, (title, verse, ref) in enumerate([
    ('Today\'s Light', '"Your word is a lamp\nto my feet, and a\nlight for my path."', 'PSALM 119:105'),
    ('Your Journey', '"Be still, and know\nthat I am God."', 'PSALM 46:10'),
    ('Gracious Community', '"Love is patient,\nlove is kind."', '1 COR 13:4'),
    ('60+ Languages', '"La luz brilla en\nla oscuridad."', 'JUAN 1:5'),
]):
    Wp, Hp = 1080, 1920
    s = Image.new('RGB', (Wp,Hp), '#dcece5')
    d = ImageDraw.Draw(s)
    d.rectangle([0,0,Wp,120], fill='#173a38')
    try:
        big = ImageFont.truetype('C:/Windows/Fonts/georgia.ttf', 78)
        med = ImageFont.truetype('C:/Windows/Fonts/arial.ttf', 42)
        small = ImageFont.truetype('C:/Windows/Fonts/arial.ttf', 32)
    except:
        big = ImageFont.load_default()
        med = small = big
    d.text((60, 35), 'VERSELight', font=med, fill='white')
    d.text((Wp-180, 45), '\u2600', font=med, fill='#b88746')
    d.rounded_rectangle([60, 220, Wp-60, 1100], radius=48, fill='white')
    d.text((Wp//2-20, 320), '\u2726', font=big, fill='#b88746', anchor='mm')
    d.text((Wp//2, 550), verse, font=big, fill='#173a38', anchor='mm', align='center', spacing=12)
    d.text((Wp//2, 850), ref + ' \u00b7 WEB', font=med, fill='#6c6a62', anchor='mm')
    d.text((Wp//2, 950), 'Tap \u2661 to save to your private journey', font=small, fill='#b88746', anchor='mm')
    d.rounded_rectangle([60, 1350, Wp-60, 1580], radius=32, fill='white')
    d.text((120, 1420), '\u2600  Today', font=med, fill='#173a38')
    d.text((480, 1420), '\u2661  Journey', font=med, fill='#6c6a62')
    d.text((820, 1420), '\u2630  Profile', font=med, fill='#6c6a62')
    d.text((Wp//2, 1800), 'Contains ads \u2022 Free \u2022 Offline', font=small, fill='#6c6a62', anchor='mm')
    s.save(f'H:/bible-verse-app/fastlane/metadata/android/en-US/images/phoneScreenshots_{i+1}.png', 'PNG')
    print(f'screenshot {i+1} done')
print('all graphics done')
