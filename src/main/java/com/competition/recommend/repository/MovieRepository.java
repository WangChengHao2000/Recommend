package com.competition.recommend.repository;

import com.competition.recommend.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTitle(String title);

    List<Movie> findAllByTitleContains(String title);

    Page<Movie> findAllByTitleContains(String title, PageRequest pageRequest);

}
