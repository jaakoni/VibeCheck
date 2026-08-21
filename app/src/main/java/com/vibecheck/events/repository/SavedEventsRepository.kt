package com.vibecheck.events.repository

import com.vibecheck.events.model.Event
import com.vibecheck.events.model.EventCategory
import com.vibecheck.events.model.EventSource
import com.vibecheck.events.model.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SavedEventsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    private fun getSavedEventsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("savedEvents")

    suspend fun saveEvent(userId: String, event: Event): Result<Unit> {
        return try {
            val eventMap = hashMapOf(
                "id" to event.id,
                "title" to event.title,
                "description" to event.description,
                "source" to event.source.name,
                "sourceUrl" to event.sourceUrl,
                "category" to event.category.name,
                "startTimestamp" to event.startTimestamp,
                "endTimestamp" to event.endTimestamp,
                "cost" to event.cost,
                "imageUrls" to event.imageUrls,
                "locationVenueName" to event.location.venueName,
                "locationAddress" to event.location.address,
                "locationCity" to event.location.city,
                "locationLatitude" to event.location.latitude,
                "locationLongitude" to event.location.longitude,
                "tags" to event.tags,
                "organizerName" to event.organizerName,
            )
            getSavedEventsCollection(userId).document(event.id).set(eventMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeEvent(userId: String, eventId: String): Result<Unit> {
        return try {
            getSavedEventsCollection(userId).document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSavedEventsFlow(userId: String): Flow<List<Event>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = getSavedEventsCollection(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val events = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: doc.id
                            val title = doc.getString("title") ?: ""
                            val description = doc.getString("description")
                            val sourceStr = doc.getString("source") ?: EventSource.TICKETMASTER.name
                            val source = try {
                                EventSource.valueOf(sourceStr)
                            } catch (_: Exception) {
                                EventSource.TICKETMASTER
                            }
                            val sourceUrl = doc.getString("sourceUrl") ?: ""
                            val categoryStr = doc.getString("category") ?: EventCategory.LIVE_MUSIC.name
                            val category = try {
                                EventCategory.valueOf(categoryStr)
                            } catch (_: Exception) {
                                EventCategory.LIVE_MUSIC
                            }
                            val startTimestamp = doc.getLong("startTimestamp") ?: 0L
                            val endTimestamp = doc.getLong("endTimestamp")
                            val cost = doc.getDouble("cost")
                            @Suppress("UNCHECKED_CAST")
                            val imageUrls = (doc.get("imageUrls") as? List<String>) ?: emptyList()
                            val venueName = doc.getString("locationVenueName") ?: ""
                            val address = doc.getString("locationAddress") ?: ""
                            val city = doc.getString("locationCity") ?: ""
                            val latitude = doc.getDouble("locationLatitude") ?: 0.0
                            val longitude = doc.getDouble("locationLongitude") ?: 0.0
                            @Suppress("UNCHECKED_CAST")
                            val tags = (doc.get("tags") as? List<String>) ?: emptyList()
                            val organizerName = doc.getString("organizerName")

                            Event(
                                id = id,
                                title = title,
                                description = description,
                                source = source,
                                sourceUrl = sourceUrl,
                                category = category,
                                startTimestamp = startTimestamp,
                                endTimestamp = endTimestamp,
                                cost = cost,
                                imageUrls = imageUrls,
                                location = Location(
                                    venueName = venueName,
                                    address = address,
                                    city = city,
                                    latitude = latitude,
                                    longitude = longitude,
                                ),
                                tags = tags,
                                organizerName = organizerName,
                            )
                        } catch (_: Exception) {
                            null
                        }
                    } ?: emptyList()
                    trySend(events)
                }
        } catch (e: Exception) {
            close(e)
        }
        awaitClose {
            listener?.remove()
        }
    }
}
