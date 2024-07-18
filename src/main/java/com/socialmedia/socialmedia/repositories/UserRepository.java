package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUserRole(UserRole userRole);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findByEmail(String email);

    User findUserById(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String mail);

    List<User> findByUserRoleNot(UserRole userRole);

    List<User> findByUsernameContainingOrEmailContaining(String username, String Email);

}
