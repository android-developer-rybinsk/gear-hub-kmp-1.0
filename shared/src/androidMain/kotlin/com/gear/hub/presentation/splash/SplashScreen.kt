package com.gear.hub.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gear.hub.presentation.screens.splash.SplashAction
import com.gear.hub.presentation.screens.splash.SplashViewModel
import com.gear.hub.shared.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import gear.hub.core.di.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color(0xFF0A2841),
            darkIcons = useDarkIcons
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(SplashAction.OnStartTimeout)
    }
    LaunchedEffect(state.isTimeout) {
        if (state.isTimeout) viewModel.onAction(SplashAction.OnEndTimeout)
    }

    val carOffsetX = remember { Animatable(-1000f) }
    val bikeOffsetX = remember { Animatable(-1000f) }

    LaunchedEffect(Unit) {
        carOffsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        bikeOffsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A2841)),
    ) {
        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = R.drawable.gear_hub),
                contentDescription = null,
                modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)
            )
            Text(
                text = "GearHub",
                color = Color.White,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.weight(1f))

        Box (
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 48.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.auto),
                contentDescription = "Car",
                modifier = Modifier
                    .offset { IntOffset(carOffsetX.value.roundToInt(), 0) }
                    .size(400.dp)
                    .align(Alignment.Center)
            )
            Image(
                painter = painterResource(id = R.drawable.moto),
                contentDescription = "Motorcycle",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.Center)
                    .padding(end = 150.dp)
                    .offset { IntOffset(bikeOffsetX.value.roundToInt(), 0) }
                    .size(200.dp)
            )
        }
    }
}