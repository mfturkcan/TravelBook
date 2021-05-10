package com.mfturkcanoglu.travelbook

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mfturkcanoglu.travelbook.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var currentLatLng : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(mapLongClickListener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        // Add a marker in Sydney and move the camera

        /*
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
         */

        var intent = intent
        var placeName : String? = null
        var lat : Double? = null
        var long : Double? = null

        placeName = intent.getStringExtra("placeName")
        lat = intent.getDoubleExtra("lat",0.0)
        long = intent.getDoubleExtra("long", 0.0)

        println(lat)
        println(long)
        println(placeName)
        if(!placeName.isNullOrEmpty() && lat != 0.0 && long != 0.0)
        {
            println("is not empty!")
            currentLatLng = LatLng(lat, long)
            mMap.addMarker(MarkerOptions().position(currentLatLng).title(placeName))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                }
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
            else{
                locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if(location != null){
                            currentLatLng = LatLng(location.latitude,location.longitude)
                            mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
                        }
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
             }


        }

        
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1 && grantResults.isNotEmpty())
        {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
            }
        }
    }

    val mapLongClickListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng) {
            if(p0 != null){
                try{
                    var geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    var address =  geocoder.getFromLocation(p0.latitude, p0.longitude, 1)[0]

                    var db = openOrCreateDatabase("Travels", MODE_PRIVATE, null)
                    var alertDialog = AlertDialog.Builder(this@MapsActivity)
                    alertDialog.setTitle("Are you sure about saving this place?")
                    alertDialog.setMessage("The place is : ${address.thoroughfare}")
                    alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener {
                            dialog, which -> try{
                        db.execSQL("INSERT INTO travels (streetName, lat, long) VALUES ('${address.thoroughfare}', ${p0.latitude}, ${p0.longitude})")
                    }catch(e : Exception){
                        println(e.message)
                    }
                    var intent = Intent(this@MapsActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    })
                    alertDialog.setNegativeButton("No",DialogInterface.OnClickListener { dialog, which ->
                        println("Never mind!")
                    })
                    alertDialog.show()
                }catch(e : Exception){
                    println(e.message)
                }
            }
        }
    }


}