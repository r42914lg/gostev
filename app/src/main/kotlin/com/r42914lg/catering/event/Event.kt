package com.r42914lg.catering.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

fun <T> eventFlow(
    buffer: Int = 5,
    replay: Int = 1
) = MutableSharedFlow<Event<T>>(
    extraBufferCapacity = buffer,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
    replay = replay
)

@Composable
inline fun <T> CollectEvents(
    eventFlow: Flow<Event<T>?>,
    repeatLifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (value: T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(eventFlow) {
        lifecycleOwner.repeatOnLifecycle(repeatLifecycleState) {
            eventFlow.collect {
                it?.getContentIfNotHandled()?.let { event -> action(event) }
            }
        }
    }
}