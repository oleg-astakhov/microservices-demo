package com.olegastakhov.microservices.quiz.command.getnextquestion;

import java.util.List;

public record QuizDTO(String questionId,
                      String questionItemId,
                      String question,
                      List<QuizOptionDTO> options,
                      String submitAnswerButtonCaption,
                      String usernameInputPlaceholderCaption) {
}
