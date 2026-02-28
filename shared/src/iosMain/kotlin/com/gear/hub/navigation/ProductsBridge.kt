package com.gear.hub.navigation

import gearhub.feature.products.product_feature.api.navigation.DestinationProducts
import gearhub.feature.products.product_feature.internal.presentation.my.MyProductsAction
import gearhub.feature.products.product_feature.internal.presentation.models.MyProductsStateUI
import kotlinx.cinterop.ExportObjCClass
import platform.darwin.NSObject

@ExportObjCClass
class DestinationProductsAny(
    val destination: DestinationProducts
) : NSObject()

@ExportObjCClass
class MyProductsStateAny(
    val state: MyProductsStateUI
) : NSObject()

@ExportObjCClass
class MyProductsActionAny(
    val action: MyProductsAction
) : NSObject()
