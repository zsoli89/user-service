package hu.webuni.userservice.model.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String username;
    private String password;
    private String fbToken;
}
