package com.competition.recommend.entity;


import lombok.Data;

import java.util.List;

@Data
public class FriendandMovie {
    private Long userId;
    private Long friendId;
    private String friendName;
    private int relation;
    private List<Movie> movies;
}
