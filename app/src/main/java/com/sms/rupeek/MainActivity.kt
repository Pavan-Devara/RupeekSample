package com.sms.rupeek

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sms.rupeek.utils.ResourceStatus
import com.sms.rupeek.utils.StatusType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var gson = Gson()
    private lateinit var viewModelWeather: WeatherDataViewModel
    private val REQUEST_LOCATION = 1
    var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize viewmodel by lateint
        viewModelWeather = ViewModelProviders.of(this)[WeatherDataViewModel::class.java]

        //function to get latitude and longitude
        val nManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!nManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS()
        } else {
            getLocation()
        }

        //listener function to call the covid data api
        setUpListeners()
        //observers to observe whenever the api returns a response
        setupObservers()
    }

    private fun OnGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS").setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    getLocation()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("WrongConstant")
    private fun setupObservers() {
        //observing the response received from the api hit
        viewModelWeather.responseLiveData.observe(this, Observer {
            //check if we do not have any empty data
            if (it.data.size!=0){
                //intialize the recycler view
                val recyclerView = findViewById<RecyclerView>(R.id.weather_recycler_view)
                //define layout for the recycler view
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
                //initialize and set the recycler adapter with updated weather details
                val adapter = WeatherRecyclerViw(it.data)
                recyclerView.adapter = adapter
            }else{
                Toast.makeText(this, "Api hit not Successful", Toast.LENGTH_SHORT).show()
            }
        })
        //observe the status code of the api hit, whether it is a hit or fail
        viewModelWeather.apiCallStatus.observe(this, Observer {
            //processStatus function for taking action according to the status code of api
            processStatus(it)
        })
    }

    private fun setUpListeners() {
        //calling the api
        viewModelWeather.callWeatherData(this)
    }

    private fun processStatus(resource: ResourceStatus) {
        //check the returned status code of api hit
        when (resource.status) {
            StatusType.SUCCESS -> {
                Toast.makeText(this, "Api hit Successful", Toast.LENGTH_SHORT).show()
            }
            StatusType.EMPTY_RESPONSE -> {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
            }
            StatusType.PROGRESSING -> {

            }
            StatusType.ERROR -> {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
            }
            StatusType.LOADING_MORE -> {
                Toast.makeText(this, "Loading......", Toast.LENGTH_SHORT).show()
            }
            StatusType.NO_NETWORK -> {
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show()
            }
            StatusType.SESSION_EXPIRED -> {
                Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            val locationGPS =
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (locationGPS != null) {
                val lat: Double = locationGPS.getLatitude()
                val longi: Double = locationGPS.getLongitude()
                val latitude = lat.toString()
                val longitude = longi.toString()
                latLong.text = "Lat: "+ latitude.toString() + "   Long: "+ longitude.toString()
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}