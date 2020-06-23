package com.burakkarakoca.weatherapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.burakkarakoca.weatherapp.R
import com.burakkarakoca.weatherapp.adapter.WeatherInfoRecyclerViewAdapter
import com.burakkarakoca.weatherapp.model.foreWeather.FiveDayWeatherResponse
import com.burakkarakoca.weatherapp.model.foreWeather.ListDaily
import com.burakkarakoca.weatherapp.service.WeatherAPI
import kotlinx.android.synthetic.main.fragment_weather_info.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherInfoFragment(var cityName : String) : Fragment(){

    private val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    private var CITY_ID: String = "5128638"
    private var fiveDayWeatherForecastData : ArrayList<ListDaily>? = null
    var listDailyies : ArrayList<ListDaily>? = null

    var recyclerView: RecyclerView? = null

    var weatherInfoRecyclerViewAdapter : WeatherInfoRecyclerViewAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_info,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.fragment_weather_info_recyclerView
        if(cityName.isNotEmpty()){
            loadForecastDailyWeather(cityName)
        }

    }


    private fun loadForecastDailyWeather(cityNameVar : String){

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherAPI::class.java)
        //http://api.openweathermap.org/data/2.5/forecast?q={city%20name}&appid={your%20api%20key}
        val callWeather = service.getFiveDayWeatherInfo("forecast?q=$cityNameVar&appid=de70c3b3c76b6664aa0b6843b96cd927")
        // http://api.openweathermap.org/data/2.5/forecast?q=Ankara&appid=de70c3b3c76b6664aa0b6843b96cd927
        callWeather.enqueue(object : Callback<FiveDayWeatherResponse>{

            override fun onFailure(call: Call<FiveDayWeatherResponse>, t: Throwable) {
                println("err: "+t.stackTrace)
            }

            override fun onResponse(
                call: Call<FiveDayWeatherResponse>,
                response: Response<FiveDayWeatherResponse>
            ) {

                if(response.isSuccessful){

                    response.body()?.let {
                        val responseList: List<ListDaily> = it.list

                        listDailyies = ArrayList(responseList)

                        recyclerView!!.layoutManager = LinearLayoutManager(context)
                        weatherInfoRecyclerViewAdapter = WeatherInfoRecyclerViewAdapter(listDailyies!!)
                        recyclerView!!.adapter = weatherInfoRecyclerViewAdapter


                        fiveDayWeatherForecastData = ArrayList(it.list)
//                        for (weather : ListDaily in fiveDayWeatherForecastData!!){
//                            println(weather.main.temp - 273.15)
//                        }

                    }


                }

            }

        })


    }


}