package com.bookfriends.network

import android.content.ContentValues.TAG
import android.util.Log
import com.bookfriends.models.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.bookfriends.models.Note
import com.bookfriends.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

const val COLLECTION_REF = "notes"
const val COLLECTION_REF_USERS = "users"
const val COLLECTION_REF_BOOKS = "books"
const val PROFILE_COLLECTION = "users"
const val BOOK_COLLECTION_REF = "books"
const val PLANS_COLLECTION_REF = "readingPlans"



const val BOOKS_COLLECTION_REF = "books"
const val DISCUSSIONS_COLLECTION_REF = "discussions"
const val USERS_COLLECTION_REF = "users"
const val COMMENTS_COLLECTION_REF = "comments"



object NetworkServiceAdapter {


    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    fun getUserEmail(): String = Firebase.auth.currentUser?.email.orEmpty()

    val firebaseAuth = Firebase.auth.runCatching { currentUser }

    private val notesRef: CollectionReference = Firebase
        .firestore.collection(COLLECTION_REF)

    private val usersRef: CollectionReference = Firebase
        .firestore.collection(PROFILE_COLLECTION)

    private val booksRef: CollectionReference = Firebase
        .firestore.collection(COLLECTION_REF_BOOKS)

    private val plansRef: CollectionReference = Firebase
        .firestore.collection(PLANS_COLLECTION_REF)


    private val commentsRef: CollectionReference = Firebase
        .firestore.collection(COMMENTS_COLLECTION_REF)


    private val discussionsRef: CollectionReference = Firebase
        .firestore.collection(DISCUSSIONS_COLLECTION_REF)





    suspend fun createUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        onComplete: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete.invoke(true)
                } else {
                    onComplete.invoke(false)
                }
            }.await()
        // Create a new user with a first and last name
        val user = hashMapOf(
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            // Convert birthDate to Timestamp
            "birthDate" to SimpleDateFormat("dd-MM-yyyy").parse(birthDate),
            "registrationDate" to Timestamp.now()
        )
        // Add a new document with a generated ID
        Firebase.firestore.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }


    }

    suspend fun login(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete.invoke(true)
                } else {
                    onComplete.invoke(false)
                }
            }.await()
    }

    fun addPersistence() {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings
    }

    // Notes

    suspend fun getUserNotes(
        userId: String,
    ): Flow<Resources<List<Note>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {

            snapshotStateListener = notesRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val notes = snapshot.toObjects(Note::class.java)
                        Resources.Success(data = notes)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getBooks(
    ): Flow<Resources<List<Book>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = booksRef
                .orderBy("numPages")
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val books = snapshot.toObjects(Book::class.java)
                        Resources.Success(data = books)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }
        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }
        awaitClose {
            snapshotStateListener?.remove()
        }
    }


    suspend fun getNote(
        noteId:String,
        onError:(Throwable?) -> Unit,
        onSuccess: (Note?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            notesRef
                .document(noteId)
                .get()
                .addOnSuccessListener {
                    onSuccess.invoke(it?.toObject(Note::class.java))
                }
                .addOnFailureListener { result ->
                    onError.invoke(result.cause)
                }
        }
    }

    fun addNote(
        userId: String,
        title: String,
        description: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit,
    ) {
        val documentId = notesRef.document().id
        val note = Note(
            userId,
            title,
            description,
            timestamp,
            documentId = documentId
        )
        notesRef
            .document(documentId)
            .set(note)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteNote(noteId: String, onComplete: (Boolean) -> Unit) {
        notesRef.document(noteId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateNote(
        title: String,
        note: String,
        color: Int,
        noteId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to note,
            "title" to title,
        )

        notesRef.document(noteId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }
    }


    suspend fun getUserPlans(
        userId: String,
    ): Flow<Resources<List<ReadingPlans>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {

            snapshotStateListener = plansRef
                .whereArrayContains("usersIds", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val plans = snapshot.toObjects(ReadingPlans::class.java)
                        Resources.Success(data = plans)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }


    suspend fun getBookByPlan(
        bookId: String,
    ): Flow<Resources<List<Books>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {

            snapshotStateListener = plansRef
                .orderBy("timestamp")
                .whereEqualTo("bookId", bookId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val books = snapshot.toObjects(Books::class.java)
                        Resources.Success(data = books)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }


    suspend fun getAllPlans(): Flow<Resources<List<ReadingPlans>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = plansRef
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val plans = snapshot.toObjects(ReadingPlans::class.java)
                        Resources.Success(data = plans)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    // SignOut

    fun signOut() = Firebase.auth.signOut()

    // Profile

    suspend fun getProfile(
        userEmail: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (User?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            usersRef
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { profiles ->
                    for (profile in profiles) {
                        onSuccess.invoke(profile?.toObject(User::class.java))
                    }
                }
                .addOnFailureListener { result ->
                    onError.invoke(result.cause)
                }
        }
    }

    fun updateUser(
        userEmail: String,
        firstName: String,
        lastName: String,
        imageUrl: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "firstName" to firstName,
            "lastName" to lastName,
            "imageUrl" to imageUrl,
        )

        val docIdQuery: Query = usersRef.whereEqualTo("email", userEmail)

        docIdQuery.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    usersRef.document(document.reference.id)
                        .update(updateData)
                        .addOnCompleteListener {
                            onResult(it.isSuccessful)
                        }
                }
            } else {
                Log.d(
                    TAG,
                    "Error getting documents: ",
                    task.exception
                ) //Don't ignore potential errors!
            }
        }
    }

    fun getAllDiscussions(): Flow<Resources<List<Discussions>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = discussionsRef
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val discussions = snapshot.toObjects(Discussions::class.java)
                        Resources.Success(data = discussions)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getAllBooks(): Flow<Resources<List<Books>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = booksRef
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val books = snapshot.toObjects(Books::class.java)
                        Resources.Success(data = books)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getAllUsers(): Flow<Resources<List<User>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = usersRef
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val usr = snapshot.toObjects(User::class.java)
                        Resources.Success(data = usr)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getAllComments(): Flow<Resources<List<Comments>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = commentsRef
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val cmt = snapshot.toObjects(Comments::class.java)
                        Resources.Success(data = cmt)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

}


sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)

}
































