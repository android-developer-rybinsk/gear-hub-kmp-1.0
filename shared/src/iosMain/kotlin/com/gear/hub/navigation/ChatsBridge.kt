package com.gear.hub.navigation

import gearhub.feature.chats.navigation.DestinationChats
import gearhub.feature.chats.presentation.chats.ChatsAction
import gearhub.feature.chats.presentation.chats.ChatsState
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
open class DestinationChatsAny(
    val destination: DestinationChats
) : NSObject()

@ExportObjCClass
open class ChatsStateAny(
    val state: ChatsState
) : NSObject()

@ExportObjCClass
open class ChatsActionAny(
    val action: ChatsAction
) : NSObject()
