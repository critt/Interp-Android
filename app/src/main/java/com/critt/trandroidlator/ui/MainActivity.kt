package com.critt.trandroidlator.ui

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
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
                MainView()
            }
        }
    }

    @Composable
    @Preview
    fun MainView() {
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
                    verticalAlignment = CenterVertically
                ) {
                    Box(modifier = Modifier.weight(.375F)) {
                        DropdownSelector(options = listOf("English", "German", "French", "Spanish"),
                            selectedOption = "English",
                            onOptionSelected = { selectedOption ->
                                // Handle the selected option
                            })
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⇌", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(.375F)) {
                        DropdownSelector(options = listOf("English", "German", "French", "Spanish"),
                            selectedOption = "German",
                            onOptionSelected = { selectedOption ->
                                // Handle the selected option
                            })
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(modifier = Modifier.weight(.15F), content = {
                        Text("☁")
                    }, onClick = {
                        // Handle the swap action
                    })
                }
            }
        }
    }
}
