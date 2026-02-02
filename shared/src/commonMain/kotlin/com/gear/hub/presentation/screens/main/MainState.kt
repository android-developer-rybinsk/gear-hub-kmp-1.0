package com.gear.hub.presentation.screens.main

import com.gear.hub.presentation.models.TabItem
import gearhub.feature.chats.navigation.DestinationChats
import gearhub.feature.menu_feature.navigation.DestinationMenu
import gearhub.feature.products.navigation.DestinationProducts
import gearhub.feature.profile.navigation.DestinationProfile
import gearhubkmp.shared.generated.resources.Res
import gearhubkmp.shared.generated.resources.icon_test

data class MainState(
    val tabs: List<TabItem> = listOf(
        TabItem(DestinationMenu.MenuScreen.route, Res.drawable.icon_test, "Главная"),
        TabItem(DestinationProducts.MyProductsScreen.route, Res.drawable.icon_test, "Объявления"),
        TabItem(DestinationChats.ChatsScreen.route, Res.drawable.icon_test, "Сообщения"),
        TabItem(DestinationProfile.ProfileScreen.route, Res.drawable.icon_test, "Профиль")
    )
)