package com.simple.data.model

data class Movie(
    val title: String,
    val poster_path: String,
    val overview: String,
    val vote_average: Float
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500$poster_path"
}

data class MovieResponse(
    val results: List<Movie>
)
