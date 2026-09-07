#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os

fastlane_base = r"H:/bible-verse-app/fastlane/metadata/android"
play_base = r"H:/bible-verse-app/app/src/main/play/listings"
release_notes_base = r"H:/bible-verse-app/app/src/main/play/release-notes"

locales = ["en-US","es-ES","fr-FR","de-DE","pt-BR","it-IT","ja-JP","ko-KR","pt-PT","es-419","en-GB","fr-CA","ar","zh-CN"]

titles = {
 "en-US": "LumaVerse: Daily Bible Verse",
 "es-ES": "LumaVerse: Versículo Diario",
 "fr-FR": "LumaVerse: Verset du Jour",
 "de-DE": "LumaVerse: Täglicher Vers",
 "pt-BR": "LumaVerse: Versículo Diário",
 "it-IT": "LumaVerse: Verso del Giorno",
 "ja-JP": "LumaVerse: 毎日の聖句",
 "ko-KR": "LumaVerse: 매일 성경 구절",
 "pt-PT": "LumaVerse: Versículo Diário",
 "es-419": "LumaVerse: Versículo Diario",
 "en-GB": "LumaVerse: Daily Bible Verse",
 "fr-CA": "LumaVerse: Verset du Jour",
 "ar": "LumaVerse: آية يومية",
 "zh-CN": "LumaVerse: 每日圣经经文",
}

short_desc = {
 "en-US": "One shared Bible verse daily. Calm, private community. Free & offline.",
 "es-ES": "Un versículo diario compartido. Comunidad privada y serena. Gratis.",
 "fr-FR": "Un verset partagé chaque jour. Communauté privée et sereine. Gratuit.",
 "de-DE": "Ein gemeinsamer Vers täglich. Private, ruhige Community. Kostenlos.",
 "pt-BR": "Um versículo por dia, juntos. Comunidade privada e serena. Grátis.",
 "it-IT": "Un versetto al giorno, insieme. Comunità privata e serena. Gratis.",
 "ja-JP": "毎日ひとつの聖句を共に。穏やかでプライベートな community。無料。",
 "ko-KR": "매일 하나의 성경 구절을 함께. 평온하고 사적인 공동체. 무료.",
 "pt-PT": "Um versículo diário partilhado. Comunidade privada e serena. Grátis.",
 "es-419": "Un versículo diario compartido. Comunidad privada y tranquila. Gratis.",
 "en-GB": "One shared Bible verse daily. Calm, private community. Free & offline.",
 "fr-CA": "Un verset partagé chaque jour. Communauté privée et sereine. Gratuit.",
 "ar": "آية واحدة يومياً للجميع. مجتمع هادئ وخاص. مجاني دون إنترنت.",
 "zh-CN": "每日一节共享经文。私密、平静的社区。可离线，完全免费。",
}

# Fix Japanese short includes english word community typo - replace
short_desc["ja-JP"] = "毎日ひとつの聖句を共に。穏やかでプライベートな集い。無料・オフライン。"

video_url = "https://www.youtube.com/watch?v=PLACEHOLDER"

full_desc = {}

full_desc["en-US"] = """A little light, every day. ✨
One shared Bible verse, one calm moment — together, worldwide.

Why LumaVerse?
In a noisy world, LumaVerse is quiet by design. No endless feed. No followers, no DMs. Just one carefully shared verse each day to reflect, breathe, and begin again. Read, save, and carry it offline — wherever you are.

☀ ONE VERSE, SHARED DAILY
• The same verse for everyone, every day — a shared rhythm
• From the World English Bible (WEB) — public domain, free forever
• Beautiful, distraction-free reading in cream, ink & gold

♡ PRIVATE JOURNEY
• Your likes and shares stay private — only for you
• Save favorites and revisit your light over time
• No public counts, no pressure, no performance

✦ GRACIOUS COMMUNITY
• Share a short reflection if you wish — kindness is required
• On-device moderation helps keep conversation gentle and respectful
• No feed to scroll, no algorithm to chase

🌍 60+ LANGUAGES, ON-DEVICE
• Instant translation via on-device ML Kit — ~30MB models, works offline after download
• Spanish, French, German, Portuguese, Italian, Japanese, Korean, Arabic, Chinese and 50+ more
• Original WEB text always accessible alongside translation

🔔 DAILY REMINDER, YOUR RHYTHM
• Gentle daily notification at your chosen time — never spammy
• Offline-first: read anytime, even without signal
• Start your morning with light

🛡 PRIVATE & SAFE BY DESIGN
• Built with Firebase Auth & Firestore — minimal data, maximum respect
• No selling of personal data. Your journey is yours.
• GDPR-ready with Google UMP consent flow for EEA/UK
• Native Kotlin + Jetpack Compose — fast, modern, beautiful, offline-capable

— Contains ads —
LumaVerse is free forever with no in-app purchases. To keep it free, we show banner and interstitial ads via Google AdMob. Ads never cover your verse and interstitials are infrequent and dismissible. If you are in the EEA/UK, consent is requested via UMP.

Languages: English, Spanish (ES & 419), French (FR & CA), German, Portuguese (BR & PT), Italian, Japanese, Korean, Arabic, Chinese (Simplified), Dutch, Hindi, Indonesian, Russian, Turkish, Polish, Ukrainian, Thai, Vietnamese and 40+ more — all via on-device translation.

Made with care in ink #173a38, cream #f8f1e4 and gold #b88746 — calm, warm, premium.

A little light for your day — start today. Download LumaVerse free.
"""

full_desc["es-ES"] = """Un poco de luz, cada día. ✨
Un mismo versículo para todos, un momento de calma — juntos, en todo el mundo.

¿Por qué LumaVerse?
En un mundo ruidoso, LumaVerse es silencio por diseño. Sin feed infinito. Sin seguidores, sin mensajes. Solo un versículo diario, compartido por todos, para reflexionar, respirar y empezar de nuevo. Léelo, guárdalo y llévalo contigo sin conexión.

☀ UN VERSÍCULO, COMPARTIDO CADA DÍA
• El mismo versículo para todos, cada día — un ritmo común
• De la World English Bible (WEB) — dominio público, gratis para siempre
• Lectura hermosa, sin distracciones, en crema, tinta y dorado

♡ TU CAMINO PRIVADO
• Tus me gusta y compartidos son privados — solo para ti
• Guarda favoritos y revisita tu luz con el tiempo
• Sin contadores públicos, sin presión

✦ COMUNIDAD BONDADOSA
• Comparte una breve reflexión si quieres — la amabilidad es norma
• Moderación en el dispositivo mantiene el respeto y la serenidad
• Sin muro que scrollear, sin algoritmo que perseguir

🌍 MÁS DE 60 IDIOMAS, EN EL DISPOSITIVO
• Traducción instantánea con ML Kit en el dispositivo — modelos ~30MB, funciona offline tras descargar
• Español, francés, alemán, portugués, italiano, japonés, coreano, árabe, chino y 50+ más
• Texto original WEB siempre disponible

🔔 RECORDATORIO DIARIO, A TU RITMO
• Notificación diaria suave a la hora que elijas — nunca spam
• Primero sin conexión: lee en cualquier momento, sin cobertura
• Empieza la mañana con luz

🛡 PRIVADO Y SEGURO POR DISEÑO
• Con Firebase Auth y Firestore — datos mínimos, respeto máximo
• No vendemos datos personales. Tu camino es tuyo.
• Listo para RGPD con flujo de consentimiento Google UMP para EEE/RU
• Nativo en Kotlin + Jetpack Compose — rápido, moderno, bonito y offline

— Contiene anuncios —
LumaVerse es gratis para siempre sin compras dentro de la app. Para mantenerlo gratuito mostramos anuncios banner e intersticiales vía Google AdMob. Los anuncios nunca tapan tu versículo y los intersticiales son poco frecuentes. Si estás en EEE/RU, se solicita consentimiento vía UMP.

Idiomas: inglés, español (ES y 419), francés, alemán, portugués (BR y PT), italiano, japonés, coreano, árabe, chino simplificado, neerlandés, hindi, indonesio, ruso, turco, polaco y 40+ más mediante traducción en el dispositivo.

Diseñado con esmero en tinta #173a38, crema #f8f1e4 y dorado #b88746 — sereno, cálido y premium.

Un poco de luz para tu día — empieza hoy. Descarga LumaVerse gratis.
"""

full_desc["fr-FR"] = """Un peu de lumière, chaque jour. ✨
Un même verset partagé, un instant de calme — ensemble, partout dans le monde.

Pourquoi LumaVerse ?
Dans un monde bruyant, LumaVerse cultive le calme. Pas de fil infini. Pas d’abonnés, pas de messages privés. Juste un verset par jour, partagé par tous, pour réfléchir, respirer et recommencer. Lisez, enregistrez, emportez-le hors ligne.

☀ UN VERSET, PARTAGÉ CHAQUE JOUR
• Le même verset pour tous, chaque jour — un rythme commun
• Tiré de la World English Bible (WEB) — domaine public, gratuit pour toujours
• Lecture épurée, sans distraction, en crème, encre et or

♡ CHEMIN INTIME
• Vos likes et partages restent privés — pour vous seul
• Enregistrez vos favoris et retrouvez votre lumière au fil du temps
• Aucun compteur public, aucune pression

✦ COMMUNAUTÉ BIENVEILLANTE
• Partagez une courte réflexion si vous le souhaitez — la bienveillance est exigée
• Modération sur l’appareil pour des échanges doux et respectueux
• Pas de fil à faire défiler, pas d’algorithme

🌍 60+ LANGUES, SUR L’APPAREIL
• Traduction instantanée via ML Kit sur l’appareil — modèles ~30Mo, fonctionne hors ligne après téléchargement
• Espagnol, français, allemand, portugais, italien, japonais, coréen, arabe, chinois et 50+ autres
• Texte original WEB toujours accessible

🔔 RAPPEL QUOTIDIEN, À VOTRE RYTHME
• Notification quotidienne douce à l’heure choisie — jamais de spam
• Priorité hors ligne : lisez à tout moment, même sans réseau
• Commencez la matinée avec de la lumière

🛡 PRIVÉ ET SÛR PAR CONCEPTION
• Construit avec Firebase Auth & Firestore — données minimales, respect maximal
• Aucune vente de données personnelles. Votre chemin vous appartient.
• Conforme RGPD avec flux de consentement Google UMP pour EEE/RU
• Natif Kotlin + Jetpack Compose — rapide, moderne, beau, hors ligne

— Contient des annonces —
LumaVerse est gratuit pour toujours sans achat intégré. Pour rester gratuit, nous affichons des annonces bannière et interstitielles via Google AdMob. Les annonces ne masquent jamais votre verset et les interstitiels sont rares et fermables. Dans l’EEE/RU, le consentement est demandé via UMP.

Langues : anglais, espagnol (ES & 419), français (FR & CA), allemand, portugais (BR & PT), italien, japonais, coréen, arabe, chinois simplifié, néerlandais, hindi, indonésien, russe, turc, polonais et 40+ autres via traduction sur l’appareil.

Conçu avec soin en encre #173a38, crème #f8f1e4 et or #b88746 — calme, chaleureux, premium.

Un peu de lumière pour votre journée — commencez aujourd’hui. Téléchargez LumaVerse gratuitement.
"""

full_desc["de-DE"] = """Ein kleines Licht, jeden Tag. ✨
Ein gemeinsamer Bibelvers, ein ruhiger Moment — zusammen, weltweit.

Warum LumaVerse?
In einer lauten Welt ist LumaVerse bewusst leise. Kein endloser Feed. Keine Follower, keine DMs. Nur ein sorgfältig geteilter Vers pro Tag zum Innehalten, Atmen und Neubeginnen. Lesen, speichern und offline mitnehmen — überall.

☀ EIN VERS, TÄGLICH GEMEINSAM
• Derselbe Vers für alle, jeden Tag — ein gemeinsamer Rhythmus
• Aus der World English Bible (WEB) — gemeinfrei, für immer kostenlos
• Schönes, ablenkungsfreies Lesen in Creme, Tinte & Gold

♡ DEIN PRIVATER WEG
• Deine Likes und Shares bleiben privat — nur für dich
• Favoriten speichern und dein Licht immer wieder besuchen
• Keine öffentlichen Zähler, kein Druck

✦ GROSSZÜGIGE GEMEINSCHAFT
• Teile auf Wunsch einen kurzen Gedanken — Freundlichkeit ist Pflicht
• On-Device-Moderation hält Gespräche sanft und respektvoll
• Kein Feed zum Scrollen, kein Algorithmus

🌍 60+ SPRACHEN, AUF DEM GERÄT
• Sofortübersetzung via On-Device ML Kit — ~30MB Modelle, funktioniert offline nach Download
• Spanisch, Französisch, Deutsch, Portugiesisch, Italienisch, Japanisch, Koreanisch, Arabisch, Chinesisch und 50+ mehr
• Originaler WEB-Text immer verfügbar

🔔 TÄGLICHE ERINNERUNG, DEIN RHYTHMUS
• Sanfte tägliche Benachrichtigung zu deiner Wunschzeit — niemals Spam
• Offline-First: jederzeit lesen, auch ohne Empfang
• Starte den Morgen mit Licht

🛡 PRIVAT & SICHER BY DESIGN
• Mit Firebase Auth & Firestore — minimale Daten, maximaler Respekt
• Kein Verkauf persönlicher Daten. Dein Weg gehört dir.
• DSGVO-bereit mit Google UMP-Consent für EWR/UK
• Nativ in Kotlin + Jetpack Compose — schnell, modern, schön, offline-fähig

— Enthält Werbung —
LumaVerse ist für immer kostenlos ohne In-App-Käufe. Um kostenlos zu bleiben, zeigen wir Banner- und Interstitial-Anzeigen über Google AdMob. Anzeigen verdecken niemals deinen Vers und Interstitials sind selten und schließbar. Im EWR/UK wird die Zustimmung über UMP eingeholt.

Sprachen: Englisch, Spanisch (ES & 419), Französisch (FR & CA), Deutsch, Portugiesisch (BR & PT), Italienisch, Japanisch, Koreanisch, Arabisch, Chinesisch (vereinfacht), Niederländisch, Hindi, Indonesisch, Russisch, Türkisch, Polnisch und 40+ mehr via On-Device-Übersetzung.

Mit Sorgfalt gestaltet in Tinte #173a38, Creme #f8f1e4 und Gold #b88746 — ruhig, warm, premium.

Ein kleines Licht für deinen Tag — starte heute. Lade LumaVerse kostenlos.
"""

full_desc["pt-BR"] = """Um pouco de luz, todo dia. ✨
Um mesmo versículo para todos, um momento de calma — juntos, no mundo todo.

Por que LumaVerse?
Num mundo ruidoso, o LumaVerse é silêncio por design. Sem feed infinito. Sem seguidores, sem DMs. Apenas um versículo por dia, compartilhado por todos, para refletir, respirar e recomeçar. Leia, salve e leve offline — onde estiver.

☀ UM VERSÍCULO, COMPARTILHADO TODO DIA
• O mesmo versículo para todos, todo dia — um ritmo em comum
• Da World English Bible (WEB) — domínio público, grátis para sempre
• Leitura linda, sem distrações, em creme, tinta e dourado

♡ JORNADA PRIVADA
• Suas curtidas e compartilhamentos ficam privados — só para você
• Salve favoritos e revisite sua luz ao longo do tempo
• Sem contadores públicos, sem pressão

✦ COMUNIDADE GENTIL
• Compartilhe uma breve reflexão se quiser — gentileza é obrigatória
• Moderação no dispositivo mantém conversas suaves e respeitosas
• Sem feed para rolar, sem algoritmo para perseguir

🌍 MAIS DE 60 IDIOMAS, NO DISPOSITIVO
• Tradução instantânea via ML Kit no dispositivo — modelos ~30MB, funciona offline após baixar
• Espanhol, francês, alemão, português, italiano, japonês, coreano, árabe, chinês e 50+ outros
• Texto original WEB sempre acessível

🔔 LEMBRETE DIÁRIO, NO SEU RITMO
• Notificação diária suave no horário que escolher — nunca spam
• Offline primeiro: leia a qualquer hora, mesmo sem sinal
• Comece a manhã com luz

🛡 PRIVADO E SEGURO POR DESIGN
• Feito com Firebase Auth & Firestore — dados mínimos, respeito máximo
• Não vendemos dados pessoais. Sua jornada é sua.
• Pronto para LGPD/RGPD com fluxo de consentimento Google UMP para EEE/RU
• Nativo em Kotlin + Jetpack Compose — rápido, moderno, lindo e offline

— Contém anúncios —
LumaVerse é grátis para sempre sem compras no app. Para continuar grátis exibimos anúncios de banner e intersticiais via Google AdMob. Os anúncios nunca cobrem seu versículo e intersticiais são raros e dispensáveis. No EEE/RU, o consentimento é solicitado via UMP.

Idiomas: inglês, espanhol (ES e 419), francês (FR e CA), alemão, português (BR e PT), italiano, japonês, coreano, árabe, chinês simplificado, holandês, hindi, indonésio, russo, turco, polonês e 40+ outros via tradução no dispositivo.

Feito com cuidado em tinta #173a38, creme #f8f1e4 e dourado #b88746 — calmo, acolhedor e premium.

Um pouco de luz para o seu dia — comece hoje. Baixe LumaVerse grátis.
"""

full_desc["it-IT"] = """Un po' di luce, ogni giorno. ✨
Uno stesso versetto per tutti, un momento di calma — insieme, in tutto il mondo.

Perché LumaVerse?
In un mondo rumoroso, LumaVerse è quiete per scelta. Nessun feed infinito. Niente follower, niente DM. Solo un versetto al giorno, condiviso da tutti, per riflettere, respirare e ricominciare. Leggi, salva e porta con te anche offline.

☀ UN VERSETTO, CONDIVISO OGNI GIORNO
• Lo stesso versetto per tutti, ogni giorno — un ritmo comune
• Dalla World English Bible (WEB) — di pubblico dominio, gratis per sempre
• Lettura elegante, senza distrazioni, in crema, inchiostro e oro

♡ PERCORSO PRIVATO
• I tuoi like e le condivisioni restano privati — solo per te
• Salva i preferiti e ritrova la tua luce nel tempo
• Nessun contatore pubblico, nessuna pressione

✦ COMUNITÀ GENTILE
• Condividi una breve riflessione se vuoi — la gentilezza è obbligatoria
• Moderazione on-device mantiene conversazioni delicate e rispettose
• Nessun feed da scorrere, nessun algoritmo da inseguire

🌍 OLTRE 60 LINGUE, SUL DISPOSITIVO
• Traduzione istantanea tramite ML Kit on-device — modelli ~30MB, funziona offline dopo il download
• Spagnolo, francese, tedesco, portoghese, italiano, giapponese, coreano, arabo, cinese e altri 50+
• Testo originale WEB sempre accessibile

🔔 PROMEMORIA GIORNALIERO, AL TUO RITMO
• Notifica giornaliera delicata all’orario che scegli — mai spam
• Offline-first: leggi quando vuoi, anche senza segnale
• Inizia la mattina con luce

🛡 PRIVATO E SICURO BY DESIGN
• Costruito con Firebase Auth & Firestore — dati minimi, rispetto massimo
• Non vendiamo dati personali. Il tuo cammino è tuo.
• Pronto per GDPR con flusso di consenso Google UMP per SEE/RU
• Nativo Kotlin + Jetpack Compose — veloce, moderno, bello, offline

— Contiene annunci —
LumaVerse è gratis per sempre senza acquisti in-app. Per restare gratis mostriamo annunci banner e interstitial tramite Google AdMob. Gli annunci non coprono mai il tuo versetto e gli interstitial sono rari e chiudibili. Nel SEE/RU il consenso è richiesto tramite UMP.

Lingue: inglese, spagnolo (ES e 419), francese (FR e CA), tedesco, portoghese (BR e PT), italiano, giapponese, coreano, arabo, cinese semplificato, olandese, hindi, indonesiano, russo, turco, polacco e altri 40+ tramite traduzione on-device.

Realizzato con cura in inchiostro #173a38, crema #f8f1e4 e oro #b88746 — calmo, caldo, premium.

Un po' di luce per la tua giornata — inizia oggi. Scarica LumaVerse gratis.
"""

full_desc["ja-JP"] = """毎日、小さな光を。✨
みんなで同じ聖句、穏やかなひととき — 世界中で一緒に。

なぜ LumaVerse なのか？
騒がしい世界で、LumaVerse は静けさを大切に設計されています。無限のフィードなし。フォロワーなし、DMなし。1日1つの大切な聖句をみんなで分かち合い、立ち止まり、呼吸し、また歩き出す。読んで、保存して、オフラインでもどこでも持ち歩けます。

☀ 1日1つの聖句を共有
• 毎日、誰もが同じ聖句 — 共通のリズム
• World English Bible (WEB) より — パブリックドメイン、永遠に無料
• クリーム、インク、ゴールドの美しく邪魔のない読書体験

♡ プライベートな歩み
• いいねやシェアはあなただけのもの — 非公開
• お気に入りを保存し、時を越えて光を振り返る
• 公開カウントなし、プレッシャーなし

✦ 思いやりのコミュニティ
• 望めば短い分かち合いを投稿 — 優しさがルール
• オンデバイスモデレーションが穏やかで敬意ある会話を守ります
• スクロールするフィードなし、追うアルゴリズムなし

🌍 60以上の言語、端末内で翻訳
• ML Kit オンデバイス翻訳 — 約30MBのモデル、ダウンロード後はオフラインで動作
• スペイン語、フランス語、ドイツ語、ポルトガル語、イタリア語、日本語、韓国語、アラビア語、中国語など50以上
• 元のWEBテキストはいつでも確認可能

🔔 毎日のリマインダー、あなたのリズムで
• 好きな時間に優しい通知 — スパムはしません
• オフライン優先：電波がなくてもいつでも読める
• 朝を光とともに始めよう

🛡 プライバシーと安全を最優先に設計
• Firebase Auth & Firestore 採用 — 最小限のデータ、最大限の敬意
• 個人データの販売はしません。あなたの歩みはあなたのもの
• GDPR対応、EEA/UKでは Google UMP で同意取得
• ネイティブ Kotlin + Jetpack Compose — 高速、モダン、美しくオフライン対応

— 広告について —
LumaVerse は購入なしで永遠に無料です。無料を維持するため Google AdMob によるバナーとインタースティシャル広告を表示します。広告が聖句を覆うことはなく、インタースティシャルは頻度が少なくスキップ可能です。EEA/UKではUMPで同意を求めます。

対応言語：英語、スペイン語 (ES & 419)、フランス語 (FR & CA)、ドイツ語、ポルトガル語 (BR & PT)、イタリア語、日本語、韓国語、アラビア語、中国語(簡体字)、オランダ語、ヒンディー語、インドネシア語、ロシア語、トルコ語、ポーランド語など40以上 — すべて端末内翻訳で。

インク #173a38、クリーム #f8f1e4、ゴールド #b88746 で丁寧に作られた — 穏やかで温かくプレミアムな体験。

今日の小さな光を — 今すぐ LumaVerse を無料で始めよう。
"""

full_desc["ko-KR"] = """매일, 작은 빛을. ✨
모두가 같은 성경 구절, 고요한 한 순간 — 전 세계가 함께.

왜 LumaVerse인가요?
소란한 세상에서 LumaVerse는 고요함을 선택했습니다. 끝없는 피드 없음. 팔로워 없음, DM 없음. 하루 하나의 귀한 구절을 모두가 함께 나누며 묵상하고 숨 고르고 다시 시작합니다. 읽고, 저장하고, 오프라인에서도 어디서나 간직하세요.

☀ 하루 하나의 구절, 함께 나눔
• 매일 모두가 같은 구절 — 같은 리듬
• World English Bible (WEB) — 퍼블릭 도메인, 영원히 무료
• 크림, 잉크, 골드 색상의 아름답고 방해 없는 읽기

♡ 사적인 여정
• 좋아요와 공유는 비공개 — 오직 당신만 볼 수 있습니다
• 즐겨찾기를 저장하고 시간이 지나도 빛을 되새기세요
• 공개 카운트 없음, 부담 없음

✦ 은혜로운 공동체
• 원하면 짧은 묵상을 나눌 수 있어요 — 친절이 원칙입니다
• 온디바이스 moderation이 부드럽고 존중하는 대화를 지켜줍니다
• 스크롤할 피드도, 쫓을 알고리즘도 없습니다

🌍 60개 이상 언어, 기기 내 번역
• 온디바이스 ML Kit 번역 — 약 30MB 모델, 다운로드 후 오프라인 동작
• 스페인어, 프랑스어, 독일어, 포르투갈어, 이탈리아어, 일본어, 한국어, 아랍어, 중국어 등 50개 이상
• 원문 WEB 텍스트는 언제나 함께 확인 가능

🔔 매일 알림, 당신의 리듬에 맞춰
• 원하는 시간에 부드러운 일일 알림 — 스팸 절대 없음
• 오프라인 우선: 신호가 없어도 언제나 읽기 가능
• 아침을 빛과 함께 시작하세요

🛡 프라이버시와 안전을 최우선으로 설계
• Firebase Auth & Firestore 기반 — 최소한의 데이터, 최대한의 존중
• 개인정보를 판매하지 않습니다. 당신의 여정은 당신의 것입니다
• GDPR 대응, EEA/UK에서는 Google UMP로 동의 요청
• 네이티브 Kotlin + Jetpack Compose — 빠르고 현대적이며 아름답고 오프라인 지원

— 광고 포함 —
LumaVerse는 인앱 구매 없이 영원히 무료입니다. 무료 유지를 위해 Google AdMob 배너 및 전면 광고를 표시합니다. 광고가 구절을 가리지 않으며 전면 광고는 드물고 닫을 수 있습니다. EEA/UK에서는 UMP를 통해 동의를 요청합니다.

지원 언어: 영어, 스페인어(ES & 419), 프랑스어(FR & CA), 독일어, 포르투갈어(BR & PT), 이탈리아어, 일본어, 한국어, 아랍어, 중국어 간체, 네덜란드어, 힌디어, 인도네시아어, 러시아어, 터키어, 폴란드어 등 40개 이상 — 모두 기기 내 번역으로 제공.

잉크 #173a38, 크림 #f8f1e4, 골드 #b88746 색상으로 정성껏 제작 — 차분하고 따뜻하며 프리미엄한 경험.

오늘의 작은 빛을 — 지금 LumaVerse를 무료로 시작하세요.
"""

full_desc["pt-PT"] = """Um pouco de luz, todos os dias. ✨
O mesmo versículo para todos, um momento de calma — juntos, em todo o mundo.

Porquê LumaVerse?
Num mundo ruidoso, o LumaVerse é silêncio por design. Sem feed infinito. Sem seguidores, sem mensagens. Apenas um versículo por dia, partilhado por todos, para refletir, respirar e recomeçar. Leia, guarde e leve consigo mesmo offline.

☀ UM VERSÍCULO, PARTILHADO TODOS OS DIAS
• O mesmo versículo para todos, todos os dias — um ritmo comum
• Da World English Bible (WEB) — domínio público, grátis para sempre
• Leitura bela, sem distrações, em creme, tinta e dourado

♡ JORNADA PRIVADA
• Os seus gostos e partilhas ficam privados — só para si
• Guarde favoritos e revisite a sua luz ao longo do tempo
• Sem contadores públicos, sem pressão

✦ COMUNIDADE GENEROSA
• Partilhe uma breve reflexão se quiser — a gentileza é obrigatória
• Moderação no dispositivo mantém conversas suaves e respeitosas
• Sem feed para percorrer, sem algoritmo para perseguir

🌍 MAIS DE 60 IDIOMAS, NO DISPOSITIVO
• Tradução instantânea via ML Kit no dispositivo — modelos ~30MB, funciona offline após descarregar
• Espanhol, francês, alemão, português, italiano, japonês, coreano, árabe, chinês e 50+ outros
• Texto original WEB sempre acessível

🔔 LEMBRETE DIÁRIO, AO SEU RITMO
• Notificação diária suave à hora que escolher — nunca spam
• Offline primeiro: leia a qualquer momento, mesmo sem sinal
• Comece a manhã com luz

🛡 PRIVADO E SEGURO POR CONCEÇÃO
• Feito com Firebase Auth & Firestore — dados mínimos, respeito máximo
• Não vendemos dados pessoais. A sua jornada é sua.
• Pronto para RGPD com fluxo de consentimento Google UMP para EEE/RU
• Nativo em Kotlin + Jetpack Compose — rápido, moderno, bonito e offline

— Contém anúncios —
LumaVerse é grátis para sempre sem compras na app. Para continuar gratuito exibimos anúncios banner e intersticiais via Google AdMob. Os anúncios nunca cobrem o seu versículo e os intersticiais são raros e dispensáveis. No EEE/RU, o consentimento é solicitado via UMP.

Idiomas: inglês, espanhol (ES e 419), francês (FR e CA), alemão, português (BR e PT), italiano, japonês, coreano, árabe, chinês simplificado, neerlandês, hindi, indonésio, russo, turco, polaco e 40+ outros via tradução no dispositivo.

Feito com cuidado em tinta #173a38, creme #f8f1e4 e dourado #b88746 — calmo, acolhedor e premium.

Um pouco de luz para o seu dia — comece hoje. Descarregue LumaVerse grátis.
"""

full_desc["es-419"] = """Un poco de luz, cada día. ✨
Un mismo versículo para todos, un momento de calma — juntos en todo el mundo.

¿Por qué LumaVerse?
En un mundo ruidoso, LumaVerse es calma por diseño. Sin feed infinito. Sin seguidores, sin mensajes. Solo un versículo al día, compartido por todos, para reflexionar, respirar y volver a empezar. Léelo, guárdalo y llévalo contigo aun sin conexión.

☀ UN VERSÍCULO, COMPARTIDO CADA DÍA
• El mismo versículo para todos, cada día — un ritmo en común
• De la World English Bible (WEB) — dominio público, gratis para siempre
• Lectura hermosa, sin distracciones, en crema, tinta y dorado

♡ TU CAMINO PRIVADO
• Tus me gusta y compartidos quedan privados — solo para ti
• Guarda favoritos y vuelve a tu luz cuando quieras
• Sin contadores públicos, sin presión

✦ COMUNIDAD CON GRACIA
• Comparte una breve reflexión si deseas — la bondad es requisito
• Moderación en el dispositivo mantiene charlas suaves y respetuosas
• Sin muro para scrollear, sin algoritmo que perseguir

🌍 MÁS DE 60 IDIOMAS, EN EL DISPOSITIVO
• Traducción instantánea con ML Kit en el dispositivo — modelos ~30MB, funciona sin internet tras descargar
• Español, francés, alemán, portugués, italiano, japonés, coreano, árabe, chino y 50+ más
• Texto original WEB siempre disponible

🔔 RECORDATORIO DIARIO, A TU RITMO
• Notificación diaria suave a la hora que elijas — nunca spam
• Sin conexión primero: lee cuando quieras, aun sin señal
• Empieza tu mañana con luz

🛡 PRIVADO Y SEGURO POR DISEÑO
• Hecho con Firebase Auth y Firestore — datos mínimos, respeto máximo
• No vendemos datos personales. Tu camino es tuyo.
• Listo para GDPR con consentimiento Google UMP para EEE/RU
• Nativo en Kotlin + Jetpack Compose — rápido, moderno, hermoso y offline

— Contiene anuncios —
LumaVerse es gratis para siempre sin compras dentro de la app. Para mantenerse gratis muestra anuncios banner e intersticiales vía Google AdMob. Los anuncios nunca cubren tu versículo y los intersticiales son poco frecuentes y se pueden cerrar. En EEE/RU se solicita consentimiento vía UMP.

Idiomas: inglés, español (ES y 419), francés (FR y CA), alemán, portugués (BR y PT), italiano, japonés, coreano, árabe, chino simplificado, holandés, hindi, indonesio, ruso, turco, polaco y 40+ más con traducción en el dispositivo.

Diseñado con cuidado en tinta #173a38, crema #f8f1e4 y dorado #b88746 — tranquilo, cálido y premium.

Un poco de luz para tu día — empieza hoy. Descarga LumaVerse gratis.
"""

full_desc["en-GB"] = """A little light, every day. ✨
One shared Bible verse, one calm moment — together, worldwide.

Why LumaVerse?
In a noisy world, LumaVerse is quiet by design. No endless feed. No followers, no DMs. Just one carefully shared verse each day to reflect, breathe, and begin again. Read, save, and carry it offline — wherever you are.

☀ ONE VERSE, SHARED DAILY
• The same verse for everyone, every day — a shared rhythm
• From the World English Bible (WEB) — public domain, free for ever
• Beautiful, distraction-free reading in cream, ink & gold

♡ PRIVATE JOURNEY
• Your likes and shares stay private — only for you
• Save favourites and revisit your light over time
• No public counts, no pressure, no performance

✦ GRACIOUS COMMUNITY
• Share a short reflection if you wish — kindness is required
• On-device moderation helps keep conversation gentle and respectful
• No feed to scroll, no algorithm to chase

🌍 60+ LANGUAGES, ON-DEVICE
• Instant translation via on-device ML Kit — ~30MB models, works offline after download
• Spanish, French, German, Portuguese, Italian, Japanese, Korean, Arabic, Chinese and 50+ more
• Original WEB text always accessible alongside translation

🔔 DAILY REMINDER, YOUR RHYTHM
• Gentle daily notification at your chosen time — never spammy
• Offline-first: read any time, even without signal
• Start your morning with light

🛡 PRIVATE & SAFE BY DESIGN
• Built with Firebase Auth & Firestore — minimal data, maximum respect
• No selling of personal data. Your journey is yours.
• GDPR-ready with Google UMP consent flow for EEA/UK
• Native Kotlin + Jetpack Compose — fast, modern, beautiful, offline-capable

— Contains ads —
LumaVerse is free for ever with no in-app purchases. To keep it free, we show banner and interstitial ads via Google AdMob. Ads never cover your verse and interstitials are infrequent and dismissible. If you are in the EEA/UK, consent is requested via UMP.

Languages: English, Spanish (ES & 419), French (FR & CA), German, Portuguese (BR & PT), Italian, Japanese, Korean, Arabic, Chinese (Simplified), Dutch, Hindi, Indonesian, Russian, Turkish, Polish, Ukrainian, Thai, Vietnamese and 40+ more — all via on-device translation.

Made with care in ink #173a38, cream #f8f1e4 and gold #b88746 — calm, warm, premium.

A little light for your day — start today. Download LumaVerse free.
"""

full_desc["fr-CA"] = """Un peu de lumière, chaque jour. ✨
Un même verset partagé, un instant de calme — ensemble, partout dans le monde.

Pourquoi LumaVerse ?
Dans un monde bruyant, LumaVerse cultive le calme. Pas de fil infini. Pas d’abonnés, pas de messages privés. Juste un verset par jour, partagé par tous, pour réfléchir, respirer et recommencer. Lisez, enregistrez, emportez-le hors ligne.

☀ UN VERSET, PARTAGÉ CHAQUE JOUR
• Le même verset pour tous, chaque jour — un rythme commun
• Tiré de la World English Bible (WEB) — domaine public, gratuit pour toujours
• Lecture épurée, sans distraction, en crème, encre et or

♡ CHEMIN INTIME
• Vos likes et partages restent privés — pour vous seul
• Enregistrez vos favoris et retrouvez votre lumière au fil du temps
• Aucun compteur public, aucune pression

✦ COMMUNAUTÉ BIENVEILLANTE
• Partagez une courte réflexion si vous le souhaitez — la bienveillance est exigée
• Modération sur l’appareil pour des échanges doux et respectueux
• Pas de fil à faire défiler, pas d’algorithme

🌍 60+ LANGUES, SUR L’APPAREIL
• Traduction instantanée via ML Kit sur l’appareil — modèles ~30Mo, fonctionne hors ligne après téléchargement
• Espagnol, français, allemand, portugais, italien, japonais, coréen, arabe, chinois et 50+ autres
• Texte original WEB toujours accessible

🔔 RAPPEL QUOTIDIEN, À VOTRE RYTHME
• Notification quotidienne douce à l’heure choisie — jamais de pourriel
• Priorité hors ligne : lisez à tout moment, même sans réseau
• Commencez la matinée avec de la lumière

🛡 PRIVÉ ET SÛR PAR CONCEPTION
• Construit avec Firebase Auth & Firestore — données minimales, respect maximal
• Aucune vente de données personnelles. Votre chemin vous appartient.
• Conforme aux lois sur la vie privée avec flux de consentement Google UMP pour EEE/RU
• Natif Kotlin + Jetpack Compose — rapide, moderne, beau, hors ligne

— Contient des annonces —
LumaVerse est gratuit pour toujours sans achat intégré. Pour rester gratuit, nous affichons des annonces bannières et interstitielles via Google AdMob. Les annonces ne masquent jamais votre verset et les interstitiels sont rares et fermables. Dans l’EEE/RU, le consentement est demandé via UMP.

Langues : anglais, espagnol (ES & 419), français (FR & CA), allemand, portugais (BR & PT), italien, japonais, coréen, arabe, chinois simplifié, néerlandais, hindi, indonésien, russe, turc, polonais et 40+ autres via traduction sur l’appareil.

Conçu avec soin en encre #173a38, crème #f8f1e4 et or #b88746 — calme, chaleureux, premium.

Un peu de lumière pour votre journée — commencez aujourd’hui. Téléchargez LumaVerse gratuitement.
"""

full_desc["ar"] = """نور صغير، كل يوم. ✨
آية واحدة يشاركها الجميع، لحظة هدوء — معاً حول العالم.

لماذا LumaVerse؟
في عالم صاخب، LumaVerse مصمم ليكون هادئاً. بلا خلاصة لا تنتهي. بلا متابعين، بلا رسائل خاصة. فقط آية واحدة مختارة بعناية كل يوم للتأمل والتنفس والبدء من جديد. اقرأها، احفظها، واحملها معك دون إنترنت — أينما كنت.

☀ آية واحدة، تُشارك يومياً
• نفس الآية للجميع كل يوم — إيقاع مشترك
• من World English Bible (WEB) — ملكية عامة، مجانية للأبد
• قراءة جميلة بلا تشتيت بألوان الكريم والحبر والذهب

♡ رحلة خاصة
• إعجاباتك ومشاركاتك تبقى خاصة — لك وحدك
• احفظ المفضلة وعد إلى نورك مع الوقت
• بلا عدادات علنية، بلا ضغط

✦ مجتمع كريم
• شارك تأملاً قصيراً إن رغبت — اللطف شرط أساسي
• إشراف على الجهاز يحافظ على حوار لطيف ومحترم
• بلا خلاصة للتمرير، بلا خوارزمية

🌍 أكثر من 60 لغة، على الجهاز
• ترجمة فورية عبر ML Kit على الجهاز — نماذج ~30MB، تعمل دون إنترنت بعد التنزيل
• الإسبانية، الفرنسية، الألمانية، البرتغالية، الإيطالية، اليابانية، الكورية، العربية، الصينية وأكثر من 50 لغة أخرى
• نص WEB الأصلي متاح دائماً بجانب الترجمة

🔔 تذكير يومي، بإيقاعك
• إشعار يومي لطيف في الوقت الذي تختاره — بلا إزعاج
• أولوية دون إنترنت: اقرأ في أي وقت حتى بلا إشارة
• ابدأ صباحك بنور

🛡 خاص وآمن بالتصميم
• مبني بـ Firebase Auth و Firestore — أقل بيانات، أقصى احترام
• لا نبيع البيانات الشخصية. رحلتك ملكك.
• جاهز لـ GDPR مع تدفق موافقة Google UMP لمنطقة EEA/UK
• أصلي بـ Kotlin + Jetpack Compose — سريع، عصري، جميل، يعمل دون إنترنت

— يحتوي على إعلانات —
LumaVerse مجاني للأبد بلا مشتريات داخل التطبيق. لإبقائه مجانياً نعرض إعلانات بانر وبينية عبر Google AdMob. الإعلانات لا تغطي آيتك أبداً والإعلانات البينية نادرة وقابلة للإغلاق. في EEA/UK يُطلب الموافقة عبر UMP.

اللغات: الإنجليزية، الإسبانية (ES و 419)، الفرنسية (FR و CA)، الألمانية، البرتغالية (BR و PT)، الإيطالية، اليابانية، الكورية، العربية، الصينية المبسطة، الهولندية، الهندية، الإندونيسية، الروسية، التركية، البولندية وأكثر من 40 أخرى عبر الترجمة على الجهاز.

مصنوع بعناية بألوان الحبر #173a38 والكريم #f8f1e4 والذهب #b88746 — هادئ، دافئ، راقٍ.

نور صغير ليومك — ابدأ اليوم. حمّل LumaVerse مجاناً.
"""

full_desc["zh-CN"] = """每天，一点微光。✨
同一节经文，同一份宁静 — 与全世界一起分享。

为什么选择 LumaVerse？
在喧嚣的世界里，LumaVerse 生来安静。没有无尽的信息流。没有粉丝，没有私信。每天只有一节精心挑选、与所有人共享的经文，让你默想、呼吸、重新出发。阅读、收藏，随时离线携带 — 无论身在何处。

☀ 每日一节，共享同行
• 每天所有人同一节经文 — 共同的节奏
• 来自 World English Bible (WEB) — 公有领域，永远免费
• 以奶油、墨色与金色呈现的纯净美观阅读体验

♡ 私密的旅程
• 你的点赞与分享仅自己可见 — 完全私密
• 收藏喜爱经文，随时重温你的光
• 没有公开计数，没有压力

✦ 恩慈的社区
• 愿意时分享一句简短感悟 — 以恩慈为准则
• 端侧审核守护温和、尊重的交流
• 没有信息流可刷，没有算法追逐

🌍 60+ 种语言，端侧翻译
• 通过端侧 ML Kit 即时翻译 — 约 30MB 模型，下载后可离线使用
• 西班牙语、法语、德语、葡萄牙语、意大利语、日语、韩语、阿拉伯语、中文等 50 多种
• 原版 WEB 文本始终可对照查看

🔔 每日提醒，按你的节奏
• 在你选择的时间轻柔提醒 — 绝不打扰
• 离线优先：即使无信号也能随时阅读
• 让清晨从光开始

🛡 以隐私与安全为设计之本
• 基于 Firebase Auth 与 Firestore — 最少数据，最大尊重
• 不出售个人数据。你的旅程只属于你。
• 已适配 GDPR，通过 Google UMP 在 EEA/英国地区请求授权
• 原生 Kotlin + Jetpack Compose 构建 — 快速、现代、美观、支持离线

— 包含广告 —
LumaVerse 永远免费，无应用内购买。为保持免费，我们通过 Google AdMob 展示横幅和插屏广告。广告绝不会遮挡你的经文，插屏出现频率低且可关闭。在 EEA/英国地区将通过 UMP 请求同意。

支持语言：英语、西班牙语（ES 与 419）、法语（FR 与 CA）、德语、葡萄牙语（BR 与 PT）、意大利语、日语、韩语、阿拉伯语、简体中文、荷兰语、印地语、印尼语、俄语、土耳其语、波兰语等 40 多种 — 均通过端侧翻译实现。

以墨色 #173a38、奶油 #f8f1e4 与金色 #b88746 精心打造 — 宁静、温暖、高级。

为你的一天点亮微光 — 今天就开始。免费下载 LumaVerse。
"""

release_note_text = {
 "en-US": "Welcome to LumaVerse — your daily light. One shared verse, private journey, gracious community. Bug fixes and calm improvements.",
 "es-ES": "Bienvenido a LumaVerse — tu luz diaria. Un versículo compartido, camino privado y comunidad bondadosa. Correcciones y mejoras serenas.",
 "fr-FR": "Bienvenue sur LumaVerse — votre lumière quotidienne. Un verset partagé, chemin intime et communauté bienveillante. Corrections et améliorations paisibles.",
 "de-DE": "Willkommen bei LumaVerse — dein tägliches Licht. Ein gemeinsamer Vers, privater Weg, gütige Gemeinschaft. Fehlerbehebungen und ruhige Verbesserungen.",
 "pt-BR": "Bem-vindo ao LumaVerse — sua luz diária. Um versículo compartilhado, jornada privada e comunidade gentil. Correções e melhorias serenas.",
 "it-IT": "Benvenuto in LumaVerse — la tua luce quotidiana. Un versetto condiviso, percorso privato e comunità gentile. Correzioni e miglioramenti sereni.",
 "ja-JP": "LumaVerseへようこそ — あなたの毎日の光。ひとつの聖句、プライベートな歩み、思いやりのコミュニティ。バグ修正と穏やかな改善。",
 "ko-KR": "LumaVerse에 오신 것을 환영합니다 — 당신의 매일의 빛. 하나의 구절, 사적인 여정, 은혜로운 공동체. 버그 수정 및 차분한 개선.",
 "pt-PT": "Bem-vindo ao LumaVerse — a sua luz diária. Um versículo partilhado, jornada privada e comunidade generosa. Correções e melhorias serenas.",
 "es-419": "Bienvenido a LumaVerse — tu luz diaria. Un versículo compartido, camino privado y comunidad bondadosa. Correcciones y mejoras serenas.",
 "en-GB": "Welcome to LumaVerse — your daily light. One shared verse, private journey, gracious community. Bug fixes and calm improvements.",
 "fr-CA": "Bienvenue sur LumaVerse — votre lumière quotidienne. Un verset partagé, chemin intime et communauté bienveillante. Corrections et améliorations paisibles.",
 "ar": "مرحباً بك في LumaVerse — نورك اليومي. آية مشتركة، رحلة خاصة ومجتمع كريم. إصلاحات وتحسينات هادئة.",
 "zh-CN": "欢迎使用 LumaVerse — 你的每日微光。一节共享经文、私密旅程、恩慈社区。问题修复与平静改进。",
}

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(content.strip() + "\n")

for loc in locales:
    title = titles[loc]
    short = short_desc[loc]
    full = full_desc[loc]
    video = video_url
    # Validate lengths
    if len(title) > 30:
        print(f"WARNING title too long {loc}: {len(title)} '{title}'")
    if len(short) > 80:
        print(f"WARNING short too long {loc}: {len(short)} '{short}'")
    if len(full) > 4000:
        print(f"WARNING full too long {loc}: {len(full)}")
    # fastlane
    write_file(os.path.join(fastlane_base, loc, "title.txt"), title)
    write_file(os.path.join(fastlane_base, loc, "short_description.txt"), short)
    write_file(os.path.join(fastlane_base, loc, "full_description.txt"), full)
    write_file(os.path.join(fastlane_base, loc, "video.txt"), video)
    # play listings mirror
    write_file(os.path.join(play_base, loc, "title.txt"), title)
    write_file(os.path.join(play_base, loc, "short_description.txt"), short)
    write_file(os.path.join(play_base, loc, "full_description.txt"), full)
    write_file(os.path.join(play_base, loc, "video.txt"), video)
    # release notes
    rn = release_note_text.get(loc, release_note_text["en-US"])
    write_file(os.path.join(release_notes_base, loc, "default.txt"), rn)

# changelog for en-US
write_file(os.path.join(fastlane_base, "en-US", "changelogs", "1.txt"), release_note_text["en-US"])

print("Done. Locales:", len(locales))
for loc in locales:
    print(loc, "title len", len(titles[loc]), "short len", len(short_desc[loc]), "full len", len(full_desc[loc]))
