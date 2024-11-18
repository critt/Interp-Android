package com.critt.trandroidlator.ui

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.critt.trandroidlator.ui.theme.TrandroidlatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
            Column(modifier = Modifier.fillMaxSize(1F)) {
                Box(modifier = Modifier.fillMaxHeight(.35F)) {
                    TranslationGroup("English", "German")
                }
                Spacer(modifier = Modifier.fillMaxHeight(.1F))
                Box(modifier = Modifier.fillMaxHeight(.35F)) {
                    TranslationGroup("German", "English")
                }

            }
        }
    }

    @Composable
    fun TranslationGroup(subjectLanguage: String, objectLanguage: String) {
        TrandroidlatorTheme {
            Column {
                TranslationControls(subjectLanguage, objectLanguage)
                Spacer(modifier = Modifier.height(12.dp))
                OutputCard("Sample output")
            }
        }
    }

    @Composable
    fun TranslationControls(subjectLanguage: String, objectLanguage: String) {
        TrandroidlatorTheme {
            Row {
                Text(subjectLanguage)
                Spacer(modifier = Modifier.width(8.dp))
                Text("⇌")
                Spacer(modifier = Modifier.width(8.dp))
                Text(objectLanguage)
            }
        }
    }

    @Composable
    fun OutputCard(output: String) {
        TrandroidlatorTheme {
            Surface(modifier = Modifier.fillMaxSize(), shadowElevation = 4.dp) {
                Text(output)
            }
        }
    }
}
