package com.gear.hub.navigation

import gear.hub.core.navigation.Destination
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
class DestinationAny(val destination: Destination) : NSObject()