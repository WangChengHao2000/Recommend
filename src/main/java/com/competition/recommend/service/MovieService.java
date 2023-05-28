package com.competition.recommend.service;

import com.competition.recommend.entity.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {

    List<Movie> getAllMovies();

    Page<Movie> getAllMoviesByPage(int page, int size);

    Movie getMovieByTitle(String title);

    Movie getMovieById(Long movieId);

    List<Movie> searchMovieByTitle(String title);

    Page<Movie> searchMovieByTitle(String title, int page, int size);

    List<Movie> getRecommendMovies(Long userId);
}
