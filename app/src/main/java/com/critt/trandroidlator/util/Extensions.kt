package com.critt.trandroidlator.util

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import kotlinx.coroutines.tasks.await

suspend fun FirebaseUser.getIdTokenSafely(refresh: Boolean): Result<GetTokenResult> {
    return runCatching {
        val task: Task<GetTokenResult> = getIdToken(refresh)
        task.await()
    }
}
