package com.competition.recommend.controller;

import com.competition.recommend.entity.RecommendResponse;
import com.competition.recommend.entity.RecommendStatus;
import com.competition.recommend.entity.User;
import com.competition.recommend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RecommendResponse<Map<String, String>> login(@RequestBody User user) {
        User loginResult = userService.login(user);
        Map<String, String> result = new HashMap<>();
        if (loginResult != null) {
            result.put("登录状态", "登录成功");
            result.put("用户类型", loginResult.getType());
        } else
            result.put("登录状态", "用户名或密码错误");
        return new RecommendResponse<>(RecommendStatus.SUCCESS, result);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RecommendResponse<Object> createUser(HttpServletRequest request) {
        RecommendResponse<Object> response = isAdmin(request);
        if (response != null)
            return response;

        User createUser = new User(request.getParameter("createUsername"),
                request.getParameter("createPassword"),
                request.getParameter("createType"));

        User existUser = userService.getUserByUsername(createUser.getUsername());
        if (existUser == null)
            return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.createUser(createUser));
        return new RecommendResponse<>(RecommendStatus.FAILURE, "新建用户已存在");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RecommendResponse<Object> updateUser(HttpServletRequest request) {
        RecommendResponse<Object> response = isAdmin(request);
        if (response != null)
            return response;

        User updateUser = new User(request.getParameter("updateUsername"),
                request.getParameter("updatePassword"),
                request.getParameter("updateType"));

        User existUser = userService.getUserByUsername(updateUser.getUsername());
        if (existUser != null) {
            updateUser.setId(existUser.getId());
            return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.updateUser(updateUser));
        }
        return new RecommendResponse<>(RecommendStatus.FAILURE, "修改用户不存在");

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public RecommendResponse<Object> deleteUser(HttpServletRequest request) {
        RecommendResponse<Object> response = isAdmin(request);
        if (response != null)
            return response;

        userService.deleteUser(Long.valueOf(request.getParameter("deleteId")));
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public RecommendResponse<Object> getAllUsers() {
        return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.getAllUsers());
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    public RecommendResponse<Object> getUserByUsername(String username) {
        return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.getUserByUsername(username));
    }

    private RecommendResponse<Object> isAdmin(HttpServletRequest request) {
        RecommendResponse<Object> response = null;
        User user = new User(request.getParameter("username"), request.getParameter("password"));
        User loginResult = userService.login(user);

        if (loginResult == null) {
            response = new RecommendResponse<>(RecommendStatus.UNAUTHORIZED, "登录异常");
        } else if (loginResult.getType() == null || !loginResult.getType().equals("admin"))
            response = new RecommendResponse<>(RecommendStatus.UNAUTHORIZED, "权限不足");
        return response;
    }

}