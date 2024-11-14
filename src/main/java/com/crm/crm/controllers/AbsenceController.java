package com.crm.crm.controllers;

import com.crm.crm.entities.Absence;
import com.crm.crm.entities.User;
import com.crm.crm.repositories.AbsenceRepository;
import com.crm.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/calendar")
public class AbsenceController {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/create")
    public String createAbsencePage(Model model) {
        model.addAttribute("absence", new Absence());
        model.addAttribute("currentPage", "createAbsence");
        return "calendar/create_absence";
    }

    @PostMapping("/store")
    public String storeAbsence(
            @Valid @ModelAttribute("absence") Absence absence,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList()));
            return "calendar/create_absence";
        }

        // Dates validation
        if (absence.getStartDate() == null || absence.getEndDate() == null ||
                absence.getEndDate().isBefore(absence.getStartDate())) {
            bindingResult.rejectValue("endDate", "error.absence", "Data końcowa musi być po lub równa dacie początkowej");
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList()));
            return "calendar/create_absence";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();

        absence.setUser(user);
        absence.setCreatedAt(LocalDateTime.now());
        absence.setUpdatedAt(LocalDateTime.now());

        absenceRepository.save(absence);

        redirectAttributes.addFlashAttribute("successMessage", "Nieobecność została dodana.");
        return "redirect:/calendar";
    }
}
