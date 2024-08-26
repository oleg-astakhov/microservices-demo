package com.olegastakhov.microservices.gamification.domain.score;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Table(name = "score")
public class Score {
    @Id
    @Column(value = "pk")
    private Long pk;

    @Column(value = "user_reference_id")
    private String userReferenceId;

    @Column(value = "attempt_reference_id")
    private String attemptReferenceId;

    @Column(value = "value")
    private Long value;

    @Column(value = "version")
    private Integer version;

    @Column(value = "date_created")
    private ZonedDateTime dateCreated;

    @Column(value = "last_updated")
    private ZonedDateTime lastUpdated;


    public Long getPk() {
        return pk;
    }

    public Score setPk(Long pk) {
        this.pk = pk;
        return this;
    }

    public String getUserReferenceId() {
        return userReferenceId;
    }

    public Score setUserReferenceId(String userReferenceId) {
        this.userReferenceId = userReferenceId;
        return this;
    }

    public String getAttemptReferenceId() {
        return attemptReferenceId;
    }

    public Score setAttemptReferenceId(String attemptReferenceId) {
        this.attemptReferenceId = attemptReferenceId;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public Score setValue(Long value) {
        this.value = value;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public Score setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public Score setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Score setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
