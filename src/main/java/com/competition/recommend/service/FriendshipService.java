package com.competition.recommend.service;

import com.competition.recommend.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FriendshipService {

    void addFriend(Long userId, String friendName);

    void deleteFriend(Long userId, String friendName);

    void deleteFriend(String friendName);

    List<User> getAllFriends(Long userId);

    Page<User> getAllFriends(Long userId, int page, int size);

    List<User> getAllStranger(Long userId);

}
