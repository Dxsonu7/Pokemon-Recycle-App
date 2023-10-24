package com.example.project_api


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    //    var pokemonImageUrl = ""
    lateinit var requestQueue: RequestQueue // Initialize the request queue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the request queue in the onCreate method
        requestQueue = Volley.newRequestQueue(this)

        val loadPokemonButton = findViewById<Button>(R.id.buttonPokemon)
        loadPokemonButton.setOnClickListener {
            fetchRandomPokemon()
        }
    }

    private fun fetchRandomPokemon() {
        // Generate a random Pokemon ID between 1 and 898 (the total number of Pokemon)
        val randomPokemonId = (1..898).random()
        val apiUrl = "https://pokeapi.co/api/v2/pokemon/$randomPokemonId"

        val stringRequest = StringRequest(
            Request.Method.GET, apiUrl,
            { response ->
                try {
                    // Parse the JSON response
                    val jsonObject = JSONObject(response)
                    val name = jsonObject.getString("name")

                    val types = jsonObject.getJSONArray("types")
                    val typeList = mutableListOf<String>()
                    for (i in 0 until types.length()) {
                        val typeName = types.getJSONObject(i).getJSONObject("type").getString("name")
                        typeList.add(typeName)
                    }
                    val type = typeList.joinToString(", ")

                    val imageUrl = jsonObject.getJSONObject("sprites").getString("front_default")

                    // Update the UI with the fetched data
                    updateUI(name, type, imageUrl)
                } catch (e: JSONException) {
                    Log.e("JSONParseError", e.toString())
                }
            },
            { error ->
                // Handle errors here (e.g., network error)
                Log.e("VolleyError", error.toString())
            })

        // Add the request to the queue
        requestQueue.add(stringRequest)
    }

    private fun updateUI(name: String, type: String, imageUrl: String) {
        // Update the UI elements (ImageView, TextViews) with the data
        val pokemonImageView = findViewById<ImageView>(R.id.pokemonImageView)
        val nameTextView = findViewById<TextView>(R.id.pokeName)
        val typeTextView = findViewById<TextView>(R.id.pokeType)

        // Load the image using an image loading library like Glide (similar to your previous code)
        Glide.with(this).load(imageUrl).into(pokemonImageView)

        // Update TextViews with the name and type
        nameTextView.text = "$name"
        typeTextView.text = "$type"
    }

}