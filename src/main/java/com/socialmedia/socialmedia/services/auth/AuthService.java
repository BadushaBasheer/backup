package com.socialmedia.socialmedia.services.auth;

import com.socialmedia.socialmedia.dto.LoginDTO;
import com.socialmedia.socialmedia.dto.RegisterDTO;
import com.socialmedia.socialmedia.dto.UserDTO;
import com.socialmedia.socialmedia.response.LoginResponse;

public interface AuthService {

    UserDTO registerUser(RegisterDTO registerDTO);

    boolean hasUserWithEmail(String email);

    LoginResponse authenticate(LoginDTO loginDTO);

    void activateAccount(String token);
}
