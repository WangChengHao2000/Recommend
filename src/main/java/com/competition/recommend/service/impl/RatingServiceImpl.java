package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Rating;
import com.competition.recommend.repository.RatingRepository;
import com.competition.recommend.repository.UserRepository;
import com.competition.recommend.service.RatingService;
import com.competition.recommend.util.THMDEM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;


    @Override
    public List<Rating> getAllByUserId(Long userId) {
        return ratingRepository.findAllByUserId(userId);
    }

    @Override
    public Page<Rating> getAllByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ratingRepository.findAllByUserId(userId, pageRequest);
    }

    @Override
    public List<Rating> getAllByMovieId(Long movieId) {
        return ratingRepository.findAllByMovieId(movieId);
    }

    @Override
    public Page<Rating> getAllByMovieId(Long movieId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ratingRepository.findAllByMovieId(movieId, pageRequest);
    }

    @Override
    public void addRating(Long userId, Long movieId, String title ,String rating, String C, String C_tag, String C_ser, String C_ser_tag, String C_csp) {
        Rating saveRating = ratingRepository.findByUserIdAndAndMovieId(userId, movieId).orElse(new Rating());
        saveRating.setUserId(userId);
        saveRating.setMovieId(movieId);
        saveRating.setTitle(title);
        saveRating.setRating(rating);
        saveRating.setC(C);
        saveRating.setC_tag(C_tag);
        saveRating.setC_ser(C_ser);
        saveRating.setC_ser(C_ser);
        saveRating.setC_ser_tag(C_ser_tag);
        saveRating.setC_csp(C_csp);

        ratingRepository.save(saveRating);
    }

    @Override
    public void deleteRating(Long userId, Long movieId) {
        ratingRepository.findByUserIdAndAndMovieId(userId, movieId).ifPresent(deleteRating -> ratingRepository.delete(deleteRating));
    }
}
