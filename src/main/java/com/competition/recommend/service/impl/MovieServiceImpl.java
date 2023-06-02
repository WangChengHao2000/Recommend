package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Friendship;
import com.competition.recommend.entity.Movie;
import com.competition.recommend.entity.Rating;
import com.competition.recommend.entity.User;
import com.competition.recommend.repository.FriendshipRepository;
import com.competition.recommend.repository.MovieRepository;
import com.competition.recommend.repository.RatingRepository;
import com.competition.recommend.repository.UserRepository;
import com.competition.recommend.service.MovieService;
import com.competition.recommend.util.THMDEM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Page<Movie> getAllMovies(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return movieRepository.findAll(pageRequest);
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return movieRepository.findByTitle(title).orElse(null);
    }

    @Override
    public Movie getMovieById(Long movieId) {
        return movieRepository.findById(movieId).orElse(null);
    }

    @Override
    public List<Movie> searchMovieByTitle(String title) {
        return movieRepository.findAllByTitleContains(title);
    }

    @Override
    public Page<Movie> searchMovieByTitle(String title, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return movieRepository.findAllByTitleContains(title, pageRequest);
    }

    @Override
    public List<Movie> getRecommendMovies(Long userId, Long[] strangerId) {
        int movieNumber = getAllMovies().size();
        List<Friendship> friendships = friendshipRepository.findAllByUserId(userId);
        int friendNumber = friendships.size();
        BigInteger[][] M_friend_avg = new BigInteger[friendNumber][2];
        //获取每个friend的平均评分 avg = avg[0]/avg[1].
        for(int i =0;i<friendNumber;i++){
            String friendName = friendships.get(i).getFriendName();
            User friend = userRepository.findByUsername(friendName).orElse(null);
            if(friend==null) continue;
            List<Rating> ratings = ratingRepository.findAllByUserId(friend.getId());
            M_friend_avg[i][1] = BigInteger.valueOf(ratings.size());
            M_friend_avg[i][0] = BigInteger.valueOf(0);
            for (Rating rating : ratings) {
                Map<String, String> mp = new HashMap<>();
                mp.put("C", rating.getC());
                mp.put("C_tag", rating.getC_tag());
                mp.put("C_ser", rating.getC_ser());
                mp.put("C_ser_tag", rating.getC_ser_tag());
                mp.put("C_csp", rating.getC_csp());
                Map<String, BigInteger> result = THMDEM.KeySwitch(mp, new BigInteger(friend.getP0()), THMDEM.sk_ser, THMDEM.sk_csp);
                BigInteger C_ser = result.get("C_ser");
                BigInteger cmpResult = THMDEM.Cmp(C_ser, THMDEM.System_tag1, 1);
                M_friend_avg[i][0] = M_friend_avg[i][0].add(cmpResult.multiply(C_ser));
            }
        }

        //获取每个stranger的平均评分  avg = avg[0]/avg[1].
        int strangerNumber = strangerId.length;
        BigInteger[][] M_stranger_avg = new BigInteger[strangerNumber][2];
        for(int i =0;i<strangerNumber;i++){
            User stranger = userRepository.findById(strangerId[i]).orElse(null);
            if(stranger==null) continue;
            List<Rating> ratings = ratingRepository.findAllByUserId(strangerId[i]);
            M_stranger_avg[i][1] = BigInteger.valueOf(ratings.size());
            M_stranger_avg[i][0] = BigInteger.valueOf(0);
            for (Rating rating : ratings) {
                Map<String, String> mp = new HashMap<>();
                mp.put("C", rating.getC());
                mp.put("C_tag", rating.getC_tag());
                mp.put("C_ser", rating.getC_ser());
                mp.put("C_ser_tag", rating.getC_ser_tag());
                mp.put("C_csp", rating.getC_csp());
                Map<String, BigInteger> result = THMDEM.KeySwitch(mp, new BigInteger(stranger.getP0()), THMDEM.sk_ser, THMDEM.sk_csp);
                BigInteger C_ser = result.get("C_ser");
                BigInteger cmpResult = THMDEM.Cmp(C_ser, THMDEM.System_tag1, 1);
                M_friend_avg[i][0] = M_friend_avg[i][0].add(cmpResult.multiply(C_ser));
                M_stranger_avg[i][0] = M_stranger_avg[i][0].add(new BigInteger(rating.getRating()));
            }
        }

        //获取目标用户的平均评分
        List<Rating> userRatings = ratingRepository.findAllByUserId(userId);
        User user = userRepository.findById(userId).orElse(null);
        assert user != null;
        BigInteger[] M_user_avg = new BigInteger[2];
        M_user_avg[1] = BigInteger.valueOf(userRatings.size());
        for (Rating userRating : userRatings) {
            Map<String,String> mp = new HashMap<>();
            mp.put("C",userRating.getC());
            mp.put("C_tag",userRating.getC_tag());
            mp.put("C_ser",userRating.getC_ser());
            mp.put("C_ser_tag",userRating.getC_ser_tag());
            mp.put("C_csp",userRating.getC_csp());
            Map<String,BigInteger> result = THMDEM.KeySwitch(mp, new BigInteger(user.getP0()),THMDEM.sk_ser,THMDEM.sk_csp);
            M_user_avg[0] = M_user_avg[0].add(new BigInteger(userRating.getRating()));
        }


        return null;
    }
}
