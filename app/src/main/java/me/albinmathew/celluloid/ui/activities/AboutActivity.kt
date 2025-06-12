package me.albinmathew.celluloid.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import me.albinmathew.celluloid.R

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(text = getString(R.string.title_activity_about)) })
                    }
                ) { paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                        Text(text = getString(R.string.about_text))
                    }
                }
            }
        }
    }
}
