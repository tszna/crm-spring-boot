package com.crm.crm.controllers;

import com.crm.crm.entities.Absence;
import com.crm.crm.entities.TimeSession;
import com.crm.crm.entities.User;
import com.crm.crm.repositories.AbsenceRepository;
import com.crm.crm.repositories.TimeSessionRepository;
import com.crm.crm.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    @Autowired
    private TimeSessionRepository timeSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @GetMapping("/")
    public String home() {
        return "home/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            return "redirect:/time-tracker";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            return "redirect:/time-tracker";
        }
        return "auth/register";
    }

    @GetMapping("/time-tracker")
    public String timeTrackerPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("currentDate", currentDate);

        return "time_tracker/time_tracker";
    }

    @GetMapping("/weekly-summary")
    public String weeklySummaryPage(
            @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
            @RequestParam(value = "user_id", required = false) Long userId,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        User selectedUser;
        
        if (userId == null) {
            String email = authentication.getName();
            selectedUser = userRepository.findByEmail(email).orElse(null);
            if (selectedUser == null) {
                model.addAttribute("errorMessage", "Nie znaleziono użytkownika.");
                return "weekly_summary/weekly_summary";
            }
        } else {
            selectedUser = userRepository.findById(userId).orElse(null);
            if (selectedUser == null) {
                model.addAttribute("errorMessage", "Nie znaleziono użytkownika.");
                return "weekly_summary/weekly_summary";
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY).plusWeeks(weekOffset);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY).plusWeeks(weekOffset);

        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endOfWeekDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<TimeSession> sessions = timeSessionRepository.findByUserAndStartTimeBetweenOrderByStartTimeAsc(
                selectedUser, startOfWeekDateTime, endOfWeekDateTime);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        Map<String, Map<String, Object>> dailySummary = new LinkedHashMap<>();

        LocalDate dateIterator = startOfWeek;
        while (!dateIterator.isAfter(endOfWeek)) {
            String dateStr = dateIterator.format(dateFormatter);
            Map<String, Object> daySummary = new HashMap<>();
            daySummary.put("time", "-");
            daySummary.put("is_active", false);
            dailySummary.put(dateStr, daySummary);
            dateIterator = dateIterator.plusDays(1);
        }

        long weeklyTotalInSeconds = 0;

        Map<String, List<TimeSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(session -> session.getStartTime().toLocalDate().format(dateFormatter)));

        for (Map.Entry<String, List<TimeSession>> entry : sessionsByDate.entrySet()) {
            String sessionDateStr = entry.getKey();
            List<TimeSession> sessionList = entry.getValue();

            TimeSession lastSession = sessionList.get(sessionList.size() - 1);

            long dailyTotalInSeconds = 0;
            boolean isActive = false;

            if (lastSession.getFullElapsedTime() != null) {
                dailyTotalInSeconds = lastSession.getFullElapsedTime().toSecondOfDay();
                weeklyTotalInSeconds += dailyTotalInSeconds;
            } else {
                isActive = true;
            }

            Map<String, Object> summary = dailySummary.get(sessionDateStr);
            summary.put("time", dailyTotalInSeconds > 0 ? formatDuration(dailyTotalInSeconds) : "-");
            summary.put("is_active", isActive);
        }

        String weeklyTotal = formatDuration(weeklyTotalInSeconds);

        List<User> users = userRepository.findAll();

        model.addAttribute("dailySummary", dailySummary);
        model.addAttribute("weeklyTotal", weeklyTotal);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("users", users);
        model.addAttribute("selectedUserId", selectedUser.getId());

        return "weekly_summary/weekly_summary";
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    @GetMapping("/calendar")
    public String calendarPage(
            @RequestParam(value = "monthOffset", defaultValue = "0") int monthOffset,
            @RequestParam(value = "user_id", required = false) Long userId,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return "redirect:/login";
        }
    
        User selectedUser;
    
        if (userId == null) {
            String email = authentication.getName();
            selectedUser = userRepository.findByEmail(email).orElse(null);
            if (selectedUser == null) {
                model.addAttribute("errorMessage", "Nie znaleziono użytkownika.");
                return "calendar/calendar";
            }
        } else {
            selectedUser = userRepository.findById(userId).orElse(null);
            if (selectedUser == null) {
                model.addAttribute("errorMessage", "Nie znaleziono użytkownika.");
                return "calendar/calendar";
            }
        }
    
        LocalDate currentMonth = LocalDate.now().plusMonths(monthOffset);
        int daysInMonth = currentMonth.lengthOfMonth();
    
        Locale polishLocale = new Locale("pl", "PL");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", polishLocale);
        String formattedCurrentMonth = currentMonth.format(formatter);
    
        // Fetch user absences from current month
        LocalDate startOfMonth = currentMonth.withDayOfMonth(1);
        LocalDate endOfMonth = currentMonth.withDayOfMonth(daysInMonth);
    
        List<Absence> absences = absenceRepository.findByUserAndEndDateGreaterThanEqualAndStartDateLessThanEqual(
                selectedUser, startOfMonth, endOfMonth);
    
        Map<String, Map<String, Object>> calendar = new LinkedHashMap<>();
    
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            String dayKey = String.valueOf(day);
    
            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("day_of_week", date.getDayOfWeek().getDisplayName(TextStyle.FULL, polishLocale));
            dayInfo.put("status", "");
            dayInfo.put("is_today", date.equals(LocalDate.now()));
    
            calendar.put(dayKey, dayInfo);
        }
    
        for (Absence absence : absences) {
            int startDay = absence.getStartDate().isBefore(startOfMonth) ? 1 : absence.getStartDate().getDayOfMonth();
            int endDay = absence.getEndDate().isAfter(endOfMonth) ? daysInMonth : absence.getEndDate().getDayOfMonth();
            String letter = getAbsenceLetter(absence.getReason());
    
            for (int day = startDay; day <= endDay; day++) {
                String dayKey = String.valueOf(day);
                Map<String, Object> dayInfo = calendar.get(dayKey);
                if (dayInfo != null) {
                    dayInfo.put("status", letter);
                }
            }
        }
    
        List<User> users = userRepository.findAll();
    
        model.addAttribute("calendar", calendar);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("formattedCurrentMonth", formattedCurrentMonth);
        model.addAttribute("monthOffset", monthOffset);
        model.addAttribute("users", users);
        model.addAttribute("selectedUserId", selectedUser.getId());
    
        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.asMap().get("successMessage"));
        }
    
        return "calendar/calendar";
    }

    private String getAbsenceLetter(String reason) {
        switch (reason) {
            case "Urlop_zwykły":
                return "U - Urlop zwykły";
            case "Urlop_bezpłatny":
                return "Ub - Urlop bezpłatny";
            case "Nadwyżka":
                return "Nad - Wolne z nadwyżki";
            case "Praca_zdalna":
                return "Z - Praca zdalna";
            case "Delegacja":
                return "D - Delegacja";
            case "Choroba":
                return "C";
            default:
                return "Inny";
        }
    }

    

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
