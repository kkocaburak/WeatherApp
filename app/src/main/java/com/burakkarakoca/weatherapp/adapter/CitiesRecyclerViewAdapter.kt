package com.burakkarakoca.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.burakkarakoca.weatherapp.R
import com.burakkarakoca.weatherapp.fragments.MainFragment
import com.burakkarakoca.weatherapp.model.citiesOfTurkey.CitiesResponse
import com.burakkarakoca.weatherapp.view.MainActivity
import kotlinx.android.synthetic.main.city_item.view.*
import kotlin.collections.ArrayList


class CitiesRecyclerViewAdapter() :
    RecyclerView.Adapter<CitiesRecyclerViewAdapter.ExampleViewHolder>(), Filterable {



    companion object{
        lateinit var exampleList: ArrayList<CitiesResponse>
        lateinit var exampleListFull: ArrayList<CitiesResponse>
        lateinit var context : Context
    }

    constructor(exampleList: ArrayList<CitiesResponse>, context: Context) : this() {
        CitiesRecyclerViewAdapter.exampleList = exampleList
        CitiesRecyclerViewAdapter.exampleListFull = exampleList
        CitiesRecyclerViewAdapter.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.city_item,
            parent, false)

        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        holder.bindModel(exampleListFull[position],position)
    }

    override fun getItemCount() = exampleListFull.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cityText: TextView = itemView.city_item_city_textView
        var isAlreadyExist : Boolean = false

        fun bindModel(citiesDaily : CitiesResponse, position : Int){
            cityText.text = citiesDaily.il

            itemView.setOnClickListener{
                println("clicked : " + position + " city : " + citiesDaily.il)

                if(citiesDaily.il in MainActivity.cityList){
                    println(MainActivity.cityList.indexOf(citiesDaily.il))
                    MainActivity.viewPager?.currentItem = MainActivity.cityList.indexOf(citiesDaily.il)+2
                    MainActivity.closePopup()
                } else{
                    MainActivity.cityList.add(citiesDaily.il)

                    MainActivity.adapter?.addFragment(MainFragment(citiesDaily.il,false))
                    MainActivity.viewPager?.adapter = MainActivity.adapter
                    MainActivity.viewPager?.currentItem = MainActivity.cityList.size +2

                    MainActivity.closePopup()
                }



                for (cityName in MainActivity.selectedCities){
                    if(cityName == citiesDaily.il){
//                        MainActivity.viewPager?.currentItem = MainActivity.cityList.size +2
                        println("already exist")
                        MainActivity.closePopup()
                    }else{


                    }
                }




            }

        }

    }

    interface OnItemClickListener{
        fun onItemClick(item:CitiesResponse, position: Int)
    }


    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString: String = constraint.toString()
                if (charString.isEmpty()){
                    exampleListFull = exampleList
                } else{
                    var filteredList: MutableList<CitiesResponse> = mutableListOf()
                    for(filtered : CitiesResponse in exampleList){
                        if(filtered.il.toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(filtered)
                        }
                    }
                    exampleListFull = filteredList as ArrayList<CitiesResponse>

                }

                var filterResult = FilterResults()
                filterResult.values = exampleListFull

                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                exampleListFull = results!!.values as ArrayList<CitiesResponse>
                notifyDataSetChanged()
            }

        }
    }

}


