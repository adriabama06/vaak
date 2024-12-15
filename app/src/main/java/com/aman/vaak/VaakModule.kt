package com.aman.vaak

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.aman.vaak.managers.ClipboardManagerImpl
import com.aman.vaak.managers.ClipboardManager
import com.aman.vaak.managers.SystemManager
import com.aman.vaak.managers.SystemManagerImpl
import com.aman.vaak.managers.KeyboardSetupManager
import com.aman.vaak.managers.KeyboardSetupManagerImpl
import com.aman.vaak.repositories.ClipboardRepository
import com.aman.vaak.repositories.ClipboardRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VaakModule {
    @Provides
    @Singleton
    fun provideAndroidClipboardManager(
        @ApplicationContext context: Context
    ): android.content.ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    @Provides
    @Singleton
    fun provideInputMethodManager(
        @ApplicationContext context: Context
    ): InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    @Singleton
    fun provideSystemManager(
        @ApplicationContext context: Context
    ): SystemManager = SystemManagerImpl(context.contentResolver)

    @Provides
    @Singleton
    fun provideClipboardRepository(
        clipboardManager: android.content.ClipboardManager
    ): ClipboardRepository = ClipboardRepositoryImpl(clipboardManager)

    @Provides
    @Singleton
    fun provideClipboardManager(
        repository: ClipboardRepository
    ): ClipboardManager = ClipboardManagerImpl(repository)

    @Provides
    @Singleton
    fun provideKeyboardSetupManager(
        @ApplicationContext context: Context,
        inputMethodManager: InputMethodManager,
        systemManager: SystemManager
    ): KeyboardSetupManager = KeyboardSetupManagerImpl(
        packageName = context.packageName,
        inputMethodManager = inputMethodManager,
        systemManager = systemManager
    )
}
