package com.competition.recommend.controller;

import com.competition.recommend.entity.Movie;
import com.competition.recommend.entity.RecommendResponse;
import com.competition.recommend.entity.RecommendStatus;
import com.competition.recommend.entity.User;
import com.competition.recommend.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public RecommendResponse<Object> getAll(Long userId) {
        List<User> friends = friendshipService.getAllFriends(userId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, friends);
    }

    @RequestMapping(value = "/addFriend", method = RequestMethod.POST)
    public RecommendResponse<Object> addFriend(Long userId, Long friendId) {
        friendshipService.addFriend(userId, friendId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "add friend success");
    }

    @RequestMapping(value = "/deleteFriend", method = RequestMethod.POST)
    public RecommendResponse<Object> deleteFriend(Long userId, Long friendId) {
        friendshipService.deleteFriend(userId, friendId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "delete friend success");
    }
}