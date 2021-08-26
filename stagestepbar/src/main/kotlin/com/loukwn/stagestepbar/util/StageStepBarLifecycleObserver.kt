package com.loukwn.stagestepbar.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

internal class StageStepBarLifecycleObserver: LifecycleObserver {

    private var listener: Listener? = null

    fun bindToLifecycleOwner(lifecycleOwner: LifecycleOwner, listener: Listener) {
        lifecycleOwner.lifecycle.addObserver(this)
        this.listener = listener
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        listener?.onLifecycleStart()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        listener?.onLifecycleStop()
    }

    internal interface Listener {
        fun onLifecycleStart()
        fun onLifecycleStop()
    }
}