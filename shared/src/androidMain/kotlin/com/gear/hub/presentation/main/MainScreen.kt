package com.gear.hub.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.gear.hub.presentation.models.TabItem
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainScreen(
    tabs: List<TabItem>,
    currentRoute: String?,
    onTabSelected: (TabItem) -> Unit,
    content: @Composable (Modifier) -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            CustomNavigationBar(
                currentRoute = currentRoute,
                tabs = tabs,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}

@Composable
fun CustomNavigationBar(
    currentRoute: String?,
    tabs: List<TabItem>,
    onTabSelected: (TabItem) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF101010))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val isSelected = currentRoute == tab.route
            val background = if (isSelected) Color(0xFFEBA937) else Color.Transparent
            val contentColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(background)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(tab.icon),
                        contentDescription = tab.label,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tab.label,
                    color = contentColor,
                    fontSize = if (isSelected) 14.sp else 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}