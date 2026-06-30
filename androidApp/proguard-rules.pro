# Mantener anotaciones
-keepattributes *Annotation*

# Mantener información de Kotlin
-keep class kotlin.Metadata { *; }

# Kotlin Serialization
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# No mostrar advertencias
-dontwarn kotlinx.serialization.**
-dontwarn org.jetbrains.annotations.**