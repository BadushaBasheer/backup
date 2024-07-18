package com.socialmedia.socialmedia.dto;

import com.socialmedia.socialmedia.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String password;

    private UserRole userRole;

}
