package com.olegastakhov.microservices.quiz.service;

import com.olegastakhov.microservices.quiz.domain.SequenceRepository;
import com.olegastakhov.microservices.quiz.domain.user.AppUser;
import com.olegastakhov.microservices.quiz.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SequenceRepository sequenceRepository;

    public AppUser findOrCreate(final String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(buildEntity(username)));
    }

    private AppUser buildEntity(final String username) {
        return new AppUser()
                .setUsername(username)
                .setDateCreated(ZonedDateTime.now())
                .setReferenceId(getReferenceId());
    }

    /**
     * User reference ids are for external needs (for frontend,
     * for other microservices, etc.).
     *
     * Why not "UUID.randomUUID().toString()"?
     * Because "UREF1005"
     * is friendlier and easier to work with than
     * "f59bed8a-2bad-4f5b-a0b2-19703c5fd18g",
     * especially if this reference is used on invoices
     * or any other place visible to the customer, and
     * especially if this value needs to be entered
     * manually somewhere.
     */

    private String getReferenceId() {
        return "UREF" + sequenceRepository.getNextUserReferenceId();
    }

}
