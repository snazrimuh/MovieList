package com.simple.movielist.ui.activity.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.data.model.Movie
import com.simple.movielist.R
import com.squareup.picasso.Picasso

class MovieAdapter(private val movieList: List<com.simple.data.model.Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.title.text = movie.title
        holder.rating.text = "Rating: ${movie.vote_average}"
        Picasso.get().load(movie.posterUrl).into(holder.poster)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("title", movie.title)
                putExtra("poster_url", movie.posterUrl)
                putExtra("overview", movie.overview)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = movieList.size

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.movie_title)
        val poster: ImageView = view.findViewById(R.id.movie_poster)
        val rating: TextView = view.findViewById(R.id.movie_rating)
    }
}
