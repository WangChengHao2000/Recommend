package com.competition.recommend.controller;

import com.competition.recommend.entity.*;
import com.competition.recommend.service.MovieService;
import com.competition.recommend.service.RatingService;
import com.competition.recommend.service.UserService;
import com.competition.recommend.util.THMDEM;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getByUser", method = RequestMethod.GET)
    public RecommendResponse<Object> getByUser(Long userId) {
        List<Rating> ratings = ratingService.getAllByUserId(userId);

        return new RecommendResponse<>(RecommendStatus.SUCCESS, ratings);
    }

    @RequestMapping(value = "/getByUserByPage", method = RequestMethod.GET)
    public RecommendResponse<Object> getByUserByPage(Long userId, int page, int size) {
        Page<Rating> ratings = ratingService.getAllByUserId(userId, page, size);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, ratings);
    }

    @RequestMapping(value = "/getByMovie", method = RequestMethod.GET)
    public RecommendResponse<Object> getByMovie(Long movieId) {
        List<Rating> ratings = ratingService.getAllByMovieId(movieId);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, ratings);
    }

    @RequestMapping(value = "/getByMovieByPage", method = RequestMethod.GET)
    public RecommendResponse<Object> getByMovieByPage(Long movieId, int page, int size) {
        Page<Rating> ratings = ratingService.getAllByMovieId(movieId, page, size);
        return new RecommendResponse<>(RecommendStatus.SUCCESS, ratings);
    }

    @RequestMapping(value = "/addRating", method = RequestMethod.POST)
    public RecommendResponse<Object> addRating(HttpServletRequest request) {
        StopWatch stopWatch = new StopWatch();
        User user = userService.getUserByUserId(Long.valueOf(request.getParameter("userId")));
        String rating = request.getParameter("rating");
        Security.addProvider(new BouncyCastleProvider());


        System.out.println("正在为用户"+user.getUsername()+"加密对电影"+request.getParameter("title")+"的评分");
        System.out.println("待加密的评分为："+rating);
        System.out.println();
        stopWatch.start();
        BigInteger[] userKey = THMDEM.UserKeyGen();
        System.out.println("公钥为：");
        System.out.println("服务器SER的SM2公钥："+THMDEM.pk_ser);
        System.out.println("服务器CSP的SM2公钥："+THMDEM.pk_csp);
        System.out.println("对称加密算法的公开参数T："+userKey[4]);
        System.out.println("对称加密算法的公开参数p0："+user.getP0());
        Map<String,String> map = THMDEM.Encrypt(Integer.parseInt(rating),userKey[0],userKey[1],userKey[3],userKey[4],
                new BigInteger(user.getP0().replace("\t","")),userKey[6],userKey[7],THMDEM.pk_ser,THMDEM.pk_csp);
//C_ser,C_ser_tag,C_csp,C,C_tag
        stopWatch.stop();
        System.out.println();
        System.out.println("加密结果为：");
        System.out.println("C:"+map.get("C"));
        System.out.println("C_tag:"+map.get("C_tag"));
        System.out.printf("加密耗时：%d 毫秒.%n", stopWatch.getTotalTimeMillis());
        System.out.println();
        ratingService.addRating(Long.parseLong(request.getParameter("userId")),
                Long.parseLong(request.getParameter("movieId")),
                request.getParameter("title"),
                request.getParameter("rating"),
                map.get("C"),
                map.get("C_tag"),
                map.get("C_ser"),
                map.get("C_ser_tag"),
                map.get("C_csp"));
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "add rating success");
    }

    @RequestMapping(value = "/deleteRating", method = RequestMethod.POST)
    public RecommendResponse<Object> deleteRating(HttpServletRequest request) {
        ratingService.deleteRating(Long.parseLong(request.getParameter("userId")),
                Long.parseLong(request.getParameter("movieId")));
        return new RecommendResponse<>(RecommendStatus.SUCCESS, "delete rating success");
    }
}
