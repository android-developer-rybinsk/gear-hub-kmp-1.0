package com.gear.hub.presentation.models

import org.jetbrains.compose.resources.DrawableResource

data class TabItem(
    val route: String,
    val icon: DrawableResource,
    val label: String
)