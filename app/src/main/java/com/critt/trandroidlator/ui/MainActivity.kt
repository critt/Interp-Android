package com.critt.trandroidlator.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.critt.trandroidlator.data.ApiResult
import com.critt.trandroidlator.data.LanguageData
import com.critt.trandroidlator.ui.components.DropdownSelector
import com.critt.trandroidlator.ui.components.TranslationGroup
import com.critt.trandroidlator.ui.theme.TrandroidlatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrandroidlatorTheme {
                MainView(viewModel)
            }
        }
    }

    @Composable
    @Preview
    fun MainView(viewModel: MainViewModel = viewModel()) {
        val supportedLanguages by viewModel.supportedLanguages.observeAsState(ApiResult.Loading)

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
                TranslationGroup("English", "German")
            }
            Box(modifier = Modifier.weight(.40F)) {
                TranslationGroup("German", "English")
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
                        is ApiResult.Success -> (supportedLanguages as ApiResult.Success<List<LanguageData>>).data
                        else -> emptyList()
                    }?.let {
                        DropdownSelector(
                            options = it,
                            selectedOption = viewModel.subjectLanguage,
                            onOptionSelected = { selectedOption ->
                                viewModel.subjectLanguage = selectedOption
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
                            selectedOption = viewModel.objectLanguage,
                            onOptionSelected = { selectedOption ->
                                viewModel.objectLanguage = selectedOption
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        when (viewModel.isConnected) {
                            true -> viewModel.disconnect()
                            false -> viewModel.connect(
                                viewModel.objectLanguage.language,
                                viewModel.subjectLanguage.language
                            )
                        }
                    },
                    modifier = Modifier.weight(.15F)
                ) {
                    Text("☁")
                }
            }
        }
    }
}
