package com.taboola.hackathon.frontend.api;

import java.util.Objects;

public class Recommendation {

    private String userId;
    private String recommendation;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return userId.equals(that.userId) &&
                recommendation.equals(that.recommendation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, recommendation);
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "userId='" + userId + '\'' +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }

}
