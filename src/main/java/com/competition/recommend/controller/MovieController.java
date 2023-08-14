package com.competition.recommend.controller;

import com.competition.recommend.entity.*;
import com.competition.recommend.service.FriendshipService;
import com.competition.recommend.service.MovieService;
import com.competition.recommend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private RatingService ratingService;

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

    @RequestMapping(value = "/getSameMovie", method = RequestMethod.GET)
    public RecommendResponse<Object> getSameMovie(Long userId, Long friendId) {
        List<Rating> userRatings = ratingService.getAllByUserId(userId);
        List<Rating> friendRatings = ratingService.getAllByUserId(friendId);
        List<Long> userMoviesIds = new ArrayList<>();
        List<Long> friendMovieIds = new ArrayList<>();
        for (Rating rating:userRatings){
            userMoviesIds.add(rating.getMovieId());
        }
        for (Rating rating:friendRatings){
            friendMovieIds.add(rating.getMovieId());
        }
        List<Long> intersection = userMoviesIds.stream().filter(friendMovieIds::contains).collect(Collectors.toList());
        List<Movie> movies = new ArrayList<>();
        for (Long id:intersection){
            movies.add(movieService.getMovieById(id));
        }
        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }

    @RequestMapping(value = "/getRecommend", method = RequestMethod.GET)
    public RecommendResponse<Object> getRecommend(Long userId) {

        List<User> strangers = friendshipService.getAllStranger(userId);

        List<Integer> list = new ArrayList<>();
        Random ra = new Random();
        int j=0;
        while (j<1) {
            int r = ra.nextInt(strangers.size());
            //利用collection集合下的contains方法来判断集合中是否存在生成的随机数
            if (!list.contains(r)) {
                list.add(r);
                j++;
            }
        }
        Long[] strangerId = new Long[1];
        for (int i = 0; i < 1; i++) {
            strangerId[i] = strangers.get(list.get(i)).getId();
        }
        System.out.println("正在随机挑选陌生人············");
        System.out.println("正在确定朋友列表·············");
        List<Movie> movies = movieService.getRecommendMovies(userId,strangerId);

        return new RecommendResponse<>(RecommendStatus.SUCCESS, movies);
    }
}
