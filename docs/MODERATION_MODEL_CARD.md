# VerseLight on-device moderation model card

## Purpose

VerseLight uses two on-device layers before a public comment can be submitted:

1. An always-available compact linear classifier over normalized token and phrase features.
2. A Gemini Nano classification prompt through Android AICore when the device reports that feature as available.

No draft comment is sent to VerseLight, Firebase, Cloudflare, or a remote moderation API. The network write begins only after moderation allows the comment and the user taps **Post**.

## Labels and policy

The classifier covers threats, hate/dehumanization, targeted harassment, sexual abuse or exploitation, and strong profanity. It is deliberately tuned to allow respectful disagreement and ordinary discussion of difficult biblical subjects.

Unicode NFKC normalization, repeated-character folding, common leetspeak substitutions, and punctuation-separated token handling run before feature scoring. A score of `0.72` or higher blocks the comment. Gemini Nano is instructed to use the same labels and policy.

## Limitations

- Version 1 is English-first.
- Contextual language, quotations, reclaimed language, and obfuscations can cause false positives or false negatives.
- A modified client can bypass local checks, so Firestore authorization, community reporting, automatic threshold hiding, and human review remain required.
- On devices without compatible AICore/Gemini Nano, the embedded classifier remains active.

Regression tests must include benign disagreement, direct threats, strong profanity, and common obfuscations before changing features or thresholds.

