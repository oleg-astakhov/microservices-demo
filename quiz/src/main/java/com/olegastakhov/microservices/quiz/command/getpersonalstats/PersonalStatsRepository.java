package com.olegastakhov.microservices.quiz.command.getpersonalstats;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class PersonalStatsRepository {
    @PersistenceContext
    private EntityManager em;

    public List<PersonalStatsModel> listPersonalStats(final Long userPk) {
        return em.createQuery("""
                            SELECT 
                            qa.pk as pk,
                            qa.question as question,
                            qa.correct as correct
                            FROM QuizAttempt qa 
                            WHERE 1=1
                            AND qa.user.id = :pUserPk
                            ORDER BY
                            qa.pk DESC
                        """)
                .setParameter("pUserPk", userPk)
                .setMaxResults(5)
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(PersonalStatsModel.class)).getResultList();
    }
}
