package com.crm.crm.controllers;

import com.crm.crm.dtos.AuthenticationRequest;
import com.crm.crm.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class.getName());

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
    }
    // @Autowired
    // private AuthenticationManager authenticationManager;
    // @Autowired
    // private JwtUtil jwtUtil;
    // @Autowired
    // private UserDetailsService userDetailsService;
    // private static final Logger LOG = LoggerFactory.getLogger(AuthController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest
    ) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()
                )
            );
        } catch (BadCredentialsException badCredentialsException) {
            LOG.error("Incorrect email or password");
            throw badCredentialsException;
        }

        LOG.debug("User {} has logged in", authenticationRequest.getEmail());

        // Response with 200
        return ResponseEntity.ok().build();
    }
}