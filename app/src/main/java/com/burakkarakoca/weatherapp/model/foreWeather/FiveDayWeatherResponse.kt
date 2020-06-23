package com.burakkarakoca.weatherapp.model.foreWeather

data class FiveDayWeatherResponse(

    val list : List<ListDaily>,
    val city : City,
    val cod : Int,
    val cnt : Int,
    val message : Int

)