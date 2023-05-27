package com.competition.recommend.controller;

import com.competition.recommend.entity.Movie;
import com.competition.recommend.entity.RecommendResponse;
import com.competition.recommend.entity.RecommendStatus;
import com.competition.recommend.entity.User;
import com.competition.recommend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public RecommendResponse<Object> getAll() {
        List<Movie> movies = movieService.getAllMovies();
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }

    @RequestMapping(value = "/getMovie", method = RequestMethod.GET)
    public RecommendResponse<Object> getMovie(String title) {
        Movie movie = movieService.getMovieByTitle(title);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movie);
    }

    @RequestMapping(value = "/searchAll", method = RequestMethod.GET)
    public RecommendResponse<Object> searchAll(String title) {
        List<Movie> movies = movieService.searchMovieByTitle(title);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }
}
