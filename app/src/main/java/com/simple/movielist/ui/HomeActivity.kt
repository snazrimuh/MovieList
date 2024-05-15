package com.simple.movielist.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simple.movielist.data.ApiClient
import com.simple.movielist.data.DataStoreManager
import com.simple.movielist.data.MovieResponse
import com.simple.movielist.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dataStoreManager = DataStoreManager(this)

        val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        val buttonViewProfile = findViewById<Button>(R.id.buttonViewProfile)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenStarted {
            dataStoreManager.username.collect { username ->
                textViewWelcome.text = "Welcome, $username!"
            }
        }

        buttonLogout.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                dataStoreManager.saveLoginState(false, "")
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        buttonViewProfile.setOnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        fetchMovies()
    }

    private fun fetchMovies() {
        val call = ApiClient.apiService.getPopularMovies("3eafb1d6a440475fa82592a3ddb64769")

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    if (movies != null) {
                        adapter = MovieAdapter(movies)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Failed to load movies", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

