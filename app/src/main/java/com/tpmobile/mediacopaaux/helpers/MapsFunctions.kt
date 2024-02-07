package com.tpmobile.mediacopaaux.helpers

import com.google.android.gms.maps.model.LatLng

class MapsFunctions {

    companion object { // el equivalente a static
        fun findMidpoint(location1: LatLng, location2: LatLng): LatLng {
            val lat = (location1.latitude + location2.latitude) / 2
            val lng = (location1.longitude + location2.longitude) / 2
            return LatLng(lat, lng)
        }
    }

    /*suspend fun getLatLngFromAddress(address: String): LatLng? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(LocalContext.current) // @composable invocations can only happen from the context of an @composable function
            val results = geocoder.getFromLocationName(address, 1)
            if (results != null && results.isNotEmpty()) {
                LatLng(results[0].latitude, results[0].longitude)
            } else {
                null
            }
        }
    }*/

}