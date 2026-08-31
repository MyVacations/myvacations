package es.myvacations.myvacations.data.repository

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.repository.FirebaseAuthRepository
import es.myvacations.myvacations.presentation.dashboard.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class FirebaseAuthImpl actual constructor() :
    FirebaseAuthRepository, KoinComponent {
    val firebaseAuth: FirebaseAuth by inject()
    private val WEB_CLIENT_ID =
        "707718412190-sf9ibdc5sqqmmqulit67i5snkjalaaeh.apps.googleusercontent.com"

    actual override fun getUserInfo(): Flow<UserInfo> {
        return flow {
            emit(
                UserInfo(
                    userName = firebaseAuth.currentUser?.displayName ?: "",
                    userPhoto = firebaseAuth.currentUser?.photoUrl?.toString(),
                    userEmail = firebaseAuth.currentUser?.email,
                    isLoggedIn = firebaseAuth.currentUser?.isAnonymous?.not() ?: false
                )
            )
        }
    }

    actual override fun signInWithAnonymously() {
        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signInAnonymously()
        }
    }

    actual override fun loginShouldShow(): Boolean {
        return firebaseAuth.currentUser?.isAnonymous ?: true
    }

    actual override suspend fun signUpWithEmaiAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.failure(
                IllegalStateException("No authenticated user")
            )

        if (!user.isAnonymous) {
            return Result.failure(
                IllegalStateException("The current user is not anonymous")
            )
        }

        return try {
            val credential = EmailAuthProvider.getCredential(
                email,
                password
            )

            user.linkWithCredential(credential).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual override suspend fun signInWithEmaiAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getGoogleIdToken(): String? {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val context = AndroidContextHolder.context
            val credentialManager = CredentialManager.create(context)

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type ==
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                return googleCredential.idToken
            }

            return null

        } catch (e: Exception) {
            return null
        }
    }


    actual override suspend fun signInWithGoogle(): Result<Unit> {
        return try {
            if (loginShouldShow()) {

                val credential = GoogleAuthProvider.getCredential(
                    getGoogleIdToken(),
                    null
                )
                firebaseAuth.currentUser
                    ?.linkWithCredential(credential)
                    ?.await()
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual override suspend fun resetPassword(
        email: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual override fun signOut() {
        firebaseAuth.signOut()
    }
}