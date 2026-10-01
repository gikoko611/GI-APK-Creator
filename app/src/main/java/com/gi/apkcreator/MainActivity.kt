package com.gi.apkcreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    APKCreatorScreen()
                }
            }
        }
    }
}

@Composable
fun APKCreatorScreen() {
    var prompt by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "G.I APK Creator",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Describe the Android app you want to create."
        )

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("App description")
            },
            placeholder = {
                Text("Create a notes app with dark mode...")
            },
            minLines = 5
        )

        Button(
            onClick = {
                // AI generation will be connected later.
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("✨ Generate App")
        }
    }
}
