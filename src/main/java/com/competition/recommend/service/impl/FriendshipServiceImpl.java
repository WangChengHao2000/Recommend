package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Friendship;
import com.competition.recommend.entity.Rating;
import com.competition.recommend.entity.User;
import com.competition.recommend.repository.FriendshipRepository;
import com.competition.recommend.repository.RatingRepository;
import com.competition.recommend.repository.UserRepository;
import com.competition.recommend.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public void addFriend(Long userId, String friendName) {
        if (!isFriend(userId, friendName)) {
            Friendship friendship = new Friendship();
            friendship.setUserId(userId);
            friendship.setFriendName(friendName);

            User friend = userRepository.findByUsername(friendName).orElse(null);

            assert friend != null;

            Long friendId = friend.getId();

            List<Rating> userRatings = ratingRepository.findAllByUserId(userId);
            List<Rating> friendRatings = ratingRepository.findAllByUserId(friendId);

            Set<Long> userRatingId = new HashSet<>();
            for (Rating rating : userRatings) {
                userRatingId.add(rating.getMovieId());
            }
            Set<Long> friendRatingId = new HashSet<>();
            for (Rating rating : friendRatings) {
                friendRatingId.add(rating.getMovieId());
            }

            int totalUserCount = userRatingId.size();
            int totalFriendCount = friendRatingId.size();
            userRatingId.retainAll(friendRatingId);
            int sameCount = userRatingId.size();

            int relation = sameCount * 100 / (totalUserCount + totalFriendCount - sameCount);

            friendship.setRelation(relation);

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
    public void deleteFriend(String friendName) {
        friendshipRepository.deleteFriendshipsByFriendName(friendName);
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

        userList.forEach(user -> {
            if (user.getType() == null || !user.getType().equals("admin"))
                user.setType("user");
        });

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

    @Override
    public List<User> getAllStranger(Long userId) {
        List<User> userList = userRepository.findAll();
        List<Friendship> friendshipList = friendshipRepository.findAllByUserId(userId);
        for (Friendship friendship : friendshipList) {
            User user = userRepository.findByUsername(friendship.getFriendName()).orElse(null);
            if (user != null)
                userList.remove(user);
        }
        User user = userRepository.findById(userId).orElse(null);
        userList.remove(user);
        return userList;
    }


    private boolean isFriend(Long userId, String friendName) {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendName(userId, friendName).orElse(null);
        return friendship != null;
    }
}
