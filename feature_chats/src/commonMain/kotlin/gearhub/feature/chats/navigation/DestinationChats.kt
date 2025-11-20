package gearhub.feature.chats.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationChats(override val route: String) : Destination(route) {
    data object ChatsScreen : DestinationChats("chats")
}