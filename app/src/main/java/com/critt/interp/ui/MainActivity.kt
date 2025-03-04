package com.critt.interp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.critt.data.ApiResult
import com.critt.domain.LanguageData
import com.critt.domain.Speaker
import com.critt.interp.ui.components.DropdownSelector
import com.critt.ui_common.theme.InterpTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterpTheme {
                MainView(viewModel<MainViewModel>())
            }
        }
    }

    @Composable
    fun TranslationGroup(
        translationText: String = "",
        speaker: Speaker,
        langSubject: LanguageData,
        langObject: LanguageData,
        interactionSource: MutableInteractionSource? = null
    ) {
        InterpTheme {
            Column(modifier = Modifier.padding(16.dp)) {
                LanguageDisplay(speaker, langSubject, langObject)
                Spacer(modifier = Modifier.height(12.dp))
                OutputCard(translationText, interactionSource)
            }
        }
    }

    @Composable
    fun LanguageDisplay(speaker: Speaker, langSubject: LanguageData, langObject: LanguageData) {
        InterpTheme {
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
    fun OutputCard(translationText: String, interactionSource: MutableInteractionSource? = null) {
        InterpTheme {
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
                    text = translationText,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    //TODO: Refactor to use Scaffold
    @Composable
    @Preview
    fun MainView(viewModel: MainViewModel = viewModel()) {
        val context = LocalContext.current

        // State to track if the audio recording permission is granted
        var hasRecordingPermission by rememberSaveable {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasRecordingPermission = isGranted
            }
        )

        // ViewModel StateFlows
        val supportedLanguages by viewModel.supportedLanguages.collectAsState()
        val langSubject by viewModel.langSubject.collectAsState()
        val langObject by viewModel.langObject.collectAsState()
        val translationSubject by viewModel.translationSubject.collectAsState()
        val translationObject by viewModel.translationObject.collectAsState()
        val streamingState by viewModel.streamingState.collectAsState()

        /** Local state -> LaunchedEffect -> ViewModel StateFlow */
        // Language selector for Subject speaker
        var uiSelectedLangSubject by remember { mutableStateOf(langSubject) }
        LaunchedEffect(uiSelectedLangSubject) {
            viewModel.selectLangSubject(uiSelectedLangSubject)
        }
        // Language selector for Object speaker
        var uiSelectedLangObject by remember { mutableStateOf(langObject) }
        LaunchedEffect(uiSelectedLangObject) {
            viewModel.selectLangObject(uiSelectedLangObject)
        }
        // Interaction source (pressing down on the lower OutputCard) for current Speaker
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        LaunchedEffect(isPressed) {
            viewModel.updateSpeaker(subjectSpeaking = isPressed)
        }
        // Streaming state toggle (FAB)
        var toggleSideEffect by remember { mutableStateOf<(() -> Unit)?>(null) }
        LaunchedEffect(toggleSideEffect, hasRecordingPermission) {
            if (!hasRecordingPermission) {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                toggleSideEffect?.invoke()
                toggleSideEffect = null
            }
        }

        InterpTheme {
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
                    TranslationGroup(
                        translationText = translationObject,
                        speaker = Speaker.OBJECT,
                        langSubject = langSubject,
                        langObject = langObject,
                    )
                }
                Box(modifier = Modifier.weight(.40F)) {
                    TranslationGroup(
                        translationText = translationSubject,
                        speaker = Speaker.SUBJECT,
                        langSubject = langSubject,
                        langObject = langObject,
                        interactionSource = interactionSource
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(.20F)
                        .padding(16.dp)
                        .width(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(.375F)) {
                        DropdownSelector(
                            options = (supportedLanguages as? ApiResult.Success)?.data
                                ?: emptyList(),
                            selectedOption = langSubject,
                            onOptionSelected = { selectedOption ->
                                // Update the local UI state
                                uiSelectedLangSubject = selectedOption
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⇌", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(.375F)) {
                        DropdownSelector(
                            options = (supportedLanguages as? ApiResult.Success)?.data
                                ?: emptyList(),
                            selectedOption = langObject,
                            onOptionSelected = { selectedOption ->
                                // Update the local UI state
                                uiSelectedLangObject = selectedOption
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            toggleSideEffect = { viewModel.toggleStreaming() }
                        },
                        modifier = Modifier.weight(.15F)
                    ) {
                        Text(
                            "☁", color = when (streamingState) {
                                AudioStreamingState.Streaming -> MaterialTheme.colorScheme.onSecondary
                                AudioStreamingState.Idle -> MaterialTheme.colorScheme.onPrimary
                                is AudioStreamingState.Error -> MaterialTheme.colorScheme.onError
                            }
                        )
                    }
                }
            }
        }
    }
}
