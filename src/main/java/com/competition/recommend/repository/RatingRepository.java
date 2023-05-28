package com.competition.recommend.repository;

import com.competition.recommend.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllByUserId(Long userId);

    Page<Rating> findAllByUserId(Long userId, PageRequest pageRequest);

    List<Rating> findAllByMovieId(Long movieId);

    Page<Rating> findAllByMovieId(Long movieId, PageRequest pageRequest);

    Optional<Rating> findByUserIdAndAndMovieId(Long userId, Long movieId);
}
