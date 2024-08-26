package com.olegastakhov.microservices.quiz.domain.user;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name="app_user")
@SequenceGenerator(name = "app_user__seq_generator", sequenceName = "app_user__seq", allocationSize = 1)
public class AppUser {
    @Id
    @GeneratedValue(generator = "app_user__seq_generator", strategy = GenerationType.SEQUENCE)
    private Long pk;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "date_created", nullable = false)
    private ZonedDateTime dateCreated;

    @Column(name = "last_updated")
    private ZonedDateTime lastUpdated;
    @Version
    private Integer version;

    public Long getPk() {
        return pk;
    }

    public AppUser setPk(Long pk) {
        this.pk = pk;
        return this;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public AppUser setReferenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AppUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public AppUser setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public AppUser setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public AppUser setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
