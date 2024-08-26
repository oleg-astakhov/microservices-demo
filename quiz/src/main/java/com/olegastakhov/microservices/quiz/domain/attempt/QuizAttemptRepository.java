package com.olegastakhov.microservices.quiz.domain.attempt;

import org.springframework.data.repository.CrudRepository;


public interface QuizAttemptRepository extends CrudRepository<QuizAttempt, Long> {

}