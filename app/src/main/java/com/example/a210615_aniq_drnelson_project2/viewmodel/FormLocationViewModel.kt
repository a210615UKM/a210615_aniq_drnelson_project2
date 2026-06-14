package com.example.a210615_aniq_drnelson_project2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a210615_aniq_drnelson_project2.data.model.LocationData

class FormLocationViewModel : ViewModel() {

    var formLocation by mutableStateOf<LocationData?>(null)
        private set
    var formLocationName by mutableStateOf("")
        private set
    var placeImageUrl by mutableStateOf<String?>(null)
        private set
    var placeName by mutableStateOf<String?>(null)
        private set
    var placePhone by mutableStateOf<String?>(null)
        private set

    fun setFormLocation(lat: Double, lng: Double, name: String) {
        formLocation = LocationData(lat, lng)
        formLocationName = name
    }

    fun setPlaceDetails(imageUrl: String?, storeName: String?, phone: String?) {
        placeImageUrl = imageUrl
        placeName = storeName
        placePhone = phone
    }

    fun clearFormLocation() {
        formLocation = null
        formLocationName = ""
        placeImageUrl = null
        placeName = null
        placePhone = null
    }
}
