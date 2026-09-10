package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalendarDataSource(
    private val supabaseClient: SupabaseClient,
) {
    private val _assignments = MutableStateFlow<List<EventAssignment>>(emptyList())
    val assignments: StateFlow<List<EventAssignment>> = _assignments.asStateFlow()

    suspend fun fetchCalendarEvents(): Result<List<CalendarEvent>> = try {
        Result.success(supabaseClient.from("calendar_events").select().decodeList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun fetchAssignments(): Result<Unit> = try {
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

    suspend fun applyForEvent(userId: String, eventId: Long): Boolean {
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

    suspend fun cancelEventAssignment(userId: String, eventId: Long): Boolean {
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
