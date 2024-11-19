package com.critt.trandroidlator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.critt.trandroidlator.ui.theme.TrandroidlatorTheme

@Composable
fun TranslationGroup(subjectLanguage: String, objectLanguage: String) {
    TrandroidlatorTheme {
        Column(modifier = Modifier.padding(16.dp)) {
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
            Text(subjectLanguage, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text("⇌", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text(objectLanguage, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun OutputCard(output: String) {
    TrandroidlatorTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            shadowElevation = 4.dp
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = output,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}