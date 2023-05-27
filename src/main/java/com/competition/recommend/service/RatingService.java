package com.competition.recommend.service;

import com.competition.recommend.entity.Rating;

import java.util.List;

public interface RatingService {

    List<Rating> getAllByUserId(Long userId);

    List<Rating> getAllByMovieId(Long movieId);

    void addRating(Long userId, Long movieId, int rating);

    void deleteRating(Long userId, Long movieId);
}
