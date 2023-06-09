package com.competition.recommend.service.impl;

import com.competition.recommend.entity.User;
import com.competition.recommend.repository.UserRepository;
import com.competition.recommend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User login(User user) {
        return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword()).orElse(null);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers(Long userId) {
        List<User> users = userRepository.findAll();
        User user = userRepository.findById(userId).orElse(null);
        users.remove(user);

        users.forEach(user1 -> {
            if (user1.getType() == null || !user1.getType().equals("admin"))
                user1.setType("user");
        });

        return users;
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return userRepository.findAll(pageRequest);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
