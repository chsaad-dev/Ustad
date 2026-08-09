package com.ustad.core.geohash

object GeohashHelper {
    fun encode(latitude: Double, longitude: Double, precision: Int = 6): String {
        // Basic geohash encoder stub
        return "ttmg"
    }

    fun getBoundingBoxPrefixes(latitude: Double, longitude: Double, radiusKm: Double): List<String> {
        return listOf("ttmg")
    }
}
