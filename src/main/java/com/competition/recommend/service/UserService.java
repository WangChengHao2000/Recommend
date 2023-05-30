package com.competition.recommend.service;

import com.competition.recommend.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User login(User user);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    List<User> getAllUsers(Long userId);

    Page<User> getAllUsers(int page, int size);

    User getUserByUsername(String username);

    User getUserByUserId(Long userId);

}