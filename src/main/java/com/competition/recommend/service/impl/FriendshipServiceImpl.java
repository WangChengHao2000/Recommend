package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Friendship;
import com.competition.recommend.entity.User;
import com.competition.recommend.repository.FriendshipRepository;
import com.competition.recommend.repository.UserRepository;
import com.competition.recommend.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addFriend(Long userId, String friendName) {
        if (!isFriend(userId, friendName)) {
            Friendship friendship = new Friendship();
            friendship.setUserId(userId);
            friendship.setFriendName(friendName);
            friendshipRepository.save(friendship);
        }
    }

    @Override
    public void deleteFriend(Long userId, String friendName) {
        if (isFriend(userId, friendName)) {
            friendshipRepository.deleteFriendshipByUserIdAndFriendName(userId, friendName);
        }
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        List<Friendship> friendshipList = friendshipRepository.findAllByUserId(userId);
        List<User> userList = new ArrayList<>();
        for (Friendship friendship : friendshipList) {
            User user = userRepository.findByUsername(friendship.getFriendName()).orElse(null);
            if (user != null)
                userList.add(user);
        }
        return userList;
    }

    @Override
    public Page<User> getAllFriends(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Friendship> friendshipPage = friendshipRepository.findAllByUserId(userId, pageRequest);
        return new PageImpl<>(friendshipPage.getContent().stream()
                .map(friendship -> userRepository.findByUsername(friendship.getFriendName()).orElse(null)).collect(Collectors.toList()),
                pageRequest, friendshipPage.getTotalElements());
    }

    private boolean isFriend(Long userId, String friendName) {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendName(userId, friendName).orElse(null);
        return friendship != null;
    }
}
