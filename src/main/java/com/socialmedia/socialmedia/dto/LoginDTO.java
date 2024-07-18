package com.socialmedia.socialmedia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {


    @NotBlank(message = "Email should not be blank")
    @NotNull
    private String email;

    @NotBlank(message = "Password should not be blank")
    @NotNull
    private String password;

}
