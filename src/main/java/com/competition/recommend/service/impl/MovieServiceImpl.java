package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Movie;
import com.competition.recommend.repository.MovieRepository;
import com.competition.recommend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return movieRepository.findByTitle(title).orElse(null);
    }

    @Override
    public List<Movie> searchMovieByTitle(String title) {
        return movieRepository.findAllByTitleContains(title);
    }
}