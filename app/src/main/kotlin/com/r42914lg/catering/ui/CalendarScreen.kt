package com.r42914lg.catering.ui

import android.app.Activity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.r42914lg.catering.BuildConfig
import com.r42914lg.catering.auth.AuthContent
import com.r42914lg.catering.banners.BannersListView
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.secret.SecretPanel
import com.r42914lg.catering.details.DetailsContent
import com.r42914lg.catering.mvi.*
import com.r42914lg.catering.theme.*
import com.r42914lg.catering.utils.CollectEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarScreen(
    modifier: Modifier = Modifier,
    stateHolder: MainStateHolder = koinViewModel<MainViewModel>(),
) {
    val state by stateHolder.screenState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheetForDay: LocalDate? by remember { mutableStateOf(null) }
    var selectedDayEvents by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val rightDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var statusChanged by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (state.isUpdateRequired) {
        AlertDialog(
            onDismissRequest = { /* no-op - blocking modal */ },
            confirmButton = {
                Button(onClick = { (context as? Activity)?.finish() }) {
                    Text("OK")
                }
            },
            title = { Text("Update Required") },
            text = { Text("Update to latest version. Application will be closed") },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    CollectEvents(stateHolder.effects) { effect ->
        when (effect) {
            is MainEffect.OpenEventDetails -> {
                selectedDayEvents = listOf(effect.event)
                showBottomSheetForDay = effect.event.date
            }
            is MainEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = rightDrawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerContainerColor = Paper,
                        drawerShape = RoundedCornerShape(0.dp),
                        modifier = Modifier.width(284.dp)
                    ) {
                        SecretPanel()
                    }
                }
            },
            gesturesEnabled = true
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = Paper,
                            drawerShape = RoundedCornerShape(0.dp),
                            modifier = Modifier.width(284.dp)
                        ) {
                            AuthContent()
                        }
                    },
                    gesturesEnabled = true
                ) {
                    Scaffold(
                        modifier = modifier.fillMaxSize(),
                        containerColor = Paper,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text("Calendar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink) },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Ink)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Paper
                                )
                            )
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            PullToRefreshBox(
                                isRefreshing = state.isLoading,
                                onRefresh = { stateHolder.onScreenAction(ScreenEvent.RefreshRequested()) },
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (state.hasBanners) {
                                        BannersListView(
                                            banners = state.banners,
                                            baseUrl = state.bannersBaseUrl,
                                            onBannerClick = { eventId ->
                                                stateHolder.onScreenAction(ScreenEvent.BannerClicked(eventId))
                                            },
                                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                        )
                                    }
                                    MonthSelector(
                                        monthTitle = state.monthTitle,
                                        yearTitle = state.yearTitle,
                                        onPrevClick = { stateHolder.onScreenAction(ScreenEvent.PreviousMonthClicked) },
                                        onNextClick = { stateHolder.onScreenAction(ScreenEvent.NextMonthClicked) }
                                    )
                                    WeekdayHeader()
                                    CalendarGrid(
                                        days = state.days,
                                        onDateSelected = { date ->
                                            val day = state.days.find { it.date == date }
                                            if (day != null && day.events.isNotEmpty()) {
                                                selectedDayEvents = day.events
                                                showBottomSheetForDay = day.date
                                            }
                                            stateHolder.onScreenAction(ScreenEvent.DateSelected(date))
                                        }
                                    )

                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                if (BuildConfig.DEBUG) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .width(15.dp)
                                            .height(50.dp)
                                            .pointerInput(Unit) {
                                                detectTapGestures(
                                                    onPress = {
                                                        val job = scope.launch {
                                                            delay(5.seconds)
                                                            rightDrawerState.open()
                                                        }
                                                        try {
                                                            awaitRelease()
                                                        } finally {
                                                            job.cancel()
                                                        }
                                                    }
                                                )
                                            }
                                    )
                                }
                            }
                        }
                    }

                    showBottomSheetForDay?.let {
                        ModalBottomSheet(
                            onDismissRequest = {
                                if (statusChanged) {
                                    statusChanged = false
                                    stateHolder.onScreenAction(
                                        ScreenEvent.RefreshRequested(false)
                                    )
                                }
                                showBottomSheetForDay = null
                            },
                            sheetState = sheetState,
                            containerColor = Paper,
                            dragHandle = null
                        ) {
                        DetailsContent(
                            day = it,
                            events = selectedDayEvents,
                            onAuthorizeClick = {
                                showBottomSheetForDay = null
                                scope.launch { drawerState.open() }
                            },
                            onStatusChanged = {
                                statusChanged = true
                            }
                        )
                        }
                    }
                }
            }
        }
    }

    if (state.isUpdateRequired) {
        AlertDialog(
            onDismissRequest = { /* no-op - blocking modal */ },
            confirmButton = {
                Button(onClick = { (context as? Activity)?.finish() }) {
                    Text("OK")
                }
            },
            title = { Text("Update Required") },
            text = { Text("Update to latest version. Application will be closed") },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}
