package com.example.socialmediaapp.data.firebase.remote

import android.util.Log
import com.example.socialmediaapp.data.entity.notification.Message
import com.example.socialmediaapp.other.Constant.COLLECTION_MESSAGES
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRemoteDatabase @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val curUserId = auth.currentUser?.uid

    private val userCollection = db.collection(COLLECTION_USERS)
    private val messagesCollection = userCollection.document(curUserId!!).collection(COLLECTION_MESSAGES)
    private val messageCollectionGroup = db.collectionGroup(COLLECTION_MESSAGES)

    suspend fun sendMessage(receiverId: String, content: String) {
        val message = Message(
            messagesCollection.document().id,
            curUserId.toString(),
            receiverId,
            content
        )
        userCollection
            .document(curUserId.toString())
            .collection(COLLECTION_MESSAGES)
            .document(message.messageId)
            .set(message)
            .await()
    }

    suspend fun fetchMessagesRealtime(receiverId: String, onMessageReceived: (List<Message>) -> Unit) {
        messageCollectionGroup.orderBy("timestamp").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("MessageRemoteDatabase", "fetchMessagesRealtime: $e")
                return@addSnapshotListener
            }
            if (!snapshot!!.isEmpty) {
                val messagesList = mutableListOf<Message>()
                repeat(snapshot.documents.size) {
                    val message = snapshot.documents[it].toObject(Message::class.java)
                    if (
                        (message!!.senderId == receiverId && message.receiverId == curUserId)
                        ||
                        (message.senderId == curUserId && message.receiverId == receiverId)
                    ) {
                        messagesList.add(message)
                    }
                }
                onMessageReceived(messagesList)
            }
        }

    }

}