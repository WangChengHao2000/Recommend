package com.competition.recommend.service;

import com.competition.recommend.entity.Rating;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RatingService {

    List<Rating> getAllByUserId(Long userId);

    Page<Rating> getAllByUserId(Long userId, int page, int size);

    List<Rating> getAllByMovieId(Long movieId);

    Page<Rating> getAllByMovieId(Long movieId, int page, int size);

    void addRating(Long userId, Long movieId, String title ,String rating, String C, String C_tag, String C_ser, String C_ser_tag, String C_csp);

    void deleteRating(Long userId, Long movieId);
}
