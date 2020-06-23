package com.burakkarakoca.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.burakkarakoca.weatherapp.R
import com.burakkarakoca.weatherapp.adapter.CitiesRecyclerViewAdapter
import com.burakkarakoca.weatherapp.adapter.CitiesRecyclerViewAdapter.OnItemClickListener
import com.burakkarakoca.weatherapp.adapter.SlidePagerAdapter
import com.burakkarakoca.weatherapp.fragments.MainFragment
import com.burakkarakoca.weatherapp.fragments.WeatherInfoFragment
import com.burakkarakoca.weatherapp.model.citiesOfTurkey.CitiesResponse
import com.burakkarakoca.weatherapp.service.WeatherAPI
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.add_city_popup.*
import kotlinx.android.synthetic.main.custompopup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

//private var adapter : SlidePagerAdapter? = null

class MainActivity : AppCompatActivity() , OnItemClickListener {

    private var BASE_URL: String = "https://gist.githubusercontent.com/weeq/3fdc85b0a81cc3a1ed6f019cb3c3abc7/raw/"
    var citiesResponseList: ArrayList<CitiesResponse>? = null
    var recyclerView : RecyclerView? = null
    var citiesRecyclerViewAdapter : CitiesRecyclerViewAdapter? = null
    var search: SearchView?= null

    var currentPage : Int = 0
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object{
        var adapter : SlidePagerAdapter? = null
        var viewPager : ViewPager? = null
        var cityList = ArrayList<String>()
        var arrayAdapter : ArrayAdapter<String>?= null
        var currentCityByCurrentLocation : String?= null

        var selectedCities : MutableList<String> = mutableListOf()

        private var myDialog : Dialog? = null

        fun closePopup(){
            myDialog!!.dismiss()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewPager = findViewById(R.id.main_viewPager)

        getLastLocation()

        myDialog = Dialog(this)

        println("viewPager current Item : " + viewPager?.currentItem)

        viewPager?.currentItem = currentPage+1

    }

    fun currentCitiesPopup(v : View?){

        myDialog!!.setContentView(R.layout.custompopup)

        // --Buradaki layout kısmına nasıl city_item.xml kısmındaki text çekilebilirdi?
        arrayAdapter = ArrayAdapter(this,R.layout.custom_textview, cityList)

        myDialog!!.listView.adapter = arrayAdapter
        myDialog!!.show()

        myDialog!!.listView.setOnItemClickListener { parent, view, position, id ->
            val element = arrayAdapter?.getItem(position)
            // seçili fragmenti çağır
            viewPager?.currentItem = position+1
            println(element)
            println(position)
            myDialog!!.dismiss()
        }

        myDialog!!.listView.setOnItemLongClickListener { parent, view, position, id ->
            val element = arrayAdapter?.getItem(position)
            val alert = AlertDialog.Builder(this)

            alert.setTitle("Şehiri Sil")
            alert.setMessage("$element sayfasını silmek istiyor musun?")

            alert.setPositiveButton("Evet") { dialogInterface: DialogInterface, i: Int ->
                // evet
                adapter?.removeFragment(adapter!!.getItem(position+1))
                cityList.remove(element.toString())
                myDialog!!.listView.adapter = arrayAdapter
                viewPager?.adapter = adapter
                viewPager?.currentItem = cityList.size +1
                Toast.makeText(applicationContext, "$element sayfası silindi",Toast.LENGTH_LONG).show() }

            alert.setNegativeButton("Hayır") {dialogInterface: DialogInterface, i: Int -> }

            alert.show()

            true
        }

    }

    fun addCityPopup(v : View?){
        myDialog!!.setContentView(R.layout.add_city_popup)
        myDialog!!.show()
        recyclerView = myDialog!!.city_recyclerView

        search = myDialog!!.findViewById(R.id.search_editText)
        getCitiesData()
    }

    private fun getCitiesData(){

        val retrofitCity = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofitCity.create(WeatherAPI::class.java)
        val callCities = service.getCitiesOfTurkey()

        callCities.enqueue(object : Callback<List<CitiesResponse>>{
            override fun onFailure(call: Call<List<CitiesResponse>>, t: Throwable) {
                println("err: Cities "+t.message)
            }

            override fun onResponse(
                call: Call<List<CitiesResponse>>,
                response: Response<List<CitiesResponse>>
            ) {

                if(response.isSuccessful){
                    val responseList: List<CitiesResponse> = response.body()!!
                    citiesResponseList = ArrayList<CitiesResponse>(responseList)
                }

                recyclerView!!.layoutManager = LinearLayoutManager(applicationContext)
                citiesRecyclerViewAdapter = CitiesRecyclerViewAdapter(citiesResponseList!!, this@MainActivity)
                recyclerView!!.adapter = citiesRecyclerViewAdapter

                searchName(search!!)

            }

        })

    }

    private fun searchName(searhView : SearchView){
        searhView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                citiesRecyclerViewAdapter!!.filter.filter(newText)
                return true
            }

        })
    }

    override fun onItemClick(item: CitiesResponse, position: Int) {
        Toast.makeText(this@MainActivity,"text $position",Toast.LENGTH_LONG).show()
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        var cityNameFromLocation2 : String? = null
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        println("location : " + location.latitude.toString())
                        println("location : " + location.longitude.toString())

                        var geocoder : Geocoder = Geocoder(this)
                        var addresses : List<Address>? = null
                        var cityNameFromGeocoder : String ?= null
                        addresses = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        var cityNameFromLocation : String = addresses[0].featureName.toString()
                        cityNameFromLocation2 = addresses[0].adminArea
                        // static güncel şehir ismi = adminArea
                        currentCityByCurrentLocation = addresses[0].adminArea
                        cityNameFromGeocoder = geocoder.getFromLocationName(cityNameFromLocation,1).toString()
                        println(cityNameFromLocation)
                        println(cityNameFromGeocoder)
                        println(cityNameFromLocation2)

                        adapter = SlidePagerAdapter(supportFragmentManager)

                        adapter?.addFragment(MainFragment(addresses[0].adminArea,true))
                        cityList.add(addresses[0].adminArea)
                        viewPager?.adapter = adapter


                    }

                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            println(mLastLocation.latitude.toString())
            println(mLastLocation.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }



    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //
            }
        }
    }

}

