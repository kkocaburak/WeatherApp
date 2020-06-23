package com.burakkarakoca.weatherapp.service

import com.burakkarakoca.weatherapp.model.citiesOfTurkey.CitiesResponse
import com.burakkarakoca.weatherapp.model.foreWeather.FiveDayWeatherResponse
import com.burakkarakoca.weatherapp.model.mainWeather.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherAPI {

    //https://gist.githubusercontent.com/weeq/3fdc85b0a81cc3a1ed6f019cb3c3abc7/raw/20bc117285562d35ccd2d81317432c03c50d009b/Turkiye-Il-Ilce.json
    //https://api.openweathermap.org/data/2.5/weather?id=5128638&appid=de70c3b3c76b6664aa0b6843b96cd927
    //http://api.openweathermap.org/data/2.5/forecast?id=5128638&appid=de70c3b3c76b6664aa0b6843b96cd927
    //http://history.openweathermap.org/data/2.5/history/city?id=5128638&type=hour&start={start}&end={end}&appid=de70c3b3c76b6664aa0b6843b96cd927



//    @GET("weather?id=3882428&appid=de70c3b3c76b6664aa0b6843b96cd927")
//    fun getCurrentWeather(): Call<WeatherResponse>

    @GET
    fun getCurrentWeather(@Url url : String): Call<WeatherResponse>


//    @GET("forecast?id=5128638&appid=de70c3b3c76b6664aa0b6843b96cd927")
//    fun getFiveDayWeatherInfo(cityId : Int): Call<FiveDayWeatherResponse>

    @GET
    fun getFiveDayWeatherInfo(@Url url : String): Call<FiveDayWeatherResponse>

    @GET("20bc117285562d35ccd2d81317432c03c50d009b/Turkiye-Il-Ilce.json")
    fun getCitiesOfTurkey(): Call<List<CitiesResponse>>

}

// http://api.openweathermap.org/data/2.5/weather?q=istanbul&appid=de70c3b3c76b6664aa0b6843b96cd927


// forecast?id=5128638&appid=de70c3b3c76b6664aa0b6843b96cd927

// My Api Key : de70c3b3c76b6664aa0b6843b96cd927