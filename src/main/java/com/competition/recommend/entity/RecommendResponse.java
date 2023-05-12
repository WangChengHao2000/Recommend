package com.competition.recommend.entity;

import lombok.Data;

@Data
public class RecommendResponse<T> {

    private RecommendStatus status;
    private T data;

    public RecommendResponse(RecommendStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public RecommendResponse(T data, RecommendStatus status) {
        this.data = data;
        this.status = status;
    }

}
