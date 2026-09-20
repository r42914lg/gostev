package com.r42914lg.catering.core.data.internal

import com.r42914lg.catering.core.data.CalendarDataSource
import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import com.r42914lg.catering.core.data.model.Skill
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

internal class CalendarDataSourceImpl(
    private val supabaseClient: SupabaseClient,
) : CalendarDataSource {
    private val _calendarEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    override val calendarEvents: StateFlow<List<CalendarEvent>> = _calendarEvents.asStateFlow()

    private val _assignments = MutableStateFlow<List<EventAssignment>>(emptyList())
    override val assignments: StateFlow<List<EventAssignment>> = _assignments.asStateFlow()

    @Serializable
    private data class EventSkillJoin(val skills: Skill)

    override suspend fun fetchCalendarEvents(): Result<Unit> = try {
        val list = supabaseClient.from("calendar_events")
            .select{ filter { eq("is_available", true) } }
            .decodeList<CalendarEvent>()
        _calendarEvents.value = list
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
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
        e.printStackTrace()
        Result.failure(e)
    }

    override suspend fun fetchSkillsForEvent(eventId: Long): Result<List<Skill>> = try {
        val results = supabaseClient.from("event_skills")
            .select(Columns.raw("skills(*)")) {
                filter { eq("event_id", eventId) }
            }
            .decodeList<EventSkillJoin>()
        Result.success(results.map { it.skills })
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    override suspend fun applyForEvent(userId: String, eventId: Long, skills: String?): Boolean {
        val result = try {
            supabaseClient.from("event_assignments").insert(
                EventAssignment(userId = userId, eventId = eventId, skills = skills)
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
