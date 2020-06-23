package com.burakkarakoca.weatherapp.model.foreWeather

class ListDaily(

    val dt : Long,
    val dt_txt : String,
    val main : MainDaily,
    val weather : List<WeatherDaily>,
    val clouds : CloudsDaily,
    val wind : WindDaily,
    val snow : SnowDaily,
    val sys : SysDaily

)