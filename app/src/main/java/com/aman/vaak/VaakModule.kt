package com.aman.vaak

import android.app.NotificationManager
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aman.vaak.managers.ClipboardManager
import com.aman.vaak.managers.ClipboardManagerImpl
import com.aman.vaak.managers.DictationManager
import com.aman.vaak.managers.DictationManagerImpl
import com.aman.vaak.managers.FileManager
import com.aman.vaak.managers.FileManagerImpl
import com.aman.vaak.managers.KeyboardManager
import com.aman.vaak.managers.KeyboardManagerImpl
import com.aman.vaak.managers.NotifyManager
import com.aman.vaak.managers.NotifyManagerImpl
import com.aman.vaak.managers.PromptsManager
import com.aman.vaak.managers.PromptsManagerImpl
import com.aman.vaak.managers.SettingsManager
import com.aman.vaak.managers.SettingsManagerImpl
import com.aman.vaak.managers.SystemManager
import com.aman.vaak.managers.SystemManagerImpl
import com.aman.vaak.managers.TextManager
import com.aman.vaak.managers.TextManagerImpl
import com.aman.vaak.managers.VoiceManager
import com.aman.vaak.managers.VoiceManagerImpl
import com.aman.vaak.managers.WhisperManager
import com.aman.vaak.managers.WhisperManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
object VaakModule {
    @Provides
    @Singleton
    fun provideAndroidClipboardManager(
        @ApplicationContext context: Context,
    ): android.content.ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    @Provides
    @Singleton
    fun provideInputMethodManager(
        @ApplicationContext context: Context,
    ): InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    @Singleton
    fun provideSystemManager(
        @ApplicationContext context: Context,
    ): SystemManager = SystemManagerImpl(context)

    @Provides
    @Singleton
    fun provideClipboardManager(): ClipboardManager = ClipboardManagerImpl()

    @Provides
    @Singleton
    fun provideKeyboardManager(
        @ApplicationContext context: Context,
        inputMethodManager: InputMethodManager,
    ): KeyboardManager =
        KeyboardManagerImpl(
            packageName = context.packageName,
            inputMethodManager = inputMethodManager,
            contentResolver = context.contentResolver,
        )

    @Provides
    @Singleton
    fun provideSettingsManager(
        @ApplicationContext context: Context,
    ): SettingsManager = SettingsManagerImpl(context)

    @Provides @Singleton
    fun provideTextManager(scope: CoroutineScope): TextManager = TextManagerImpl(scope)

    @Provides
    @Singleton
    fun provideVoiceManager(
        systemManager: SystemManager,
        fileManager: FileManager,
        scope: CoroutineScope,
    ): VoiceManager = VoiceManagerImpl(systemManager, fileManager, scope)

    @Provides
    fun provideScope(): CoroutineScope = MainScope()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providePromptsManager(
        fileManager: FileManager,
        moshi: Moshi,
        scope: CoroutineScope,
    ): PromptsManager = PromptsManagerImpl(fileManager, moshi, scope)

    @Provides
    @Singleton
    fun provideFileManager(
        @ApplicationContext context: Context,
    ): FileManager = FileManagerImpl(context)

    @Provides
    @Singleton
    fun provideDictationManager(
        voiceManager: VoiceManager,
        whisperManager: WhisperManager,
        fileManager: FileManager,
        scope: CoroutineScope,
    ): DictationManager = DictationManagerImpl(voiceManager, whisperManager, fileManager, scope)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class VaakOpenAI

    @Provides
    @VaakOpenAI
    fun provideOpenAI(settingsManager: SettingsManager): OpenAI {
        return OpenAI(
            token = settingsManager.getApiKey() ?: "",
            timeout = Timeout(socket = 60.seconds),
        )
    }

    @Provides
    @Singleton
    fun provideWhisperManager(
        settingsManager: SettingsManager,
        fileManager: FileManager,
        @VaakOpenAI openAIProvider: Provider<OpenAI>,
    ): WhisperManager = WhisperManagerImpl(settingsManager, fileManager, openAIProvider)

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideNotifyManager(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager,
        systemManager: SystemManager,
    ): NotifyManager = NotifyManagerImpl(context, notificationManager, systemManager)

    @Provides
    @Singleton
    fun provideWindowManager(
        @ApplicationContext context: Context,
    ): WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}
