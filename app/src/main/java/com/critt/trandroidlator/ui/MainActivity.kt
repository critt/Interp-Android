package com.critt.trandroidlator.ui

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }

    @Composable
    @Preview
    fun MainView() {
        Column {
            TranslationGroup("English", "German")
            TranslationGroup("German", "English")
        }
    }

    @Composable
    fun TranslationGroup(subjectLanguage: String, objectLanguage: String) {
        Column {
            TranslationControls(subjectLanguage, objectLanguage)
            OutputCard("Sample output")
        }
    }

    @Composable
    fun TranslationControls(subjectLanguage: String, objectLanguage: String) {
        Row {
            Text(subjectLanguage)
            Text("⇌")
            Text(objectLanguage)
        }
    }

    @Composable
    fun OutputCard(output: String) {
        Text(output)
    }
}

