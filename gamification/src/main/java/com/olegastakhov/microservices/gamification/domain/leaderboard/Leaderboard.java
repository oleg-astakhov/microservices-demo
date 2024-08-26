package com.olegastakhov.microservices.gamification.domain.leaderboard;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Table(name = "leaderboard")
public class Leaderboard {
    @Id
    @Column(value = "pk")
    private Long pk;

    @Column(value = "user_reference_id")
    private String userReferenceId;

    @Column(value = "total_score")
    private Long totalScore;

    @Column(value = "version")
    private Integer version;

    @Column(value = "date_created")
    private ZonedDateTime dateCreated;

    @Column(value = "last_updated")
    private ZonedDateTime lastUpdated;


    public Long getPk() {
        return pk;
    }

    public Leaderboard setPk(Long pk) {
        this.pk = pk;
        return this;
    }

    public String getUserReferenceId() {
        return userReferenceId;
    }

    public Leaderboard setUserReferenceId(String userReferenceId) {
        this.userReferenceId = userReferenceId;
        return this;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public Leaderboard setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public Leaderboard setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public Leaderboard setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Leaderboard setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
