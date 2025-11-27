package com.gear.hub.navigation

import gearhub.feature.products.navigation.DestinationProducts
import gearhub.feature.products.presentation.my.MyAdsAction
import gearhub.feature.products.presentation.my.MyAdsState
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
class DestinationProductsAny(
    val destination: DestinationProducts
) : NSObject()

@ExportObjCClass
class MyAdsStateAny(
    val state: MyAdsState
) : NSObject()

@ExportObjCClass
class MyAdsActionAny(
    val action: MyAdsAction
) : NSObject()