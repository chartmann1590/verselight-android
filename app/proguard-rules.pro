-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn org.conscrypt.**
-dontwarn com.google.android.gms.**

# Firestore deserializes these via reflection (toObject()); R8 strips their
# fields as unused otherwise, crashing with "No properties to serialize found".
-keep class com.chartmann1590.verselight.model.** { *; }
-keepclassmembers class com.chartmann1590.verselight.model.** { *; }
