# Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
# Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).

# Mantener modelos serializables
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Compose
-dontwarn androidx.compose.**

# Glance
-keep class androidx.glance.** { *; }