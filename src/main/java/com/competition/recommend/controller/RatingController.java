package com.competition.recommend.controller;

import com.competition.recommend.entity.*;
import com.competition.recommend.service.MovieService;
import com.competition.recommend.service.RatingService;
import com.competition.recommend.service.UserService;
import com.competition.recommend.util.THMDEM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
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

        User user = userService.getUserByUserId(Long.valueOf(request.getParameter("userId")));
        String rating = request.getParameter("rating");
        BigInteger[] userKey = THMDEM.UserKeyGen();
        Map<String,String> map = THMDEM.Encrypt(Integer.parseInt(rating),userKey[0],userKey[1],userKey[3],userKey[4],
                new BigInteger(user.getP0()),userKey[6],userKey[7],THMDEM.pk_ser,THMDEM.pk_csp);
//C_ser,C_ser_tag,C_csp,C,C_tag

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
