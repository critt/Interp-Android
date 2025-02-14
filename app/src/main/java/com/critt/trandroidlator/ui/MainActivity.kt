package com.critt.trandroidlator.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.critt.trandroidlator.data.ApiResult
import com.critt.trandroidlator.data.LanguageData
import com.critt.trandroidlator.data.Speaker
import com.critt.trandroidlator.data.defaultLangObject
import com.critt.trandroidlator.data.defaultLangSubject
import com.critt.trandroidlator.ui.components.DropdownSelector
import com.critt.trandroidlator.ui.theme.TrandroidlatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startRecording()
        } else {
            // Handle the case where the user denied the permission
            // You can show a message or disable the functionality that requires the permission
        }
    }

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.startRecording()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrandroidlatorTheme {
                MainView(viewModel)
            }
        }
    }

    @Composable
    fun TranslationGroup(speaker: Speaker, interactionSource: MutableInteractionSource? = null) {
        TrandroidlatorTheme {
            Column(modifier = Modifier.padding(16.dp)) {
                LanguageDisplay(speaker)
                Spacer(modifier = Modifier.height(12.dp))
                OutputCard(speaker, interactionSource)
            }
        }
    }

    @Composable
    fun LanguageDisplay(speaker: Speaker) {
        val langSubject by viewModel.langSubject.observeAsState(defaultLangSubject)
        val langObject by viewModel.langObject.observeAsState(defaultLangObject)

        TrandroidlatorTheme {
            Row {
                Text(
                    when (speaker) {
                        Speaker.SUBJECT -> langObject.name
                        Speaker.OBJECT -> langSubject.name
                    },
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("⇌", color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when (speaker) {
                        Speaker.SUBJECT -> langSubject.name
                        Speaker.OBJECT -> langObject.name
                    },
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

    @Composable
    fun OutputCard(user: Speaker, interactionSource: MutableInteractionSource? = null) {
        val output by when (user) {
            Speaker.SUBJECT -> viewModel.translationObject.observeAsState("")
            Speaker.OBJECT -> viewModel.translationSubject.observeAsState("")
        }

        TrandroidlatorTheme {
            Surface(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = if (interactionSource != null) LocalIndication.current else null,
                    ) {}
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                shadowElevation = 4.dp
            ) {
                Text(
                    output ?: "",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    @Composable
    @Preview
    fun MainView(viewModel: MainViewModel = viewModel()) {
        val supportedLanguages by viewModel.supportedLanguages.observeAsState(ApiResult.Loading)
        val langSubject by viewModel.langSubject.observeAsState(defaultLangSubject)
        val langObject by viewModel.langObject.observeAsState(defaultLangObject)
        val isConnected by viewModel.isConnected.observeAsState(false)

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        viewModel.speakerCurr = if (isPressed) Speaker.SUBJECT else Speaker.OBJECT

        TrandroidlatorTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .weight(.40F)
                        .rotate(180F)
                ) {
                    TranslationGroup(Speaker.OBJECT)
                }
                Box(modifier = Modifier.weight(.40F)) {
                    TranslationGroup(Speaker.SUBJECT, interactionSource)
                }
                Row(
                    modifier = Modifier
                        .weight(.20F)
                        .padding(16.dp)
                        .width(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(.375F)) {
                        when (supportedLanguages) {
                            is ApiResult.Success -> (supportedLanguages as ApiResult.Success<List<LanguageData>>).data //TODO: This Flow<ApiResult<T>>.asLiveData() pattern is super awk
                            else -> emptyList()
                        }?.let {
                            DropdownSelector(
                                options = it,
                                selectedOption = langSubject,
                                onOptionSelected = { selectedOption ->
                                    viewModel.langSubject.value = selectedOption
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⇌", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(.375F)) {
                        when (supportedLanguages) {
                            is ApiResult.Success -> (supportedLanguages as ApiResult.Success<List<LanguageData>>).data
                            else -> emptyList()
                        }?.let {
                            DropdownSelector(
                                options = it,
                                selectedOption = langObject,
                                onOptionSelected = { selectedOption ->
                                    viewModel.langObject.value = selectedOption
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            when (isConnected) {
                                true -> {
                                    viewModel.stopRecording()
                                    viewModel.disconnect()
                                }

                                false -> {
                                    if (viewModel.connect()) {
                                        startRecording()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(.15F)
                    ) {
                        Text(
                            "☁", color = when (isConnected) {
                                true -> MaterialTheme.colorScheme.onSecondary
                                false -> MaterialTheme.colorScheme.onPrimary
                            }
                        )
                    }
                }
            }
        }
    }
}
