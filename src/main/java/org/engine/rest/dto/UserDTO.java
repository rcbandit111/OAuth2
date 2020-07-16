package org.engine.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {

	private Integer id;

	private String login;

	private String firstName;

	private String lastName;

	private String email;

	private String role;

	private Boolean enabled;

	private String lastActivityAt;

	private String createdAt;
}
