package com.competition.recommend.entity;

public enum RecommendStatus {

    SUCCESS(200),
    FAILURE(500),
    NOT_FOUND(404),
    BAD_REQUEST(400),
    UNAUTHORIZED(401);

    private int statusCode;

    RecommendStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
