package com.competition.recommend.service.impl;

import com.competition.recommend.entity.Friendship;
import com.competition.recommend.entity.Movie;
import com.competition.recommend.entity.Rating;
import com.competition.recommend.repository.FriendshipRepository;
import com.competition.recommend.repository.MovieRepository;
import com.competition.recommend.repository.RatingRepository;
import com.competition.recommend.repository.UserRepository;
import com.competition.recommend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Page<Movie> getAllMoviesByPage(int page, int size) {
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
        for(int i =0;i<friendNumber;i++){
            Long friendId = friendships.get(i).getFriendId();
            List<Rating> ratings = ratingRepository.findAllByUserId(friendId);
            M_friend_avg[i][1] = BigInteger.valueOf(ratings.size());
            for(int j = 0;j<ratings.size();j++){
                M_friend_avg[i][0] = M_friend_avg[i][0].add(new BigInteger(ratings.get(j).getRating()));
            }
        }
        int strangerNumber = strangerId.length;
        BigInteger[][] M_stranger_avg = new BigInteger[strangerNumber][2];
        for(int i =0;i<strangerNumber;i++){
            List<Rating> ratings = ratingRepository.findAllByUserId(strangerId[i]);
            M_stranger_avg[i][1] = BigInteger.valueOf(ratings.size());
            for(int j = 0;j<ratings.size();j++){
                M_stranger_avg[i][0] = M_stranger_avg[i][0].add(new BigInteger(ratings.get(j).getRating()));
            }
        }


        return null;
    }
}
