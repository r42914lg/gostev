package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class CalendarDataSource(
    private val supabaseClient: SupabaseClient,
) {
    suspend fun fetchCalendarEvents(): Result<List<CalendarEvent>> = try {
        Result.success(supabaseClient.postgrest.from("calendar_events").select().decodeList())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun fetchAssignments(): Result<List<EventAssignment>> = try {
        val uid = supabaseClient.auth.currentUserOrNull()?.id
        if (uid == null) {
            Result.success(emptyList())
        } else {
            Result.success(
                supabaseClient.from("event_assignments")
                    .select { filter { eq("user_id", uid) } }
                    .decodeList()
            )
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun applyForEvent(userId: String, eventId: Long): Boolean = try {
        supabaseClient.postgrest.from("event_assignments").insert(
            EventAssignment(userId = userId, eventId = eventId)
        )
        true
    } catch (_: Exception) {
        false
    }

    suspend fun cancelEventAssignment(userId: String, eventId: Long): Boolean = try {
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
}
