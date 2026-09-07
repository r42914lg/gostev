package com.r42914lg.catering.core.data

import com.r42914lg.catering.core.data.model.CalendarEvent
import com.r42914lg.catering.core.data.model.EventAssignment
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class CalendarDataSource(private val supabaseClient: SupabaseClient) {
    suspend fun getCalendarEvents(): List<CalendarEvent> =
        supabaseClient.postgrest.from("events").select().decodeList()

    suspend fun getEventAssignments(userId: Long): List<EventAssignment> =
        supabaseClient.postgrest.from("assignments").select {
            filter {
                eq("userId", userId)
            }
        }.decodeList()

    suspend fun applyForEvent(userId: Long, eventId: Long): Boolean = try {
        supabaseClient.postgrest.from("assignments").insert(
            EventAssignment(userId = userId, eventId = eventId)
        )
        true
    } catch (_: Exception) {
        false
    }
}