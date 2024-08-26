package com.olegastakhov.microservices.quiz.command.getusers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class GetUsersRepository {
    @PersistenceContext
    private EntityManager em;

    public List<GetUsersModel> listUsers(final List<String> userRefs) {
        if (userRefs.isEmpty()) {
            return Collections.emptyList();
        }
        return em.createQuery("""
                            SELECT 
                            au.pk as pk,
                            au.username as username,
                            au.referenceId as referenceId 
                            FROM AppUser au 
                            WHERE 1=1
                            AND au.referenceId IN (:pUserRefs)
                        """)
                .setParameter("pUserRefs", userRefs)
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.aliasToBean(GetUsersModel.class)).getResultList();
    }
}
