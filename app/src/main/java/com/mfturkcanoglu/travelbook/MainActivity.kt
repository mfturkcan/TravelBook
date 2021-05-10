package com.mfturkcanoglu.travelbook

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mfturkcanoglu.travelbook.Models.Place.Place
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var database : SQLiteDatabase
    private lateinit var travelPlaces : ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        try{
            database = openOrCreateDatabase("Travels",MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS travels (id INTEGER PRIMARY KEY,streetName VARCHAR, lat DOUBLE, long DOUBLE)")
            //database.execSQL("INSERT INTO travels (streetName, lat, long) VALUES ('Hatay', 33.2, 35.5)")

            database.execSQL("DELETE FROM travels WHERE streetName == 'Hatay'")
            var cursor = database.rawQuery("SELECT * FROM travels",null)

            var streetIndex = cursor.getColumnIndex("streetName")
            var latIndex = cursor.getColumnIndex("lat")
            var longIndex = cursor.getColumnIndex("long")

            travelPlaces = ArrayList<Place>()

            while(cursor.moveToNext()){
                travelPlaces.add(Place(cursor.getString(streetIndex),cursor.getDouble(latIndex),cursor.getDouble(longIndex)))
            }

            var adapter = ArrayAdapter(this,R.layout.place_list_item,R.id.list_item,travelPlaces)
            listView.adapter = adapter
            listView.onItemClickListener = AdapterView.OnItemClickListener {
                    parent, view, position, id ->
                var intent = Intent(this, MapsActivity:: class.java)
                intent.putExtra("placeName", travelPlaces[position].streetName)
                intent.putExtra("lat", travelPlaces[position].lat)
                intent.putExtra("long", travelPlaces[position].long)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

        }catch (e : Exception){
            println(e.stackTrace)
            println(e.message)
            println(e.cause)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.place_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent = Intent(this, MapsActivity:: class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

}