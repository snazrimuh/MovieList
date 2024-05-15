package com.simple.movielist.data

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.simple.movielist.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieTitle = intent.getStringExtra("title")
        val moviePosterUrl = intent.getStringExtra("poster_url")
        val movieOverview = intent.getStringExtra("overview")

        binding.detailMovieTitle.text = movieTitle
        binding.detailMovieOverview.text = movieOverview
        Picasso.get().load(moviePosterUrl).into(binding.detailMoviePoster)
    }
}

