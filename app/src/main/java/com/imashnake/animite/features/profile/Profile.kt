package com.imashnake.animite.features.profile

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imashnake.animite.features.theme.Backdrop
import com.imashnake.animite.features.theme.Text
import com.ramcosta.composedestinations.annotation.Destination

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Destination(style = ProfileTransitions::class)
@Composable
fun Profile() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Backdrop)
    ) {
        CircularProgressIndicator(
            color = Text,
            strokeWidth = 8.dp
        )
    }
}
