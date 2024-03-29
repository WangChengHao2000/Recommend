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
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.*;

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
        StopWatch stopWatch = new StopWatch();

        System.out.println("正在利用双服务器转换并计算每个朋友的评分密文···········");
        List<Long> movieIds = new ArrayList<>();
        List<Friendship> friendships = friendshipRepository.findAllByUserId(userId);
        int friendNumber = friendships.size();
        BigInteger[][] M_friend_avg = new BigInteger[friendNumber][2];
        //获取每个friend的平均评分 avg = avg[0]/avg[1].
        for(int i =0;i<friendNumber;i++){
            User friend = userRepository.findByUsername(friendships.get(i).getFriendName()).orElse(null);
            if(friend==null) continue;
            List<Rating> ratings = ratingRepository.findAllByUserId(friend.getId());
            M_friend_avg[i][1] = BigInteger.valueOf(0);
            M_friend_avg[i][0] = BigInteger.valueOf(0);
            for (Rating rating : ratings) {
                if (!movieIds.contains(rating.getMovieId()))
                    movieIds.add(rating.getMovieId());
                Map<String, String> mp = new HashMap<>();
                mp.put("C", rating.getC().replace("\t",""));
                mp.put("C_tag", rating.getC_tag().replace("\t",""));
                mp.put("C_ser", rating.getC_ser());
                mp.put("C_ser_tag", rating.getC_ser_tag());
                mp.put("C_csp", rating.getC_csp());
                Map<String, BigInteger> result = THMDEM.KeySwitch(mp, new BigInteger(friend.getP0().replace("\t","")), THMDEM.sk_ser, THMDEM.sk_csp);
                BigInteger C_ser = result.get("C_ser");
                BigInteger C_ser_tag = result.get("C_ser_tag");
                BigInteger cmpResult = THMDEM.Cmp(C_ser_tag, THMDEM.System_tag1, 1);
                BigInteger res = cmpResult.multiply(C_ser).multiply(THMDEM.System_r.modInverse(THMDEM.System_N)).mod(THMDEM.System_N);
                if (res.compareTo(BigInteger.valueOf(5)) > 0) continue;
                M_friend_avg[i][1] = M_friend_avg[i][1].add(BigInteger.valueOf(1));
                M_friend_avg[i][0] = M_friend_avg[i][0].add(cmpResult.multiply(C_ser)).mod(THMDEM.System_T);
            }
        }

        System.out.println("正在利用双服务器转换并计算每个陌生人的评分密文···········");
        //获取每个stranger的平均评分  avg = avg[0]/avg[1].
        int strangerNumber = strangerId.length;
        BigInteger[][] M_stranger_avg = new BigInteger[strangerNumber][2];
        for(int i =0;i<strangerNumber;i++){
            User stranger = userRepository.findById(strangerId[i]).orElse(null);
            if(stranger==null) continue;
            List<Rating> ratings = ratingRepository.findAllByUserId(strangerId[i]);
            M_stranger_avg[i][1] = BigInteger.valueOf(0);
            M_stranger_avg[i][0] = BigInteger.valueOf(0);
            for (Rating rating : ratings) {
                if (!movieIds.contains(rating.getMovieId()))
                    movieIds.add(rating.getMovieId());
                Map<String, String> mp = new HashMap<>();
                mp.put("C", rating.getC().replace("\t",""));
                mp.put("C_tag", rating.getC_tag().replace("\t",""));
                mp.put("C_ser", rating.getC_ser());
                mp.put("C_ser_tag", rating.getC_ser_tag());
                mp.put("C_csp", rating.getC_csp());
                Map<String, BigInteger> result = THMDEM.KeySwitch(mp, new BigInteger(stranger.getP0().replace("\t","")), THMDEM.sk_ser, THMDEM.sk_csp);
                BigInteger C_ser = result.get("C_ser");
                BigInteger C_ser_tag = result.get("C_ser_tag");
                BigInteger cmpResult = THMDEM.Cmp(C_ser_tag, THMDEM.System_tag1, 1);
                BigInteger res = cmpResult.multiply(C_ser).multiply(THMDEM.System_r.modInverse(THMDEM.System_N)).mod(THMDEM.System_N);
                if (res.compareTo(BigInteger.valueOf(5)) > 0) continue;
                M_stranger_avg[i][0] = M_stranger_avg[i][0].add(cmpResult.multiply(C_ser).mod(THMDEM.System_T));
                M_stranger_avg[i][1] = M_stranger_avg[i][1].add(BigInteger.valueOf(1));
            }
        }
        //获取目标用户的已评分movieId
        List<Rating> userRatings = ratingRepository.findAllByUserId(userId);
        for (Rating rating:userRatings){
            movieIds.remove(rating.getMovieId());
        }


        //计算friend和stranger的推荐评分
        System.out.println("正在计算对用户"+ Objects.requireNonNull(userRepository.findById(userId).orElse(null)).getUsername()+"的推荐结果·······");
        stopWatch.start();
        int movieNumber = movieIds.size();
        BigInteger[][] M_friend = new BigInteger[movieNumber][2];
        int[] N_friend = new int[movieNumber];
        BigInteger[][] M_stranger = new BigInteger[movieNumber][2];
        int[] N_stranger = new int[movieNumber];
        for (int i = 0; i < movieNumber; i++) {
            M_friend[i][0] = BigInteger.valueOf(0);
            M_friend[i][1] = BigInteger.valueOf(1);
            N_friend[i] = 0;
            Long movieId = movieIds.get(i);
            //计算friend的推荐评分
            for (int j = 0;j<friendNumber;j++ ) {
                User friend = userRepository.findByUsername(friendships.get(j).getFriendName()).orElse(null);
                assert friend != null;
                Rating rating_friend = ratingRepository.findByUserIdAndAndMovieId(friend.getId(), movieId).orElse(null);
                if (rating_friend==null) continue; //判断电影movie[i]是否被该friend评过分。
                Map<String,String> mp_friend = new HashMap<>();
                mp_friend.put("C",rating_friend.getC().replace("\t",""));
                mp_friend.put("C_tag",rating_friend.getC_tag().replace("\t",""));
                mp_friend.put("C_ser",rating_friend.getC_ser());
                mp_friend.put("C_ser_tag",rating_friend.getC_ser_tag());
                mp_friend.put("C_csp",rating_friend.getC_csp());
                Map<String,BigInteger> result = THMDEM.KeySwitch(mp_friend, new BigInteger(friend.getP0().replace("\t","")),THMDEM.sk_ser,THMDEM.sk_csp);
                BigInteger C_ser = result.get("C_ser");
                BigInteger C_ser_tag = result.get("C_ser_tag");
                BigInteger cmpResult = THMDEM.Cmp(C_ser_tag, THMDEM.System_tag1, 1);
                BigInteger tmp = cmpResult.multiply(C_ser);
                BigInteger res = tmp.multiply(THMDEM.System_r.modInverse(THMDEM.System_N)).mod(THMDEM.System_N);
                if (res.compareTo(BigInteger.valueOf(5)) > 0) continue;
                tmp = tmp.multiply(M_friend_avg[j][1]).subtract(M_friend_avg[j][0]);
                M_friend[i][0] = M_friend[i][0].multiply(M_friend_avg[j][1]).add(M_friend[i][1].multiply(tmp)).mod(THMDEM.System_T);
                M_friend[i][1] = M_friend[i][1].multiply(M_friend_avg[j][1]);
                N_friend[i]++;
            }
            M_stranger[i][0] = BigInteger.valueOf(0);
            M_stranger[i][1] = BigInteger.valueOf(1);
            N_stranger[i] = 0;
            //计算Stranger推荐评分
            for (int j = 0; j < strangerNumber; j++) {
                User stranger = userRepository.findById(strangerId[j]).orElse(null);
                assert stranger!=null;
                Rating rating_stranger = ratingRepository.findByUserIdAndAndMovieId(strangerId[j],movieId).orElse(null);
                if (rating_stranger==null) continue;
                Map<String,String> mp_friend = new HashMap<>();
                mp_friend.put("C",rating_stranger.getC().replace("\t",""));
                mp_friend.put("C_tag",rating_stranger.getC_tag().replace("\t",""));
                mp_friend.put("C_ser",rating_stranger.getC_ser());
                mp_friend.put("C_ser_tag",rating_stranger.getC_ser_tag());
                mp_friend.put("C_csp",rating_stranger.getC_csp());
                Map<String,BigInteger> result = THMDEM.KeySwitch(mp_friend, new BigInteger(stranger.getP0().replace("\t","")),THMDEM.sk_ser,THMDEM.sk_csp);
                BigInteger C_ser = result.get("C_ser");
                BigInteger C_ser_tag = result.get("C_ser_tag");
                BigInteger cmpResult = THMDEM.Cmp(C_ser_tag, THMDEM.System_tag1, 1);
                BigInteger tmp = cmpResult.multiply(C_ser);
                BigInteger res = tmp.multiply(THMDEM.System_r.modInverse(THMDEM.System_N)).mod(THMDEM.System_N);
                if (res.compareTo(BigInteger.valueOf(5)) > 0) continue;
                tmp = tmp.multiply(M_stranger_avg[j][1]).subtract(M_stranger_avg[j][0]);
                M_stranger[i][0] = M_stranger[i][0].multiply(M_stranger_avg[j][1]).add(M_stranger[i][1].multiply(tmp));
                M_stranger[i][1] = M_stranger[i][1].multiply(M_stranger_avg[j][1]);
                N_stranger[i]++;
            }
            if (N_friend[i]==0) N_friend[i] = 1;
            if (N_stranger[i]==0) N_stranger[i] = 1;
            M_friend[i][1] = M_friend[i][1].multiply(BigInteger.valueOf(N_friend[i]));
            M_stranger[i][1] = M_stranger[i][1].multiply(BigInteger.valueOf(N_stranger[i]));
        }
        stopWatch.stop();
        System.out.printf("计算完成，总计耗时：%d 毫秒.%n", stopWatch.getTotalTimeMillis());
        BigInteger[] Cx = new BigInteger[movieNumber];
        BigInteger[] Cy = new BigInteger[movieNumber];

        List<Movie>  recoMovie = new ArrayList<>();
        for (int i = 0; i < movieNumber; i++) {
            Cy[i]= M_stranger[i][1].multiply(M_friend[i][1]).multiply(BigInteger.valueOf(11));
            Cx[i] = BigInteger.valueOf(10).multiply(M_friend[i][0].multiply(M_stranger[i][1]))
                    .add(BigInteger.valueOf(1).multiply(M_friend[i][1].multiply(M_stranger[i][0]))).mod(THMDEM.System_N);
            BigInteger r_ = THMDEM.System_r.modInverse(THMDEM.System_T);
            BigInteger x = Cx[i].multiply(r_).mod(THMDEM.System_T).mod(THMDEM.System_N);
            BigInteger res = x.divide(Cy[i]);
            long a = x.longValue();
            long b = Cy[i].longValue();
            double result = (double)a/ (double) b;
            if(result>=1.0){
                Movie nowMovie = movieRepository.findById(movieIds.get(i)).orElse(null);
                assert nowMovie != null;
                String str = String.format("%.1f",result);
                nowMovie.setRating(2+Double.parseDouble(str));
                recoMovie.add(nowMovie);
            }
        }
        Collections.sort(recoMovie);
        List<Movie> resMovie = new ArrayList<>();
        if (recoMovie.size()>18){
            for (int i = 0; i < 18; i++) {
                resMovie.add(recoMovie.get(i));
            }
        }else return recoMovie;

        return resMovie;
    }
}
