package com.competition.recommend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Movie implements Comparable<Movie>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String date;

    private double avg;

    private int count;

    private Integer rating;

    @Override
    public int compareTo(Movie o) {
        return o.getRating()-this.getRating();
    }
}
