package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Rating;
import com.competition.recommend.repository.RatingRepository;
import com.competition.recommend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public List<Rating> getAllByUserId(Long userId) {
        return ratingRepository.findAllByUserId(userId);
    }

    @Override
    public List<Rating> getAllByMovieId(Long movieId) {
        return ratingRepository.findAllByMovieId(movieId);
    }

    @Override
    public void addRating(Long userId, Long movieId, String rating) {
        Rating saveRating = ratingRepository.findByUserIdAndAndMovieId(userId, movieId).orElse(new Rating());
        saveRating.setUserId(userId);
        saveRating.setMovieId(movieId);
        saveRating.setRating(rating);
        ratingRepository.save(saveRating);
    }

    @Override
    public void deleteRating(Long userId, Long movieId) {
        ratingRepository.findByUserIdAndAndMovieId(userId, movieId).ifPresent(deleteRating -> ratingRepository.delete(deleteRating));
    }
}
