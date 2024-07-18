package com.socialmedia.socialmedia.services.auth.impl;

import com.socialmedia.socialmedia.dto.LoginDTO;
import com.socialmedia.socialmedia.dto.RegisterDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.entities.Token;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.EmailTemplateName;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.TokenRepository;
import com.socialmedia.socialmedia.repositories.UserRepository;
import com.socialmedia.socialmedia.response.LoginResponse;
import com.socialmedia.socialmedia.services.auth.AuthService;
import com.socialmedia.socialmedia.services.auth.EmailService;
import com.socialmedia.socialmedia.services.jwt.UserService;
import com.socialmedia.socialmedia.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    private final EmailService emailService;

    private final UserService userService;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;


    @Override
    @Transactional
    public UserDTO registerUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setCreatedDate(LocalDateTime.now());
        user.setAccountLocked(false);
        user.setEnabled(false);

        User createdUser = userRepository.save(user);

        // Convert to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(createdUser.getId());
        userDTO.setName(createdUser.getUsername());
        userDTO.setEmail(createdUser.getEmail());
        userDTO.setPassword(createdUser.getPassword());
        userDTO.setUserRole(createdUser.getUserRole());

        sendValidationEmail(user);
        return userDTO;
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }


    @Override
    public LoginResponse authenticate(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        final UserDetails userDetails = userService.userDetailService().loadUserByUsername(loginDTO.getEmail());
        Optional<User> optionalUser = userRepository.findFirstByEmail(loginDTO.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return LoginResponse.builder()
                    .token(jwtToken)
                    .userId(user.getId())
                    .userRole(user.getUserRole())
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public void activateAccount(String token) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }


    private void sendValidationEmail(User user){
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
    //----------------------------------------

}
