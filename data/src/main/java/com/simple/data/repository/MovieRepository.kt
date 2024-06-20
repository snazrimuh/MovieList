package com.simple.data.repository

import com.simple.data.model.Movie

class MovieRepository(private val apiService: MovieApiService) {

    fun fetchMovies(): List<Movie> {
        return apiService.getMovies()
    }
}
interface MovieApiService {
    fun getMovies(): List<Movie>
}
