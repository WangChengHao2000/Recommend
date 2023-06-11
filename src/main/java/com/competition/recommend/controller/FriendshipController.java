package com.competition.recommend.controller;

import com.competition.recommend.entity.*;
import com.competition.recommend.service.FriendshipService;
import com.competition.recommend.service.MovieService;
import com.competition.recommend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private MovieService movieService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public RecommendResponse<Object> getAll(Long userId) {
        List<Friendship> friends = friendshipService.getAllFriends(userId);
        List<Rating> userRatings = ratingService.getAllByUserId(userId);
        List<FriendandMovie> res = new ArrayList<>();
        for (Friendship friendship:friends){
            List<Rating> friendRatings = ratingService.getAllByUserId(friendship.getFriendId());
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
            FriendandMovie friendandMovie = new FriendandMovie();
            friendandMovie.setMovies(movies);
            friendandMovie.setUserId(userId);
            friendandMovie.setRelation(friendship.getRelation());
            friendandMovie.setFriendName(friendship.getFriendName());
            friendandMovie.setFriendId(friendship.getFriendId());
            res.add(friendandMovie);
        }

        return new RecommendResponse<>(RecommendStatus.SUCCESS, res);
    }

    @RequestMapping(value = "/getAllStranger", method = RequestMethod.GET)
    public RecommendResponse<Object> getAllStranger(Long userId) {
        List<User> strangers = friendshipService.getAllStranger(userId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, strangers);
    }

    @RequestMapping(value = "/getAllByPage", method = RequestMethod.GET)
    public RecommendResponse<Object> getAllByPage(Long userId, int page, int size) {
        Page<User> friends = friendshipService.getAllFriends(userId, page, size);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, friends);
    }

    @RequestMapping(value = "/addFriend", method = RequestMethod.GET)
    public RecommendResponse<Object> addFriend(Long userId, String friendName) {
        friendshipService.addFriend(userId, friendName);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "add friend success");
    }

    @RequestMapping(value = "/deleteFriend", method = RequestMethod.GET)
    public RecommendResponse<Object> deleteFriend(Long userId, String friendName) {
        friendshipService.deleteFriend(userId, friendName);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "delete friend success");
    }
}
