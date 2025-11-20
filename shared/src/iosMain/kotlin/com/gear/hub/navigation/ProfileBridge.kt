package com.gear.hub.navigation

import gearhub.feature.profile.navigation.DestinationProfile
import gearhub.feature.profile.presentation.profile.ProfileAction
import gearhub.feature.profile.presentation.profile.ProfileState
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
open class DestinationProfileAny(
    val destination: DestinationProfile
) : NSObject()

@ExportObjCClass
open class ProfileStateAny(
    val state: ProfileState
) : NSObject()

@ExportObjCClass
open class ProfileActionAny(
    val action: ProfileAction
) : NSObject()