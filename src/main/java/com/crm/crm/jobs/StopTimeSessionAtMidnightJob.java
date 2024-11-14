package com.crm.crm.jobs;

import com.crm.crm.entities.TimeSession;
import com.crm.crm.repositories.TimeSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.*;
import java.util.List;

@Component
public class StopTimeSessionAtMidnightJob {

    @Autowired
    private TimeSessionRepository timeSessionRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void stopTimeSessionsAtMidnight() {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime midnight = LocalDate.now(zoneId).atTime(LocalTime.MAX);
        LocalDateTime previousDayMidnight = midnight.minusDays(1);

        List<TimeSession> timeSessions = timeSessionRepository.findAllByOrderByUserId();

        long totalSecondsSum = 0;
        Long previousUserId = null;

        for (TimeSession session : timeSessions) {
            Long currentUserId = session.getUser().getId();

            if (!currentUserId.equals(previousUserId)) {
                totalSecondsSum = 0;
                previousUserId = currentUserId;
            }

            if (session.getEndTime() != null) {
                LocalTime elapsedTime = session.getElapsedTime();
                if (elapsedTime != null) {
                    totalSecondsSum += elapsedTime.toSecondOfDay();
                }
            } else {
                LocalDateTime startTime = session.getStartTime();
                long differenceInSeconds = Duration.between(startTime, previousDayMidnight).getSeconds();
                differenceInSeconds += totalSecondsSum;

                LocalTime elapsedTime = LocalTime.ofSecondOfDay(Duration.between(startTime, previousDayMidnight).getSeconds());
                LocalTime fullElapsedTime = LocalTime.ofSecondOfDay(differenceInSeconds);

                session.setEndTime(previousDayMidnight);
                session.setElapsedTime(elapsedTime);
                session.setFullElapsedTime(fullElapsedTime);

                timeSessionRepository.save(session);

                System.out.println("Zaktualizowano sesję dla użytkownika ID: " + currentUserId);
            }
        }
    }
}
