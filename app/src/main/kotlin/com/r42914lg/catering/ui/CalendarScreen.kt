package com.r42914lg.catering.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.r42914lg.catering.mvi.CalendarDay
import com.r42914lg.catering.mvi.EventDot
import com.r42914lg.catering.mvi.MainStateHolder
import com.r42914lg.catering.mvi.MainViewModel
import com.r42914lg.catering.mvi.ScreenEvent
import com.r42914lg.catering.mvi.ScreenState
import com.r42914lg.catering.theme.*
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarScreen(
    stateHolder: MainStateHolder = koinViewModel<MainViewModel>(),
    modifier: Modifier = Modifier
) {
    val state by stateHolder.screenState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Paper,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Ink)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Paper
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            MonthSelector(
                monthTitle = state.monthTitle,
                yearTitle = state.yearTitle,
                onPrevClick = { stateHolder.onScreenAction(ScreenEvent.PreviousMonthClicked) },
                onNextClick = { stateHolder.onScreenAction(ScreenEvent.NextMonthClicked) }
            )

            WeekdayHeader()

            CalendarGrid(
                days = state.days,
                onDateSelected = { stateHolder.onScreenAction(ScreenEvent.DateSelected(it)) }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MonthSelector(
    monthTitle: String,
    yearTitle: String,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevClick) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev", tint = Bronze)
        }
        
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = monthTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Ink,
                letterSpacing = (-0.01).sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = yearTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = Muted,
                modifier = Modifier.padding(bottom = 0.dp)
            )
        }

        IconButton(onClick = onNextClick) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = Bronze)
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .drawBehind {
                drawLine(
                    color = Hairline,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        weekdays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 10.dp),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Muted
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    days: List<CalendarDay>,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val rows = days.chunked(7)
        rows.forEach { rowDays ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowDays.forEach { day ->
                    DayCell(
                        day = day,
                        onClick = { if (day.isCurrentMonth) onDateSelected(day.date) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(0.8f)
            .padding(top = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = day.isCurrentMonth,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val dayNumColor = when {
            day.isToday -> Paper
            !day.isCurrentMonth -> OtherMonthDay
            else -> Ink
        }

        val backgroundModifier = if (day.isToday) {
            Modifier.background(Pine, CircleShape)
        } else Modifier

        val borderModifier = if (day.isSelected && !day.isToday) {
            Modifier.border(1.5.dp, Pine, CircleShape)
        } else Modifier

        Box(
            modifier = Modifier
                .size(32.dp)
                .then(borderModifier)
                .then(backgroundModifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = 14.sp,
                color = dayNumColor,
                fontWeight = if (day.isToday) FontWeight.Medium else FontWeight.Normal
            )
        }

        if (day.isCurrentMonth) {
            EventDots(dots = day.dots)
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EventDots(dots: List<EventDot>) {
    Row(
        modifier = Modifier
            .height(12.dp)
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val maxDots = 3
        val displayedDots = dots.take(maxDots)
        
        displayedDots.forEach { dot ->
            val color = if (dot.status == EventDot.Status.CONFIRMED) Pine else Brick
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(color, CircleShape)
            )
        }
        
        if (dots.size > maxDots) {
            Text(
                text = "+${dots.size - maxDots}",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = Muted,
                lineHeight = 1.sp
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun CalendarScreenPreview() {
    val mockState = remember {
        ScreenState(
            monthTitle = "September",
            yearTitle = "2026",
            days = (0 until 42).map { i ->
                val date = LocalDate(2026, 9, 1).plus(DatePeriod(days = i - 2))
                CalendarDay(
                    date = date,
                    isCurrentMonth = date.month == Month.SEPTEMBER,
                    isToday = date.year == 2026 && date.month == Month.SEPTEMBER && date.dayOfMonth == 8,
                    isSelected = date.year == 2026 && date.month == Month.SEPTEMBER && date.dayOfMonth == 10,
                    dots = if (i % 3 == 0) listOf(
                        EventDot(EventDot.Status.CONFIRMED, 1L),
                        EventDot(EventDot.Status.APPLIED, 2L),
                        EventDot(EventDot.Status.NOT_REGISTERED, 3L),
                        EventDot(EventDot.Status.NOT_REGISTERED, 4L)
                    ) else emptyList()
                )
            }
        )
    }
    val mockStateHolder = remember {
        object : MainStateHolder {
            override val screenState = MutableStateFlow(mockState).asStateFlow()
            override fun onScreenAction(event: ScreenEvent) {}
        }
    }
    CateringTheme {
        CalendarScreen(stateHolder = mockStateHolder)
    }
}
