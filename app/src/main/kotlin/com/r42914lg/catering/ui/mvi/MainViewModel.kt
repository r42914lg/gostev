package com.r42914lg.catering.ui.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.r42914lg.catering.core.data.CalendarDataSource
import kotlinx.coroutines.flow.StateFlow

@Stable
interface MainStateHolder {
    val screenState: StateFlow<ScreenState>
}

class MainViewModel(
    private val calendarDataSource: CalendarDataSource,
): ViewModel(), MainStateHolder {
    override val screenState: StateFlow<ScreenState> = TODO()
}