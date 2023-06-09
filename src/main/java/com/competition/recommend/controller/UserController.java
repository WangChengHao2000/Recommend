package com.competition.recommend.controller;

import com.competition.recommend.entity.Rating;
import com.competition.recommend.entity.RecommendResponse;
import com.competition.recommend.entity.RecommendStatus;
import com.competition.recommend.entity.User;
import com.competition.recommend.service.FriendshipService;
import com.competition.recommend.service.UserService;
import com.competition.recommend.util.MySM2;
import com.competition.recommend.util.THMDEM;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RecommendResponse<Map<String, String>> login(@RequestBody User user) {
        User loginResult = userService.login(user);
        Map<String, String> result = new HashMap<>();
        if (loginResult != null) {
            result.put("status", "登录成功");
            result.put("type", loginResult.getType());
            result.put("userId", String.valueOf(loginResult.getId()));
        } else
            result.put("status", "用户名或密码错误");
        return new RecommendResponse<>(RecommendStatus.SUCCESS, result);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RecommendResponse<Object> createUser(HttpServletRequest request) {
//        RecommendResponse<Object> response = isAdmin(request);
//        if (response != null)
//            return response;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPair sm2Key = MySM2.createSm2Key();
        //获取公钥
        PublicKey publicKey = sm2Key.getPublic();
        //获取公钥base加密后字符串
        String publicStr = Base64.encodeBase64String(publicKey.getEncoded());
        //获取私钥
        PrivateKey privateKey = sm2Key.getPrivate();
        //获取私钥base加密后字符串
        String privateStr = Base64.encodeBase64String(privateKey.getEncoded());

        String p0 = String.valueOf(THMDEM.GenPrime(31));
        User createUser = new User(request.getParameter("createUsername"),
                request.getParameter("createPassword"),
                Integer.parseInt(request.getParameter("createAge")),
                request.getParameter("createGender"),
                request.getParameter("createOccupation"),
                publicStr,
                privateStr,
                p0
                );

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
                Integer.parseInt(request.getParameter("updateAge")),
                request.getParameter("updateGender"),
                request.getParameter("updateOccupation"),null,null,null);

        User existUser = userService.getUserByUsername(updateUser.getUsername());
        if (existUser != null) {
            updateUser.setPublickey(existUser.getPublickey());
            updateUser.setPrivatekey(existUser.getPrivatekey());
            updateUser.setP0(existUser.getP0());
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
        User delUser = userService.getUserByUserId(Long.valueOf(request.getParameter("deleteId")));
        friendshipService.deleteFriend(delUser.getUsername());
        userService.deleteUser(Long.valueOf(request.getParameter("deleteId")));
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public RecommendResponse<Object> getAllUsers(Long userId) {
        return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.getAllUsers(userId));
    }

    @RequestMapping(value = "/getAllByPage", method = RequestMethod.POST)
    public RecommendResponse<Object> getAllUsersByPage(HttpServletRequest request) {
        int page = Integer.parseInt(request.getParameter("page"));
        int size = Integer.parseInt(request.getParameter("size"));
        return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.getAllUsers(page, size));
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public RecommendResponse<Object> getUserByUsername(HttpServletRequest request) {
        String username = request.getParameter("username");
        System.out.println(username);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.getUserByUsername(username));
    }

    @RequestMapping(value = "/getUserById", method = RequestMethod.POST)
    public RecommendResponse<Object> getUserById(Long userId) {
        return new RecommendResponse<>(RecommendStatus.SUCCESS, userService.getUserByUserId(userId));
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
