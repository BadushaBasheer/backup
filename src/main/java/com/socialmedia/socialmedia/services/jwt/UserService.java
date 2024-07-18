package com.socialmedia.socialmedia.services.jwt;

import com.socialmedia.socialmedia.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDetailsService userDetailService();

    List<User> allUsers();

    User findUserById(Long userId);

    List<User> searchUser(String query);

    User updateUser(User user);

    void deleteUser(Long userId);

    User followUser(Long userId1, Long userId2);

    Optional<User> getUserByEmail(String email);

    void blockUserById(Long id);

    void unBlockUserById(Long id);

}
