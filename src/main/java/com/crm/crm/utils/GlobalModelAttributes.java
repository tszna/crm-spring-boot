package com.crm.crm.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.crm.crm.controllers")
public class GlobalModelAttributes {

    @ModelAttribute
    public void addAuthenticatedAttribute(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = isAuthenticated(authentication);
        model.addAttribute("authenticated", isAuthenticated);

        String currentUri = request.getRequestURI();

        String currentPage = "";
        if (currentUri.startsWith("/time-tracker")) {
            currentPage = "time-tracker";
        } else if (currentUri.startsWith("/weekly-summary")) {
            currentPage = "weekly-summary";
        } else if (currentUri.startsWith("/calendar")) {
            currentPage = "calendar";
        } else if (currentUri.startsWith("/login")) {
            currentPage = "login";
        } else if (currentUri.startsWith("/register")) {
            currentPage = "register";
        }
        model.addAttribute("currentPage", currentPage);
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}