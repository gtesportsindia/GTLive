package com.gtesports.gtlive.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gtesports.gtlive.model.ChatMessage
import com.gtesports.gtlive.model.StreamSession
import com.gtesports.gtlive.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentUserProfile(): UserProfile? {
        val currentUser = auth.currentUser ?: return null
        return try {
            val doc = firestore.collection("users").document(currentUser.uid).get().await()
            if (doc.exists()) {
                doc.toObject(UserProfile::class.java)
            } else {
                val newProfile = UserProfile(
                    uid = currentUser.uid,
                    displayName = currentUser.displayName ?: "GT Broadcaster",
                    email = currentUser.email ?: "",
                    photoUrl = currentUser.photoUrl?.toString() ?: ""
                )
                firestore.collection("users").document(currentUser.uid).set(newProfile).await()
                newProfile
            }
        } catch (e: Exception) {
            UserProfile(uid = currentUser.uid, displayName = currentUser.displayName ?: "GT Broadcaster")
        }
    }
}

class StreamRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun saveStreamHistory(session: StreamSession) {
        try {
            firestore.collection("streams").document(session.id.ifEmpty { System.currentTimeMillis().toString() })
                .set(session).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeLiveChat(streamId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("streams")
            .document(streamId)
            .collection("chat")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }
}
