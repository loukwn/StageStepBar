package com.loukwn.stagestepbar.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

internal class StageStepBarLifecycleObserver: LifecycleObserver {

    private var doOnStart: (() -> Unit)? = null
    private var doOnStop: (() -> Unit)? = null

    fun bindToLifecycleOwner(
        lifecycleOwner: LifecycleOwner,
        doOnStart: () -> Unit,
        doOnStop: () -> Unit,
    ) {
        lifecycleOwner.lifecycle.addObserver(this)
        this.doOnStart = doOnStart
        this.doOnStop = doOnStop
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        doOnStart?.invoke()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        doOnStop?.invoke()
    }
}