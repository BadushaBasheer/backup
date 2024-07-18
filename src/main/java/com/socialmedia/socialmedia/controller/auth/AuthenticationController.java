package com.socialmedia.socialmedia.controller.auth;

import com.socialmedia.socialmedia.dto.LoginDTO;
import com.socialmedia.socialmedia.dto.RegisterDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.response.LoginResponse;
import com.socialmedia.socialmedia.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public LoginResponse authenticate(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }
        return authService.authenticate(loginDTO);

    }

    @PostMapping("/register")
    public ResponseEntity<?> signUpUser(@RequestBody @Valid RegisterDTO registerDTO) {
        if (authService.hasUserWithEmail(registerDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exist with this email");
        }
        UserDTO createdUser = authService.registerUser(registerDTO);
        if (createdUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created, please try again later!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) {
        authService.activateAccount(token);
    }

}
