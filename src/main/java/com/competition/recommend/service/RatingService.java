package com.competition.recommend.service;

import com.competition.recommend.entity.Rating;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RatingService {

    List<Rating> getAllByUserName(String username);

    Page<Rating> getAllByUserName(String username, int page, int size);

    List<Rating> getAllByMovieId(Long movieId);

    Page<Rating> getAllByMovieId(Long movieId, int page, int size);

    void addRating(Long userId, Long movieId, String rating);

    void deleteRating(Long userId, Long movieId);
}
