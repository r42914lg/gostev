package com.r42914lg.catering.core.data.internal

import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CalendarDataSourceImpl(
    private val supabaseClient: SupabaseClient,
) : CalendarDataSource {
    private val _assignments = MutableStateFlow<List<EventAssignment>>(emptyList())
    override val assignments: StateFlow<List<EventAssignment>> = _assignments.asStateFlow()

    override suspend fun fetchCalendarEvents(): Result<List<CalendarEvent>> = try {
        Result.success(supabaseClient.from("calendar_events")
            .select{ filter { eq("is_available", true) } }
            .decodeList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun fetchAssignments(): Result<Unit> = try {
        val uid = supabaseClient.auth.currentUserOrNull()?.id
        val list = if (uid == null) {
            emptyList()
        } else {
            supabaseClient.from("event_assignments")
                .select { filter { eq("user_id", uid) } }
                .decodeList<EventAssignment>()
        }
        _assignments.value = list
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun applyForEvent(userId: String, eventId: Long): Boolean {
        val result = try {
            supabaseClient.from("event_assignments").insert(
                EventAssignment(userId = userId, eventId = eventId)
            )
            true
        } catch (_: Exception) {
            false
        }
        if (result) fetchAssignments()
        return result
    }

    override suspend fun cancelEventAssignment(userId: String, eventId: Long): Boolean {
        val result = try {
            supabaseClient.from("event_assignments").delete {
                filter {
                    eq("user_id", userId)
                    eq("event_id", eventId)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
        if (result) fetchAssignments()
        return result
    }
}
