package com.competition.recommend.repository;

import com.competition.recommend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllByUserId(Long userId);

    List<Rating> findAllByMovieId(Long movieId);

    Optional<Rating> findByUserIdAndAndMovieId(Long userId, Long movieId);
}
