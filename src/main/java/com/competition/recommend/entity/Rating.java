package com.competition.recommend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long movieId;

    private String title;

    private String rating;

    private String C;
    private String C_tag;
    private String C_ser;
    private String C_ser_tag;
    private String C_csp;

}
