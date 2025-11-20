package com.gear.hub.navigation

import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.presentation.menu.MenuAction
import gearhub.feature.menu.presentation.menu.MenuState
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
open class DestinationMenuAny(
    val destination: DestinationMenu
) : NSObject()

@ExportObjCClass
open class MenuStateAny(
    val state: MenuState
) : NSObject()

@ExportObjCClass
open class MenuActionAny(
    val action: MenuAction
) : NSObject()