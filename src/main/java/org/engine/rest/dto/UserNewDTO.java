package org.engine.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserNewDTO {

	private Integer id;
   
    private String login;
        
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String role;

    private Boolean enabled;	
    
    private Integer ownerId;
    
    private String ownerType;	
    
    private LocalDateTime lastActivityAt;	
    
    private LocalDateTime createdAt;
}
