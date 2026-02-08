package com.gear.hub.navigation

import gearhub.feature.menu_feature.navigation.DestinationMenu
import gearhub.feature.menu_feature.internal.presentation.menu.MenuAction
import gearhub.feature.menu_feature.internal.presentation.menu.MenuStateUI
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
class DestinationMenuAny(
    val destination: DestinationMenu
) : NSObject()

@ExportObjCClass
class MenuStateAny(
    val state: MenuStateUI
) : NSObject()

@ExportObjCClass
class MenuActionAny(
    val action: MenuAction
) : NSObject()
