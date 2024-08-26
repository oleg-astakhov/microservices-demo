package com.olegastakhov.microservices.quiz.domain.attempt;

import com.olegastakhov.microservices.quiz.domain.user.AppUser;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name="quiz_attempt")
@SequenceGenerator(name = "quiz_attempt__seq_generator", sequenceName = "quiz_attempt__seq", allocationSize = 1)
public class QuizAttempt {

    @Id
    @GeneratedValue(generator = "quiz_attempt__seq_generator", strategy = GenerationType.SEQUENCE)
    private Long pk;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_user_fk", nullable = false)
    private AppUser user;

    @Column(name = "correct", nullable = false)
    private boolean correct;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "question_item_id", nullable = false)
    private String questionItemId;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "user_answer", nullable = false)
    private String userAnswer;

    @Column(name = "date_created", nullable = false)
    private ZonedDateTime dateCreated;

    @Column(name = "last_updated")
    private ZonedDateTime lastUpdated;

    @Version
    private Integer version;

    public Long getPk() {
        return pk;
    }

    public QuizAttempt setPk(Long pk) {
        this.pk = pk;
        return this;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public QuizAttempt setReferenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public AppUser getUser() {
        return user;
    }

    public QuizAttempt setUser(AppUser user) {
        this.user = user;
        return this;
    }

    public boolean isCorrect() {
        return correct;
    }

    public QuizAttempt setCorrect(boolean correct) {
        this.correct = correct;
        return this;
    }

    public String getQuestionId() {
        return questionId;
    }

    public QuizAttempt setQuestionId(String questionId) {
        this.questionId = questionId;
        return this;
    }

    public String getQuestionItemId() {
        return questionItemId;
    }

    public QuizAttempt setQuestionItemId(String questionItemId) {
        this.questionItemId = questionItemId;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public QuizAttempt setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public QuizAttempt setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
        return this;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public QuizAttempt setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        return this;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public QuizAttempt setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public QuizAttempt setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public QuizAttempt setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
