# ProGuard / R8 Rules for ADI-OS (Titan CMD)

# --- Room Database Rules ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.example.data.model.** { *; }
-keep class com.example.data.dao.** { *; }

# --- Moshi & JSON Serialization Rules ---
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonClass <methods>;
}
-keep class com.example.ai.Gemini** { *; }
-keep class com.example.data.model.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter
-dontwarn com.squareup.moshi.**

# --- Retrofit & OkHttp Rules ---
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# --- Kotlin Coroutines Rules ---
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# --- Firebase Rules ---
-keepattributes *Annotation*, Signature
-keepclassmembers class * {
  @com.google.firebase.database.PropertyName <fields>;
  @com.google.firebase.firestore.PropertyName <fields>;
}

# --- WorkManager Rules ---
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# --- Jetpack Compose Rules ---
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
