package org.bfchain.libappmgr.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProgressCircular(progressValue: Float) {
    //val rememberProgress = remember { mutableStateOf(0.1f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d("ProgressCircular", "progressValue->$progressValue")
        CircularProgressIndicator(
            progress = progressValue, color = Color.White
        )

        Spacer(Modifier.height(30.dp))
    }
}