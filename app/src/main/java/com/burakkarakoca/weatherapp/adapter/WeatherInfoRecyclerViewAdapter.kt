package com.burakkarakoca.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.burakkarakoca.weatherapp.R
import com.burakkarakoca.weatherapp.model.foreWeather.ListDaily
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.daily_weather_item.view.*
import java.util.*


class WeatherInfoRecyclerViewAdapter(private val exampleList: ArrayList<ListDaily>) :
    RecyclerView.Adapter<WeatherInfoRecyclerViewAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.daily_weather_item,
            parent, false)

        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        holder.bindModel(exampleList[position],position)
    }

    override fun getItemCount() = exampleList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tempText: TextView = itemView.daily_weather_item_temp_textView
        private val dateText: TextView = itemView.daily_weather_item_date_textView
        private val iconImage : ImageView = itemView.daily_weather_item_icon_imageView

        fun bindModel(listDaily : ListDaily, position : Int){
            tempText.text = String.format("%.0f", (listDaily.main.temp - 273.15)) + "°C"
            dateText.text = listDaily.dt_txt
            Picasso.get().load("http://openweathermap.org/img/wn/${listDaily.weather[0].icon}@2x.png").into(iconImage)
            //Picasso.get().load("http://openweathermap.org/img/wn/${weather.icon}@2x.png").into(weather_main_imageView)

        }

    }


}

