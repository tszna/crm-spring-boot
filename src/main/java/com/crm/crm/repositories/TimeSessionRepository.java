package com.crm.crm.repositories;

import com.crm.crm.entities.TimeSession;
import com.crm.crm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSessionRepository extends JpaRepository<TimeSession, Long> {

    List<TimeSession> findByUserAndStartTimeBetweenOrderByStartTimeAsc(
        User user, LocalDateTime start, LocalDateTime end);

    Optional<TimeSession> findFirstByUserAndEndTimeIsNullOrderByStartTimeDesc(User user);

    List<TimeSession> findByUserAndEndTimeIsNull(User user);

    Optional<TimeSession> findFirstByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    List<TimeSession> findAllByOrderByUserId();
}
