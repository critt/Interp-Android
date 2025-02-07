package com.critt.trandroidlator.ui

import androidx.lifecycle.ViewModel
import com.critt.trandroidlator.data.SessionManager
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.critt.trandroidlator.util.getIdTokenSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> get() = _authState

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    fun initSession(user: FirebaseUser) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val result = user.getIdTokenSafely(true)

            result.onSuccess { tokenResult ->
                tokenResult.token?.let {
                    sessionManager.setAuthToken(it)
                    _authState.value = AuthState.Authenticated
                } ?: run {
                    showToast("Token is null")
                    _authState.value = AuthState.Unauthenticated
                }
            }.onFailure { exc ->
                exc.printStackTrace()
                exc.message?.let {
                    showToast(it)
                }
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    sealed class AuthState {
        data object Authenticated : AuthState()
        data object Unauthenticated : AuthState()
    }
}