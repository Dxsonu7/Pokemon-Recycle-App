package com.example.project_api


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.marvelapp.PokeAdapter
import com.example.marvelapp.Pokemon
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pokemonAdapter: PokeAdapter
    private val pokemonList = mutableListOf<Pokemon>()
    private lateinit var requestQueue: RequestQueue // Initialize the request queue

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize the request queue in the onCreate method
        requestQueue = Volley.newRequestQueue(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.pokemonRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        pokemonAdapter = PokeAdapter(pokemonList)
        recyclerView.adapter = pokemonAdapter

        // Implement pagination and random Pokemon loading as the user scrolls.
        // For simplicity, let's use a basic example without endless scrolling.

        // Initial loading of random Pokemon
        loadRandomPokemon()

        // Implement a scroll listener for loading more Pokemon as the user scrolls
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Check if the user has scrolled to the bottom
                if (!recyclerView.canScrollVertically(1)) {
                    loadRandomPokemon()
                }
            }
        })
    }

    private fun loadRandomPokemon() {
        val randomPokemonId = (1..898).random()
        val apiUrl = "https://pokeapi.co/api/v2/pokemon/$randomPokemonId"

        val stringRequest = StringRequest(
            Request.Method.GET, apiUrl,
            { response ->
                try {
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

                    // Create a new Pokemon object and add it to the list
                    val pokemon = Pokemon(name, type, imageUrl)
                    pokemonList.add(pokemon)

                    // Notify the adapter that data has changed
                    pokemonAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e("JSONParseError", e.toString())
                }
            },
            { error ->
                Log.e("VolleyError", error.toString())
            })

        requestQueue.add(stringRequest)
    }
}