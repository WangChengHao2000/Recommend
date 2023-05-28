package com.competition.recommend.service;

import com.competition.recommend.entity.User;

import java.util.List;

public interface FriendshipService {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getAllFriends(Long userId);

}
