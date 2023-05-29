package com.competition.recommend.controller;

import com.competition.recommend.entity.Movie;
import com.competition.recommend.entity.RecommendResponse;
import com.competition.recommend.entity.RecommendStatus;
import com.competition.recommend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value = "/getAllByPage", method = RequestMethod.GET)
    public RecommendResponse<Object> getAllByPage(int page, int size) {
        Page<Movie> movies = movieService.getAllMovies(page, size);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }

    @RequestMapping(value = "/getMovie", method = RequestMethod.GET)
    public RecommendResponse<Object> getMovie(String title) {
        Movie movie = movieService.getMovieByTitle(title);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movie);
    }

    @RequestMapping(value = "/getMovieById", method = RequestMethod.GET)
    public RecommendResponse<Object> getMovieById(Long movieId) {
        Movie movie = movieService.getMovieById(movieId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movie);
    }

    @RequestMapping(value = "/searchAll", method = RequestMethod.GET)
    public RecommendResponse<Object> searchAll(String title) {
        List<Movie> movies = movieService.searchMovieByTitle(title);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }

    @RequestMapping(value = "/searchAllByPage", method = RequestMethod.GET)
    public RecommendResponse<Object> searchAllByPage(String title, int page, int size) {
        Page<Movie> movies = movieService.searchMovieByTitle(title, page, size);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }

    @RequestMapping(value = "/getRecommend", method = RequestMethod.GET)
    public RecommendResponse<Object> getRecommend(Long userId, Long[] strangerId) {
        List<Movie> movies = movieService.getRecommendMovies(userId,strangerId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }
}
