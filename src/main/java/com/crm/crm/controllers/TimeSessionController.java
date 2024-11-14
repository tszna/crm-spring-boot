package com.crm.crm.controllers;

import com.crm.crm.entities.TimeSession;
import com.crm.crm.entities.User;
import com.crm.crm.repositories.TimeSessionRepository;
import com.crm.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/time")
public class TimeSessionController {

    @Autowired
    private TimeSessionRepository timeSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @PostMapping("/startSession")
    public ResponseEntity<Map<String, Object>> startSession() {
        // Fetch logged user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = optionalUser.get();

        // Get today's date
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Fetch existing TimeSession for today
        Optional<TimeSession> optionalTimeSession = timeSessionRepository
            .findFirstByUserAndStartTimeBetween(user, startOfDay, endOfDay);

        // Create new TimeSession
        TimeSession timeSessionNew = new TimeSession();
        timeSessionNew.setUser(user);
        timeSessionNew.setStartTime(LocalDateTime.now());
        timeSessionNew.setCreatedAt(LocalDateTime.now());
        timeSessionNew.setUpdatedAt(LocalDateTime.now());
        timeSessionRepository.save(timeSessionNew);

        // Determine start_time
        String startTime;
        if (optionalTimeSession.isPresent()) {
            startTime = optionalTimeSession.get().getStartTime().format(timeFormatter);
        } else {
            startTime = timeSessionNew.getStartTime().format(timeFormatter);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("start_time", startTime);
        response.put("count_time", calculateCountTime(user)); // Calculate sum of all sessions work time
        response.put("is_active", true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stopSession")
    public ResponseEntity<Map<String, String>> stopSession() {
        // Fetch logged user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = optionalUser.get();

        // Find last active session
        Optional<TimeSession> optionalActiveSession = timeSessionRepository.findFirstByUserAndEndTimeIsNullOrderByStartTimeDesc(user);

        if (optionalActiveSession.isPresent()) {
            TimeSession activeSession = optionalActiveSession.get();
            LocalDateTime endTime = LocalDateTime.now();

            // calculate session time duration
            Duration duration = Duration.between(activeSession.getStartTime(), endTime);
            long elapsedSeconds = duration.getSeconds();

            activeSession.setEndTime(endTime);
            activeSession.setElapsedTime(LocalTime.ofSecondOfDay(elapsedSeconds));
            activeSession.setUpdatedAt(LocalDateTime.now());

            // Calculate the total duration of all sessions
            long fullElapsedSeconds = calculateCountTime(user);

            activeSession.setFullElapsedTime(LocalTime.ofSecondOfDay(fullElapsedSeconds));
            timeSessionRepository.save(activeSession);

            Map<String, String> response = new HashMap<>();
            response.put("end_time", activeSession.getEndTime().format(timeFormatter));
            response.put("elapsed_time", activeSession.getFullElapsedTime().format(timeFormatter));

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "No active session found"));
    }

    @GetMapping("/getCurrentSession")
    public ResponseEntity<Map<String, Object>> getCurrentSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = optionalUser.get();

        //Fetch all sesions for today
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);

        List<TimeSession> timeSessions = timeSessionRepository.findByUserAndStartTimeBetweenOrderByStartTimeAsc(user, startOfDay, endOfDay);

        if (!timeSessions.isEmpty()) {
            // Calculate the total duration of all sessions
            long countTime = 0;
            boolean isActive = false;
            for (TimeSession session : timeSessions) {
                if (session.getEndTime() != null && session.getElapsedTime() != null) {
                    countTime += session.getElapsedTime().toSecondOfDay();
                } else if (session.getEndTime() == null) {
                    // If active session, add curent time of lasting session
                    Duration duration = Duration.between(session.getStartTime(), LocalDateTime.now());
                    countTime += duration.getSeconds();
                    isActive = true;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("start_time", timeSessions.get(0).getStartTime().format(timeFormatter));
            TimeSession lastSession = timeSessions.get(timeSessions.size() - 1);
            response.put("end_time", lastSession.getEndTime() != null ? lastSession.getEndTime().format(timeFormatter) : null);
            response.put("elapsed_time", formatDuration(countTime));
            response.put("count_time", countTime);
            response.put("is_active", isActive);

            return ResponseEntity.ok(response);
        }

        // If there is no session
        Map<String, Object> response = new HashMap<>();
        response.put("is_active", false);
        response.put("count_time", 0);
        return ResponseEntity.ok(response);
    }

    private long calculateCountTime(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);

        List<TimeSession> timeSessions = timeSessionRepository.findByUserAndStartTimeBetweenOrderByStartTimeAsc(user, startOfDay, endOfDay);

        long countTime = 0;
        for (TimeSession session : timeSessions) {
            if (session.getEndTime() != null && session.getElapsedTime() != null) {
                countTime += session.getElapsedTime().toSecondOfDay();
            } else if (session.getEndTime() == null) {
                Duration duration = Duration.between(session.getStartTime(), LocalDateTime.now());
                countTime += duration.getSeconds();
            }
        }

        return countTime;
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
