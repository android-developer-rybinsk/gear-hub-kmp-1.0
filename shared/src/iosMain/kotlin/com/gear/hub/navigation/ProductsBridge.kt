package com.gear.hub.navigation

import gearhub.feature.products.navigation.DestinationProducts
import gearhub.feature.products.presentation.my.MyProductsAction
import gearhub.feature.products.presentation.my.MyProductsState
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
class DestinationProductsAny(
    val destination: DestinationProducts
) : NSObject()

@ExportObjCClass
class MyProductsStateAny(
    val state: MyProductsState
) : NSObject()

@ExportObjCClass
class MyProductsActionAny(
    val action: MyProductsAction
) : NSObject()