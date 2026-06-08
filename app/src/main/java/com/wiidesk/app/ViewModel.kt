package com.wiidesk.app

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.wiidesk.app.backend.login.AuthRepo
import com.wiidesk.app.backend.perfil.Smile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val auth = AuthRepo()

    private val _activeProfile = MutableStateFlow<Smile?>(null)
    val activeProfile: StateFlow<Smile?> = _activeProfile.asStateFlow()

    private val _currentTheme = MutableStateFlow<WiTemaColors>(CieloTemaColors)
    val currentTheme: StateFlow<WiTemaColors> = _currentTheme.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _checkingSession = MutableStateFlow(true)
    val checkingSession: StateFlow<Boolean> = _checkingSession.asStateFlow()

    private val _rachaCount = MutableStateFlow(5)
    val rachaCount: StateFlow<Int> = _rachaCount.asStateFlow()

    private val _celularAlias = MutableStateFlow("Mi Celular Android")
    val celularAlias: StateFlow<String> = _celularAlias.asStateFlow()

    private val _pinSeguridad = MutableStateFlow("123456")
    val pinSeguridad: StateFlow<String> = _pinSeguridad.asStateFlow()

    init {
        val store = wiStore(application)
        _celularAlias.value = store.get("celular_alias", "Mi Celular Android")
        _pinSeguridad.value = store.get("celular_pin", "123456")
        
        val savedThemeName = store.get("selected_theme", "Cielo")
        val initialTheme = WiTemas.find { it.name == savedThemeName } ?: CieloTemaColors
        _currentTheme.value = initialTheme

        val cachedProfile = store.loadCachedProfile()
        if (cachedProfile != null) {
            _activeProfile.value = cachedProfile
        }
        _checkingSession.value = false
        
        verifySessionInBackground()
    }

    private fun verifySessionInBackground() {
        viewModelScope.launch {
            val context = getApplication<Application>()
            _loading.value = true
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    auth.ensureReady(context)
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        currentUser.reload().await()
                        auth.getSessionProfile()
                    } else {
                        null
                    }
                }
            }

            if (result.isSuccess) {
                val freshProfile = result.getOrNull()
                val store = wiStore(context)
                if (freshProfile != null) {
                    _activeProfile.value = freshProfile
                    store.saveCachedProfile(freshProfile)
                    val themeName = freshProfile.tema
                    WiTemas.find { it.name == themeName }?.let { theme ->
                        _currentTheme.value = theme
                        store.save("selected_theme", theme.name)
                    }
                } else {
                    val loggedIn = withContext(Dispatchers.IO) { runCatching { auth.isLoggedIn }.getOrDefault(false) }
                    if (loggedIn) {
                        withContext(Dispatchers.IO) { auth.logout(context) }
                        _activeProfile.value = null
                        store.clearCachedProfile()
                    }
                }
            } else {
                val exception = result.exceptionOrNull()
                val isAuthError = exception is FirebaseAuthInvalidUserException ||
                        exception is FirebaseAuthInvalidCredentialsException
                
                if (isAuthError) {
                    val store = wiStore(context)
                    withContext(Dispatchers.IO) { auth.logout(context) }
                    _activeProfile.value = null
                    store.clearCachedProfile()
                }
            }
            _checkingSession.value = false
            _loading.value = false
        }
    }

    fun init(context: Context) {
        // Noop - kept for backward compatibility with MainActivity
    }

    fun logout(context: Context, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            withContext(Dispatchers.IO) {
                auth.logout(context)
            }
            _activeProfile.value = null
            wiStore(context).clearCachedProfile()
            _loading.value = false
            onDone()
        }
    }

    fun onProfileChange(profile: Smile?, context: Context) {
        _activeProfile.value = profile
        val store = wiStore(context)
        if (profile != null) {
            store.saveCachedProfile(profile)
            WiTemas.find { it.name == profile.tema }?.let { theme ->
                _currentTheme.value = theme
                store.save("selected_theme", theme.name)
            }
        } else {
            store.clearCachedProfile()
        }
    }

    fun onThemeChange(theme: WiTemaColors, context: Context) {
        _currentTheme.value = theme
        wiStore(context).save("selected_theme", theme.name)
        
        val profile = _activeProfile.value ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    auth.updateProfileTheme(profile.usuario, theme.name)
                }
            }
            val fresh = withContext(Dispatchers.IO) {
                runCatching { auth.getSessionProfile() }.getOrNull()
            }
            if (fresh != null) {
                _activeProfile.value = fresh
                wiStore(context).saveCachedProfile(fresh)
            }
        }
    }

    fun updateAlias(alias: String, context: Context) {
        _celularAlias.value = alias
        wiStore(context).save("celular_alias", alias)
    }

    fun updatePin(pin: String, context: Context) {
        if (pin.length <= 6) {
            _pinSeguridad.value = pin
            wiStore(context).save("celular_pin", pin)
        }
    }
}
