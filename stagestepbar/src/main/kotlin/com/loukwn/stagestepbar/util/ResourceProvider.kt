package com.loukwn.stagestepbar.util

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

internal object ResourceProvider {
    @ColorInt
    fun provideColor(context: Context, @ColorRes colorRes: Int): Int =
        ContextCompat.getColor(context, colorRes)
}