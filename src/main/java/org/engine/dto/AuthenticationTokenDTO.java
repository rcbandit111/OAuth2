package org.engine.dto;

import lombok.*;

import java.util.List;

/**
 * Used in JWT token successful response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuthenticationTokenDTO {

    // Server Response data

    private String accessToken;

    private String tokenType;

    private List<String> role;

    private Long expiresIn;
}
