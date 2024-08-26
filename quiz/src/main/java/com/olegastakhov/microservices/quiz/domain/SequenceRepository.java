package com.olegastakhov.microservices.quiz.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceRepository {
    @PersistenceContext
    private EntityManager em;

    /**
     *  Arbitrary sequence to be used for generation of
     *  unique user references (not primary key). See
     *  usage of this method for more info.
     */
    public Long getNextUserReferenceId() {
        final Query query = em.createNativeQuery("""
                    SELECT NEXTVAL('user_reference_id__seq')
                """);
        return (Long) query.getSingleResult();
    }
}
