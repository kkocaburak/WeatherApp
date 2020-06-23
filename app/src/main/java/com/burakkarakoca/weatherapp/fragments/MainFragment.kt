package com.burakkarakoca.weatherapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.burakkarakoca.weatherapp.R
import com.burakkarakoca.weatherapp.model.mainWeather.Weather
import com.burakkarakoca.weatherapp.model.mainWeather.WeatherResponse
import com.burakkarakoca.weatherapp.service.WeatherAPI
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat

class MainFragment(var cityName: String, var isCurrentCity : Boolean) : Fragment(){

    private val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    private var weatherData : ArrayList<Weather>? = null

    var weatherIcon : ImageView? = null
    var tempText : TextView? = null
    var humidityText : TextView? = null
    var feelTempText : TextView? = null
    var windText : TextView? = null
    var dateText : TextView? = null
    var currentTimeText : TextView? = null
    var sunriseText : TextView? = null
    var sunsetText : TextView? = null
    var cityNameText : TextView? = null
    var currentCityIcon : ImageView? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        current_time_textView.setShadowLayer(10f, 10f, 10f, Color.BLACK)
        temp_value_textView.setShadowLayer(10f, 10f, 10f, Color.BLACK)

        weatherIcon = view.findViewById(R.id.weather_main_imageView)
        tempText = view.findViewById(R.id.temp_value_textView)
        humidityText = view.findViewById(R.id.humidity_textView)
        feelTempText = view.findViewById(R.id.feel_temp_textView)
        windText = view.findViewById(R.id.wind_speed_textView)
        dateText = view.findViewById(R.id.date_textView)
        currentTimeText = view.findViewById(R.id.current_time_textView)
        sunriseText = view.findViewById(R.id.sunrise_textView)
        sunsetText = view.findViewById(R.id.sunset_textView)
        cityNameText = view.findViewById(R.id.city_name_textView)
        currentCityIcon = view.findViewById(R.id.city_name_current_city_icon_imageView)

        if(!isCurrentCity){
            currentCityIcon!!.visibility = View.INVISIBLE
        }

        if(cityName.isNotEmpty()){
            loadWeatherData(cityName)
        }

    }


    private fun loadWeatherData(cityNameVar : String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherAPI::class.java)
        // http://api.openweathermap.org/data/2.5/weather?q=istanbul&appid=de70c3b3c76b6664aa0b6843b96cd927
        var lastCityName : String = "weather?q=$cityNameVar&appid=de70c3b3c76b6664aa0b6843b96cd927"
        val callWeather = service.getCurrentWeather(lastCityName)

        callWeather.enqueue(object : Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                println("err:"+t.localizedMessage)
            }

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {

                if (response.isSuccessful) {

                    response.body()?.let {
                        // --- WEATHER ARRAYLIST ---
                        weatherData = ArrayList(it.weather)

//                        for (weather: Weather in weatherData!!) {
//                            println(weather.description)
//                            println(weather.icon)
//                            println(weather.id)
//                            println(weather.main)
//                            // her id için http://openweathermap.org/img/wn/10d@2x.png buradaki "10d" yerine icon kodunu gir
//
//                        }

                        Picasso.get().load("http://openweathermap.org/img/wn/${it.weather[0].icon}@2x.png").into(weatherIcon)

                        // --- MAIN ---
                        println(it.main.temp - 273.15)
                        var sicaklik: String = String.format("%.0f", (it.main.temp - 273.15))
                        var nem: Int = it.main.humidity
                        var hissedilenYuksek: String = String.format("%.0f", (it.main.temp_max - 273.15))
                        var hissedilenDusuk: String = String.format("%.0f", (it.main.temp_min - 273.15))

                        tempText?.text = sicaklik + "'C"
                        humidityText?.text = "%"+nem
                        feelTempText?.text = hissedilenYuksek+"°C  /  "+hissedilenDusuk+"°C"

                        println(sicaklik)

                        // --- WIND ---
                        var ruzgarHizi: String = String.format("%.0f", (it.wind.speed))
                        windText?.text = ruzgarHizi+" m/s"

                        // --- SYS ---
                        var formatter = SimpleDateFormat("HH:mm")
                        var sunriseDate : String = formatter.format((it.sys.sunrise).toLong()*1000)
                        var sunsetDate : String = formatter.format((it.sys.sunset).toLong()*1000 )
                        var currentTime : String = formatter.format((it.dt).toLong()*1000 )

                        var formatter4 = SimpleDateFormat("EEEE - dd MMMM YYYY")
                        var currentDate : String = formatter4.format((it.dt).toLong()*1000 )

                        dateText?.text = currentDate.toLowerCase()
                        currentTimeText?.text = currentTime
                        sunriseText?.text = sunriseDate
                        sunsetText?.text = sunsetDate

                        var cityNameFromResponse : String = it.name
                        var splintedCityName : List<String> = cityNameFromResponse.split("Province")

                        // --- NAME ---
                        cityNameText?.text = splintedCityName[0]

                    }

                }

            }

        })

    }

//    private fun ShowPopup(v : View?){
//        myDialog!!.setContentView(R.layout.custompopup)
//    }


}