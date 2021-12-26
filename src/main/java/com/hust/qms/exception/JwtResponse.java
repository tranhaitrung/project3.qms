package com.hust.qms.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String avatar;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, String displayName, String avatar, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.displayName = displayName;
        this.avatar = avatar;
    }
}
