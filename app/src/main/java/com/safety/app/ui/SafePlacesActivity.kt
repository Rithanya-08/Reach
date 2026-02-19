package com.safety.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.safety.app.R

class SafePlacesActivity : AppCompatActivity() {

    private lateinit var adapter: SafePlacesAdapter
    private val placesList = listOf(
        SafePlace("Police Station", "Find nearest police station", null, 0.0, 0.0),
        SafePlace("Hospital", "Find nearest hospital", null, 0.0, 0.0),
        SafePlace("Pharmacy", "Find nearest pharmacy", null, 0.0, 0.0),
        SafePlace("Bus Stop", "Find nearest bus stop", null, 0.0, 0.0),
        SafePlace("Gas Station", "Find nearest gas station", null, 0.0, 0.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_places)
        
        supportActionBar?.title = "Find Safe Places"

        val rv = findViewById<RecyclerView>(R.id.rvSafePlaces)
        // Hide progress bar as we don't need it for static list
        findViewById<View>(R.id.progressBar).visibility = View.GONE
        
        rv.layoutManager = LinearLayoutManager(this)
        adapter = SafePlacesAdapter(placesList)
        rv.adapter = adapter
    }

    class SafePlacesAdapter(private val places: List<SafePlace>) : RecyclerView.Adapter<SafePlacesAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvPlaceName)
            val address: TextView = view.findViewById(R.id.tvPlaceAddress)
            val btnCall: Button = view.findViewById(R.id.btnCallPlace) // We'll hide this
            val btnNav: Button = view.findViewById(R.id.btnNavigatePlace)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val place = places[position]
            holder.name.text = place.name
            holder.address.text = place.address
            
            holder.btnCall.visibility = View.GONE // No phone number for categories
            holder.btnNav.text = "Search on Map"
            
            holder.btnNav.setOnClickListener {
                val query = Uri.encode(place.name)
                val gmmIntentUri = Uri.parse("geo:0,0?q=$query")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(it.context.packageManager) != null) {
                    it.context.startActivity(mapIntent)
                } else {
                    // Fallback if Maps app isn't installed
                    it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")))
                }
            }
        }

        override fun getItemCount() = places.size
    }
}
