package com.gear.hub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform