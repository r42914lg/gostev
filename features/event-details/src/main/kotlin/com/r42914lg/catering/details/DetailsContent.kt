package com.r42914lg.catering.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val Paper = Color(0xFFFAF9F6)
private val Ink = Color(0xFF171512)
private val Pine = Color(0xFF2E4B43)
private val PineLight = Color(0xFFE7EEEC)
private val Bronze = Color(0xFF8A6E4B)
private val Brick = Color(0xFFB23A2E)
private val Hairline = Color(0xFFE5E1D8)
private val Muted = Color(0xFF8A857C)

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

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DetailsEffect.RegistrationStatusChanged -> {
                    onStatusChanged()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Paper)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Hairline)
        )
        Spacer(modifier = Modifier.height(18.dp))
        state.event?.let { e ->
            Text(
                text = "${e.date.dayOfWeek.name}, ${e.date.month.name} ${e.date.dayOfMonth}".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Muted,
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
                        tint = if (state.index > 0) Bronze else Color.Transparent
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
                        color = Muted
                    )
                }
                Text(
                    text = state.event?.name ?: "",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Ink,
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
                        tint = if (state.index < state.total - 1) Bronze else Color.Transparent
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (state.isAuthorized) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (state.status == DetailsState.Status.CONFIRMED) Pine else Brick)
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
                    color = Muted
                )
            }

            if (state.status == DetailsState.Status.NOT_REGISTERED && state.availableSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Skills required for this event:".uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Bronze,
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 0.05.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                state.availableSkills.forEach { skill ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onAction(DetailsAction.SkillToggled(skill.id)) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.selectedSkillIds.contains(skill.id),
                            onCheckedChange = { viewModel.onAction(DetailsAction.SkillToggled(skill.id)) },
                            colors = CheckboxDefaults.colors(checkedColor = Pine)
                        )
                        Text(
                            text = skill.name,
                            fontSize = 14.sp,
                            color = Ink,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Hairline)
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
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!state.isAuthorized || state.status == DetailsState.Status.NOT_REGISTERED) Pine else Color.Transparent,
                contentColor = if (!state.isAuthorized || state.status == DetailsState.Status.NOT_REGISTERED) Paper else Brick
            ),
            border = if (state.isAuthorized && state.status != DetailsState.Status.NOT_REGISTERED) 
                androidx.compose.foundation.BorderStroke(1.5.dp, Brick) 
            else null,
            contentPadding = PaddingValues(0.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Paper)
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
