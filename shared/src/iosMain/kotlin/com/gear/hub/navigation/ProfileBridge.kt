package com.gear.hub.navigation

import gearhub.feature.profile.navigation.DestinationProfile
import gearhub.feature.profile.presentation.profile.ProfileAction
import gearhub.feature.profile.presentation.profile.ProfileState
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
class DestinationProfileAny(
    val destination: DestinationProfile
) : NSObject()

@ExportObjCClass
class ProfileStateAny(
    val state: ProfileState
) : NSObject()

@ExportObjCClass
class ProfileActionAny(
    val action: ProfileAction
) : NSObject()