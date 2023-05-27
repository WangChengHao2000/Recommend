package com.competition.recommend.service;

import com.competition.recommend.entity.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getAllMovies();

    Movie getMovieByTitle(String title);

    List<Movie> searchMovieByTitle(String title);

    List<Movie> getRecommendMovies(Long userId);
}
