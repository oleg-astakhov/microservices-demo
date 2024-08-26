String.prototype.isBlank = function() {
    return (this.length === 0 || !this.trim());
};


$.hasValue = function(elem) {
    return !(undefined === elem || null === elem);
};

$.fn.exists = function(){ return this.length > 0; }; 

$(document).ready(function () {
    $("#dark-toggle").click(function(event) {
        var elems = $("[data-darkable]");
        elems.each(function() {
            $(this).toggleClass("dark");
        });
    });

    var options = $("#options");
    var question = $("#question");
    var submit = $("#submit-quiz");
    var quizWidget = $("#quiz-widget");
    var quizQuestionPanel = $("#quiz-question-panel");
    var quizErrorPanel = $("#quiz-error-panel");
    var quizErrorPanelMsg = $("#quiz-error-panel-msg");
    var quizAttemptRecovery = $("#quiz-attempt-recovery");
    var sideWidgets = $("#side-widgets");
    var personalStats = $("#personal-stats");
    var personalStatsHeader = $("#personal-stats-header");
    var personalStatsData = $("#personal-stats-data");
    var leaderboard = $("#leaderboard");
    var leaderboardHeader = $("#leaderboard-header");
    var leaderboardData = $("#leaderboard-data");
    var messageBox = $("#msg-box");
    var username = $("#username");
    var quizInputDataPanel = $("#quiz-input-data-panel");
    var quizPostSubmitPanel = $("#quiz-post-submit-panel");
    var quizPostSubmitMsg = $("#quiz-post-submit-msg");
    var quizPostSubmitIcon = $("#quiz-post-submit-icon");
    var nextQuestionBtn = $("#next-question");

    var handleAjaxResponse = function(data, statusCode, errorThrown) {
        if (errorThrown) {
            console.error(errorThrown);
        }

        var stringConstructor = "test".constructor;

        var json = {};
        if (data.constructor === stringConstructor) {
            if (!data.isBlank()) {
                try {
                    json = jQuery.parseJSON(data)
                } catch (e) {
                    console.error(e);
                }
            }
        } else {
            json = data;
        }

        var message = json['message'];
        messageBox.removeClass();
        messageBox.addClass("msg-box error");
        if ($.hasValue(message)) {
            messageBox.text(message);
        } else {
            messageBox.text("Ouch, we are sorry, something went wrong. We have been notified about this incident and our engineers are on it. Please try again at a later time or contact our tech support if the problem persists for a long time.");
        }
        messageBox.show();
    }

    var jqAjaxErrorFunctionForWidgets = function (_5xxErrorHandler) {
        return function (jqXHR, textStatus, errorThrown) {
            var data = (jqXHR.responseJSON ? jqXHR.responseJSON : jqXHR.responseText);
            if (jqXHR.status >= 500 && jqXHR.status <= 599) {
                _5xxErrorHandler();
            } else {
                handleAjaxResponse(data, jqXHR.status, errorThrown);
            }
        }
    }

    var getNextQuestion = function(){
        $.ajax({url: "/quiz/next-question",
            success: function(result){
                quizErrorPanel.hide();
                options.hide();
                options.empty();
                question.text(result.data.question);
                var idx=0;
                $.each(result.data.options, function(index, item) {
                    // Create a list item
                    var listItem = $('<div class="quiz-option white-space-no-wrap"></div>');
                    var currId = "option" + (++idx);
                    var radio = $('<input type="radio" name="quiz-answer">')
                    radio.attr("id", currId);
                    radio.attr("value", item.valueForBackend);
                    radio.attr("data-question-id", result.data.questionId);
                    radio.attr("data-question-item-id", result.data.questionItemId);
                    username.attr("placeholder", result.data.usernameInputPlaceholderCaption);

                    submit.text(result.data.submitAnswerButtonCaption)

                    var label = $('<label class="click-elem-resp-padding"></label>');
                    label.attr('for', currId);
                    label.text(item.displayValue);
                    listItem.append(radio);
                    listItem.append(label);
                    options.append(listItem);
                    options.show();
                    quizPostSubmitPanel.hide();
                    quizQuestionPanel.show();
                    quizInputDataPanel.show();
                    quizWidget.show();
                });
            },
            error: jqAjaxErrorFunctionForWidgets(_5xxHandlerForQuiz),
        });
    };

    var _5xxHandlerForQuiz = function () {
        quizErrorPanelMsg.empty();
        quizErrorPanelMsg.text("Sorry, quiz is currently unavailable.");
        quizErrorPanel.show();
        quizQuestionPanel.hide();
        quizPostSubmitPanel.hide();
        quizWidget.show();
    }

    getNextQuestion();


    var submitAnswer = function(){
        messageBox.hide();
        var dataToSend = {};
        dataToSend.username = username.val();
        var answer = $('input[name="quiz-answer"]:checked');

        if (answer.exists()) {
            dataToSend.answer = answer.val();
            dataToSend.questionId = answer.attr("data-question-id");
            dataToSend.questionItemId = answer.attr("data-question-item-id");
        }

        $.ajax({
            type: "POST",
            url: "/quiz/submit-answer",
            dataType: "json",
            data: JSON.stringify(dataToSend),
            contentType: "application/json",
            success: function(result){
                quizInputDataPanel.hide();
                quizPostSubmitMsg.text(result.data.motivationMessage);
                if (result.data.correct) {
                    quizPostSubmitPanel.removeClass("wrong");
                    quizPostSubmitPanel.addClass("correct");
                    quizPostSubmitIcon.removeClass("icon-baffled2");
                    quizPostSubmitIcon.addClass("icon-cool2");
                    nextQuestionBtn.hide();
                    setTimeout(function() {
                        getNextQuestion();
                        getPersonalStats();
                        getLeaderboard();
                    }, 1200);
                } else {
                    quizPostSubmitPanel.addClass("correct");
                    quizPostSubmitPanel.addClass("wrong");
                    quizPostSubmitIcon.addClass("icon-baffled2");
                    quizPostSubmitIcon.removeClass("icon-cool2");
                    nextQuestionBtn.text(result.data.nextQuestionButtonCaption);
                    nextQuestionBtn.show();
                }
                quizPostSubmitPanel.show();
            },
            error: jqAjaxErrorFunctionForWidgets(_5xxHandlerForQuiz),
        });
    };
    submit.click(function(event) {
        submitAnswer();
    });

    quizAttemptRecovery.click(function(event) {
        getNextQuestion();
    });

    nextQuestionBtn.click(function(event) {
        getNextQuestion();
        getPersonalStats();
        getLeaderboard();
    });


    var getPersonalStats = function(){
        $.ajax({url: "/quiz/personal-stats",
            data: {username : username.val()},
            contentType: "application/json",
            success: function(result){
                var table = $('<div class="table"></div>');
                $.each(result.data.stats, function(index, item) {
                    // Create a list item
                    var row = $('<div class="table-row"></div>');
                    var cell1 = $('<div class="table-cell"></div>');
                    var cell2 = $('<div class="table-cell"></div>');
                    if (item.correct) {
                        cell1.append($('<span class="icon icon-checkmark4 correct"></span>'));
                    } else {
                        cell1.append($('<span class="icon icon-cross2 wrong"></span>'));
                    }
                    cell2.text(item.question);
                    row.append(cell1);
                    row.append(cell2);
                    table.append(row);
                });
                personalStatsHeader.text(result.data.headerText);
                personalStatsData.empty();
                personalStatsData.append(table);
                personalStats.show();
                sideWidgets.show();
            },
            error: jqAjaxErrorFunctionForWidgets(_5xxHandlerForPersonalStats),
        });
    };

    var _5xxHandlerForPersonalStats = function () {
        personalStatsData.empty();
        personalStatsData.text("Sorry, history data is currently unavailable.");
        personalStats.show();
    }


    var getLeaderboard = function(){
        $.ajax({url: "/leaderboard/list",
            contentType: "application/json",
            success: function(result){
                var data = result.data;
                if (data.leaders.length === 0) {
                    return;
                }
                var table = $('<div class="table"></div>');

                var header = $('<div class="table-row"></div>');
                var cell1Title = $('<div class="table-cell header"></div>');
                var cell2Title = $('<div class="table-cell header"></div>');
                cell1Title.text(data.scoreColumnTitle);
                cell2Title.text(data.usernameColumnTitle);
                header.append(cell1Title);
                header.append(cell2Title);
                table.append(header);

                var userRefs = $.map(data.leaders, function(obj) {
                    return obj.userReferenceId;
                });

                getUsers(userRefs).done(function(usersResult, textStatus, jqXHR ) {
                    var usersData = usersResult.data;

                    $.each(data.leaders, function(index, item) {
                        // Create a list item
                        var row = $('<div class="table-row"></div>');
                        var cell1 = $('<div class="table-cell"></div>');
                        var cell2 = $('<div class="table-cell"></div>');

                        cell1.text(item.totalScore);
                        cell2.text(usersData.refToUser[item.userReferenceId].username);

                        row.append(cell1);
                        row.append(cell2);
                        table.append(row);
                    });
                    leaderboardHeader.text(data.headerText);
                    leaderboardData.empty();
                    leaderboardData.append(table);
                    leaderboard.show();
                }).fail(jqAjaxErrorFunctionForWidgets(_5xxHandlerForLeaderboard));
            },
            error: jqAjaxErrorFunctionForWidgets(_5xxHandlerForLeaderboard)
        });
    };

    var _5xxHandlerForLeaderboard = function () {
        leaderboardData.empty();
        leaderboardData.text("Sorry, leaderboard data is currently unavailable.");
        leaderboard.show();
    }

    var getUsers = function(userRefs){
        var ajaxConfigObject = {
            contentType: "application/json"
        }
        ajaxConfigObject.url = "/quiz/users?refs=" + userRefs.join(",");
        return $.ajax(ajaxConfigObject);
    };
});