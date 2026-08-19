package com.chartmann1590.verselight.data

import com.chartmann1590.verselight.model.DailyVerse
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DailyVerseRepository(private val clock: Clock = Clock.systemUTC()) {
    fun today(): DailyVerse = forDate(LocalDate.now(clock))

    fun forDate(date: LocalDate): DailyVerse {
        val entry = VERSES[Math.floorMod(date.toEpochDay().toInt(), VERSES.size)]
        return DailyVerse(
            dayKey = date.format(DateTimeFormatter.ISO_DATE),
            reference = entry.first,
            text = entry.second,
        )
    }

    companion object {
        fun utcTodayKey(): String = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE)

        private val VERSES = listOf(
            "Genesis 1:3" to "God said, “Let there be light,” and there was light.",
            "Exodus 14:14" to "Yahweh will fight for you, and you shall be still.",
            "Deuteronomy 31:8" to "Yahweh himself is who goes before you. He will be with you. He will not fail you nor forsake you. Don’t be afraid. Don’t be discouraged.",
            "Joshua 1:9" to "Haven’t I commanded you? Be strong and courageous. Don’t be afraid. Don’t be dismayed, for Yahweh your God is with you wherever you go.",
            "1 Samuel 16:7" to "Man looks at the outward appearance, but Yahweh looks at the heart.",
            "2 Samuel 22:29" to "For you are my lamp, Yahweh. Yahweh will light up my darkness.",
            "1 Chronicles 16:11" to "Seek Yahweh and his strength. Seek his face forever more.",
            "Nehemiah 8:10" to "Don’t be grieved, for the joy of Yahweh is your strength.",
            "Psalm 16:8" to "I have set Yahweh always before me. Because he is at my right hand, I shall not be moved.",
            "Psalm 19:14" to "Let the words of my mouth and the meditation of my heart be acceptable in your sight, Yahweh, my rock, and my redeemer.",
            "Psalm 23:1" to "Yahweh is my shepherd; I shall lack nothing.",
            "Psalm 27:1" to "Yahweh is my light and my salvation. Whom shall I fear? Yahweh is the strength of my life. Of whom shall I be afraid?",
            "Psalm 34:8" to "Oh taste and see that Yahweh is good. Blessed is the man who takes refuge in him.",
            "Psalm 37:4" to "Also delight yourself in Yahweh, and he will give you the desires of your heart.",
            "Psalm 46:1" to "God is our refuge and strength, a very present help in trouble.",
            "Psalm 51:10" to "Create in me a clean heart, O God. Renew a right spirit within me.",
            "Psalm 55:22" to "Cast your burden on Yahweh and he will sustain you. He will never allow the righteous to be moved.",
            "Psalm 56:3" to "When I am afraid, I will put my trust in you.",
            "Psalm 84:11" to "For Yahweh God is a sun and a shield. Yahweh will give grace and glory. He withholds no good thing from those who walk blamelessly.",
            "Psalm 90:14" to "Satisfy us in the morning with your loving kindness, that we may rejoice and be glad all our days.",
            "Psalm 119:105" to "Your word is a lamp to my feet, and a light for my path.",
            "Psalm 121:2" to "My help comes from Yahweh, who made heaven and earth.",
            "Psalm 139:14" to "I will give thanks to you, for I am fearfully and wonderfully made. Your works are wonderful. My soul knows that very well.",
            "Proverbs 3:5–6" to "Trust in Yahweh with all your heart, and don’t lean on your own understanding. In all your ways acknowledge him, and he will make your paths straight.",
            "Proverbs 4:23" to "Keep your heart with all diligence, for out of it is the wellspring of life.",
            "Proverbs 16:3" to "Commit your deeds to Yahweh, and your plans shall succeed.",
            "Ecclesiastes 3:1" to "For everything there is a season, and a time for every purpose under heaven.",
            "Isaiah 26:3" to "You will keep whoever’s mind is steadfast in perfect peace, because he trusts in you.",
            "Isaiah 40:31" to "But those who wait for Yahweh will renew their strength. They will mount up with wings like eagles. They will run and not be weary. They will walk and not faint.",
            "Isaiah 41:10" to "Don’t you be afraid, for I am with you. Don’t be dismayed, for I am your God. I will strengthen you. Yes, I will help you.",
            "Isaiah 43:2" to "When you pass through the waters, I will be with you, and through the rivers, they will not overflow you.",
            "Jeremiah 29:11" to "For I know the thoughts that I think toward you, says Yahweh, thoughts of peace, and not of evil, to give you hope and a future.",
            "Lamentations 3:22–23" to "It is because of Yahweh’s loving kindnesses that we are not consumed, because his compassion doesn’t fail. They are new every morning. Great is your faithfulness.",
            "Micah 6:8" to "He has shown you, O man, what is good. What does Yahweh require of you, but to act justly, to love mercy, and to walk humbly with your God?",
            "Matthew 5:14" to "You are the light of the world. A city located on a hill can’t be hidden.",
            "Matthew 6:33" to "But seek first God’s Kingdom and his righteousness; and all these things will be given to you as well.",
            "Matthew 11:28" to "Come to me, all you who labor and are heavily burdened, and I will give you rest.",
            "Matthew 22:37" to "You shall love the Lord your God with all your heart, with all your soul, and with all your mind.",
            "Mark 9:23" to "If you can believe, all things are possible to him who believes.",
            "Luke 1:37" to "For nothing spoken by God is impossible.",
            "Luke 6:31" to "As you would like people to do to you, do exactly so to them.",
            "John 1:5" to "The light shines in the darkness, and the darkness hasn’t overcome it.",
            "John 3:16" to "For God so loved the world, that he gave his only born Son, that whoever believes in him should not perish, but have eternal life.",
            "John 8:12" to "I am the light of the world. He who follows me will not walk in the darkness, but will have the light of life.",
            "John 13:34" to "A new commandment I give to you, that you love one another. Just as I have loved you, you also love one another.",
            "John 14:27" to "Peace I leave with you. My peace I give to you; not as the world gives, I give to you. Don’t let your heart be troubled, neither let it be fearful.",
            "John 15:5" to "I am the vine. You are the branches. He who remains in me and I in him bears much fruit, for apart from me you can do nothing.",
            "Romans 5:8" to "But God commends his own love toward us, in that while we were yet sinners, Christ died for us.",
            "Romans 8:28" to "We know that all things work together for good for those who love God, for those who are called according to his purpose.",
            "Romans 12:12" to "Rejoicing in hope; enduring in troubles; continuing steadfastly in prayer.",
            "Romans 15:13" to "Now may the God of hope fill you with all joy and peace in believing, that you may abound in hope in the power of the Holy Spirit.",
            "1 Corinthians 13:13" to "But now faith, hope, and love remain—these three. The greatest of these is love.",
            "1 Corinthians 16:14" to "Let all that you do be done in love.",
            "2 Corinthians 5:7" to "For we walk by faith, not by sight.",
            "2 Corinthians 12:9" to "My grace is sufficient for you, for my power is made perfect in weakness.",
            "Galatians 5:22–23" to "But the fruit of the Spirit is love, joy, peace, patience, kindness, goodness, faith, gentleness, and self-control.",
            "Galatians 6:9" to "Let’s not be weary in doing good, for we will reap in due season if we don’t give up.",
            "Ephesians 2:10" to "For we are his workmanship, created in Christ Jesus for good works, which God prepared before that we would walk in them.",
            "Ephesians 4:32" to "Be kind to one another, tenderhearted, forgiving each other, just as God also in Christ forgave you.",
            "Philippians 4:4" to "Rejoice in the Lord always! Again I will say, “Rejoice!”",
            "Philippians 4:6–7" to "In nothing be anxious, but in everything, by prayer and petition with thanksgiving, let your requests be made known to God. And the peace of God, which surpasses all understanding, will guard your hearts and your thoughts in Christ Jesus.",
            "Philippians 4:13" to "I can do all things through Christ, who strengthens me.",
            "Colossians 3:15" to "Let the peace of God rule in your hearts, to which also you were called in one body, and be thankful.",
            "1 Thessalonians 5:16–18" to "Always rejoice. Pray without ceasing. In everything give thanks, for this is the will of God in Christ Jesus toward you.",
            "2 Timothy 1:7" to "For God didn’t give us a spirit of fear, but of power, love, and self-control.",
            "Hebrews 11:1" to "Now faith is assurance of things hoped for, proof of things not seen.",
            "Hebrews 13:8" to "Jesus Christ is the same yesterday, today, and forever.",
            "James 1:5" to "But if any of you lacks wisdom, let him ask of God, who gives to all liberally and without reproach, and it will be given to him.",
            "1 Peter 5:7" to "Casting all your worries on him, because he cares for you.",
            "1 John 4:7" to "Beloved, let’s love one another, for love is of God; and everyone who loves has been born of God and knows God.",
            "1 John 4:19" to "We love him, because he first loved us.",
            "Revelation 21:4" to "He will wipe away from them every tear from their eyes. Death will be no more; neither will there be mourning, nor crying, nor pain any more."
        )
    }
}

