package com.socialmedia.socialmedia.services.jwt;

import com.socialmedia.socialmedia.entities.Follower;
import com.socialmedia.socialmedia.entities.Following;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.UserRole;
import com.socialmedia.socialmedia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetailsService userDetailService() {
        return email -> userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public User findUserById(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("User with ID: " + userId + " is an admin and access is restricted.");
        }
        return user;
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .collect(Collectors.toList());
    }


    @Override
    public List<User> searchUser(String query) {
        return userRepository.findByUsernameContainingOrEmailContaining(query, query).stream()
                .filter(user -> user.getUserRole() != UserRole.ADMIN)
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findUserByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + user.getEmail()));
        return updateExistingUser(user, existingUser);
    }

    private User updateExistingUser(User user, User existingUser) {
        existingUser.setUsername(user.getUsername());
        if (!existingUser.getEmail().equals(user.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }
        if (!bCryptPasswordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        if (user.getUserRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot delete an admin user.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User followUser(Long userId1, Long userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);

        Following following = new Following();
        following.setUser(user1);
        following.setFollowingId(user2.getId());

        Follower follower = new Follower();
        follower.setUser(user2);
        follower.setFollowerId(user1.getId());

        user1.getFollowing().add(following);
        user2.getFollowers().add(follower);

        userRepository.save(user1);
        userRepository.save(user2);

        return user1;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    @Transactional
    public void blockUserById(Long id) {
        User user = userRepository.findUserById(id);
        if (user != null) {
            user.setEnabled(false);
            userRepository.save(user);
            log.info("User with id {} has been blocked", id);
        } else {
            log.warn("User with id {} not found", id);
        }
    }

    @Override
    @Transactional
    public void unBlockUserById(Long id) {
        User user = userRepository.findUserById(id);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
            log.info("User with id {} has been unblocked", id);
        } else {
            log.warn("User with id {} not found", id);
        }
    }
}
