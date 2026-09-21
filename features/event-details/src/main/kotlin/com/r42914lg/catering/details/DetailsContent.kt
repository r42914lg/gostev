package com.r42914lg.catering.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.designsys.CateringTheme
import com.r42914lg.catering.utils.CollectEvents
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailsContent(
    day: LocalDate,
    events: List<CalendarEvent>,
    onAuthorizeClick: () -> Unit,
    onStatusChanged: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel(key = day.toString()) { parametersOf(events) }
) {
    val state by viewModel.state.collectAsState()

    CollectEvents(viewModel.effects) { effect ->
        when (effect) {
            DetailsEffect.RegistrationStatusChanged -> {
                onStatusChanged()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CateringTheme.colors.background)
            .padding(horizontal = CateringTheme.spacing.xl, vertical = CateringTheme.spacing.m),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(CateringTheme.spacing.xs)
                .clip(CircleShape)
                .background(CateringTheme.colors.divider)
        )
        Spacer(modifier = Modifier.height(CateringTheme.spacing.l))
        state.event?.let { e ->
            Text(
                text = "${e.date.dayOfWeek.name}, ${e.date.month.name} ${e.date.dayOfMonth}".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CateringTheme.colors.muted,
                letterSpacing = 0.04.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (state.total > 1) {
                IconButton(
                    onClick = { viewModel.onAction(DetailsAction.PrevClicked) },
                    enabled = state.index > 0
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Prev",
                        tint = if (state.index > 0) CateringTheme.colors.accent else Color.Transparent
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.total > 1) {
                    Text(
                        text = "Event ${state.index + 1} of ${state.total}",
                        fontSize = 11.sp,
                        color = CateringTheme.colors.muted
                    )
                }
                Text(
                    text = state.event?.name ?: "",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = CateringTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }

            if (state.total > 1) {
                IconButton(
                    onClick = { viewModel.onAction(DetailsAction.NextClicked) },
                    enabled = state.index < state.total - 1
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = if (state.index < state.total - 1) CateringTheme.colors.accent else Color.Transparent
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(CateringTheme.spacing.m))

        if (state.isAuthorized) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (state.status == DetailsState.Status.CONFIRMED) CateringTheme.colors.brand else CateringTheme.colors.error)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = when (state.status) {
                        DetailsState.Status.CONFIRMED -> "Confirmed"
                        DetailsState.Status.APPLIED -> "Applied"
                        DetailsState.Status.NOT_REGISTERED -> "Not Registered"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CateringTheme.colors.muted
                )
            }

            if (state.status == DetailsState.Status.NOT_REGISTERED && state.availableSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(CateringTheme.spacing.xl))
                Text(
                    text = "Skills required for this event:".uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CateringTheme.colors.accent,
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 0.05.sp
                )
                Spacer(modifier = Modifier.height(CateringTheme.spacing.s))
                state.availableSkills.forEach { skill ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onAction(DetailsAction.SkillToggled(skill.id)) }
                            .padding(vertical = CateringTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.selectedSkillIds.contains(skill.id),
                            onCheckedChange = { viewModel.onAction(DetailsAction.SkillToggled(skill.id)) },
                            colors = CheckboxDefaults.colors(checkedColor = CateringTheme.colors.brand)
                        )
                        Text(
                            text = skill.name,
                            fontSize = 14.sp,
                            color = CateringTheme.colors.onBackground,
                            modifier = Modifier.padding(start = CateringTheme.spacing.s)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(CateringTheme.spacing.xl))
        HorizontalDivider(color = CateringTheme.colors.divider)
        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (!state.isAuthorized) {
                    onAuthorizeClick()
                } else {
                    viewModel.onAction(
                        if (state.status == DetailsState.Status.NOT_REGISTERED) 
                            DetailsAction.ApplyClicked 
                        else 
                            DetailsAction.CancelClicked
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = CateringTheme.shapes.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!state.isAuthorized || state.status == DetailsState.Status.NOT_REGISTERED) CateringTheme.colors.brand else Color.Transparent,
                contentColor = if (!state.isAuthorized || state.status == DetailsState.Status.NOT_REGISTERED) CateringTheme.colors.background else CateringTheme.colors.error
            ),
            border = if (state.isAuthorized && state.status != DetailsState.Status.NOT_REGISTERED)
                BorderStroke(1.5.dp, CateringTheme.colors.error)
            else null,
            contentPadding = PaddingValues(0.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CateringTheme.colors.background)
            } else {
                Text(
                    text = when {
                        !state.isAuthorized -> "Authorize to apply for event"
                        state.status == DetailsState.Status.NOT_REGISTERED -> "Apply"
                        else -> "Cancel registration"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(18.dp))
    }
}
